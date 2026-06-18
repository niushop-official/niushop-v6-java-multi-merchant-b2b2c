package com.niu.mall.service.core.delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.mapper.sys.SysAreaMapper;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.consts.ShopDeliveryElectronicSheetConst;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.entity.delivery.DeliveryElectronicSheet;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDelivery;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.core.entity.site.SiteAddress;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.core.mapper.delivery.DeliveryElectronicSheetMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.order.MallOrderDeliveryMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.mall.service.core.delivery.ICoreElectronicSheetService;
import com.niu.core.service.core.delivery.engine.ApplyParam;
import com.niu.core.service.core.delivery.engine.ElectronicSheetFactory;
import com.niu.core.service.core.delivery.param.ElectronicSheetInfoSearchParam;
import com.niu.core.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.core.service.core.delivery.vo.ElectronicSheetConfigValueVo;
import com.niu.core.service.core.delivery.vo.ElectronicSheetPrintVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreElectronicSheetServiceImpl implements ICoreElectronicSheetService {

    private final SysAreaMapper sysAreaMapper;
    private final MallOrderMapper mallOrderMapper;
    private final SiteAddressMapper shopAddressMapper;
    private final ICoreConfigService coreConfigService;
    private final MallOrderGoodsMapper orderGoodsMapper;
    private final MallGoodsSkuMapper mallGoodsSkuMapper;
    private final MallOrderDeliveryMapper mallOrderDeliveryMapper;
    private final DeliveryCompanyMapper deliveryCompanyMapper;
    private final DeliveryElectronicSheetMapper deliveryElectronicSheetMapper;

    @Override
    public List<ElectronicSheetPrintVo> printElectronicSheet(ShopDeliveryPrintElectronicSheetParam param) {
        return new ElectronicSheetPrinter(param).print();
    }

    private List<ElectronicSheetInfo> getInfo(ElectronicSheetInfoSearchParam param) {
        var sheetQueryWrapper = new LambdaQueryWrapper<DeliveryElectronicSheet>()
                .eq(DeliveryElectronicSheet::getSiteId, param.getSiteId());

        if (param.getIsDefault() != null && param.getIsDefault() != 0) {
            sheetQueryWrapper.eq(DeliveryElectronicSheet::getIsDefault, 1);
        }

        if (ObjectUtil.isNotEmpty(param.getIds())) {
            if (param.getIds().size() == 1) {
                sheetQueryWrapper.eq(DeliveryElectronicSheet::getId, param.getIds().get(0));
            } else {
                sheetQueryWrapper.in(DeliveryElectronicSheet::getId, param.getIds());
            }
        }

        if (param.getStatus() != null && param.getStatus() == 1) {
            sheetQueryWrapper.eq(DeliveryElectronicSheet::getStatus, 1);
        }

        var shopDeliveryElectronicSheets = deliveryElectronicSheetMapper.selectList(sheetQueryWrapper);
        var companyIds = CollStreamUtil.toSet(shopDeliveryElectronicSheets, DeliveryElectronicSheet::getExpressCompanyId);
        var companyMap = deliveryCompanyMapper.selectBatchIds(companyIds)
                .stream().collect(Collectors.toMap(DeliveryCompany::getCompanyId, e -> e));

        return shopDeliveryElectronicSheets.stream()
                .map(sheet -> {
                    var vo = BeanUtil.toBean(sheet, ElectronicSheetInfo.class);
                    var company = companyMap.getOrDefault(sheet.getExpressCompanyId(), new DeliveryCompany());
                    return vo.setCompany(BeanUtil.toBean(company, ElectronicSheetInfo.Company.class));
                })
                .toList();
    }

    private class ElectronicSheetPrinter {
        private static final String SINGLE_PRINT_TYPE = "single";
        private static final String MULTIPLE_PRINT_TYPE = "multiple";

        private final Cached cacheOperator;
        private final ElectronicSheetConfigValueVo config;
        private final ShopDeliveryPrintElectronicSheetParam param;

        private SiteAddress siteAddress;
        private SysArea senderExpSysArea;
        private SysArea senderCitySysArea;
        private List<MallOrderInfo> orderList;
        private SysArea senderProvinceSysArea;
        private List<MallOrderGoods> orderGoods;

        public ElectronicSheetPrinter(ShopDeliveryPrintElectronicSheetParam param) {
            this.param = param;
            this.cacheOperator = CacheFactory.getCacheOperator();
            this.config = getElectronicSheetConfig(param.getSiteId());

            if (ObjectUtil.hasEmpty(config.getKdniaoId(), config.getKdniaoApiKey())) {
                throw new RuntimeException("未配置电子面单接口");
            }

            init();
        }

        public List<ElectronicSheetPrintVo> print() {
            if (param.getPrintType() == null) {
                return printByDelivery();
            }

            return switch (param.getPrintType()) {
                case SINGLE_PRINT_TYPE -> printSingle();
                case MULTIPLE_PRINT_TYPE -> printMultiple();
                default -> Collections.emptyList();
            };
        }

        private List<ElectronicSheetPrintVo> printByDelivery() {
            MallOrderInfo orderInfo = orderList.get(0);
            if (ObjectUtil.isNotEmpty(param.getOrderGoodsIds())) {
                List<MallOrderInfo.OrderGoodsInfo> goodsInfos = orderGoodsMapper.selectBatchIds(param.getOrderGoodsIds())
                        .stream()
                        .map(o -> {
                            MallOrderInfo.OrderGoodsInfo info = new MallOrderInfo.OrderGoodsInfo();
                            BeanUtil.copyProperties(o, info);
                            return info;
                        })
                        .toList();
                orderInfo.setOrderGoods(goodsInfos);
            }

            // 查询电子面单模板
            boolean notDefault = ObjectUtil.isNotEmpty(param.getElectronicSheetId());
            List<ElectronicSheetInfo> info = getInfo(notDefault ? 0 : 1, notDefault ? param.getElectronicSheetId() : null);
            ElectronicSheetInfo esTemplateInfo = info.get(0);
            if (ObjectUtil.isEmpty(esTemplateInfo)) {
                throw new RuntimeException("电子面单模板不存在");
            }

            if ("0".equals(esTemplateInfo.getPrintStyle())) {
                esTemplateInfo.setPrintStyle("");
            }

            QueryWrapper<MallOrderDelivery> deliveryQueryWrapper = new QueryWrapper<>();
            deliveryQueryWrapper.eq("site_id", param.getSiteId())
                    .eq("order_id", orderInfo.getOrderId())
                    .eq("delivery_type", "express")
                    .eq("sub_delivery_type", "express");
            Long count = mallOrderDeliveryMapper.selectCount(deliveryQueryWrapper);

            DeliveryCompany company = deliveryCompanyMapper.selectById(esTemplateInfo.getExpressCompanyId());
            String key = String.format("%s_%s_%s_%s_%s", company.getExpressNoElectronicSheet(), orderInfo.getOrderNo(),
                    config.getKdniaoId(), esTemplateInfo.getPrintStyle(), count);

            Set<Integer> skuIds = CollStreamUtil.toSet(orderGoods, MallOrderGoods::getSkuId);
            List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectBatchIds(skuIds);
            BigDecimal weightTotal = skuList.stream()
                    .map(MallGoodsSku::getWeight)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal volumeTotal = skuList.stream()
                    .map(MallGoodsSku::getVolume)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<ApplyParam.Commodity> commodities = orderGoods.stream()
                    .map(orderGood -> {
                        String goodsName = renderIllegalChar(CharSequenceUtil.sub(orderGood.getGoodsName() + " " + orderGood.getSkuName(), 0, 50));
                        return new ApplyParam.Commodity()
                                .setGoodsName(goodsName)
                                .setGoodsquantity(orderGood.getNum());
                    })
                    .toList();

            if (ObjectUtil.isEmpty(commodities)) {
                ElectronicSheetPrintVo vo = new ElectronicSheetPrintVo();
                vo.setSuccess(false);
                vo.setResultCode(-1);
                vo.setReason("该订单不支持打印电子面单");
                vo.setOrderNo(orderInfo.getOrderNo());
                return List.of(vo);
            }

            ExtraShopGoodsDetail shopGoodsDetail = new ExtraShopGoodsDetail()
                    .setWeightTotal(weightTotal)
                    .setVolumeTotal(volumeTotal)
                    .setRemarks(Collections.emptyList())
                    .setCommodities(commodities);
            ElectronicSheetPrintVo apply = apply(key, esTemplateInfo, orderInfo.getOrderNo() + count, orderInfo, shopGoodsDetail);
            return List.of(apply);
        }

        private List<ElectronicSheetPrintVo> printSingle() {
            if (ObjectUtil.isEmpty(param.getList())) {
                return Collections.emptyList();
            }

            var electronicSheetIds = CollStreamUtil.toList(param.getList(), ShopDeliveryPrintElectronicSheetParam.Parcel::getElectronicSheetId)
                    .toArray(new Integer[]{});
            var electronicSheetInfoMap = getInfo(0, electronicSheetIds)
                    .stream()
                    .collect(Collectors.toMap(ElectronicSheetInfo::getId, e -> e));
            var results = new ArrayList<ElectronicSheetPrintVo>(param.getList().size());

            // 单订单打印，按照包裹拆分打印
            for (int i = 0; i < param.getList().size(); i++) {
                var parcel = param.getList().get(i);

                // 查询电子面单模板
                var electronicSheetInfo = electronicSheetInfoMap.get(parcel.getElectronicSheetId());
                if (ObjectUtil.isEmpty(electronicSheetInfo)) {
                    // 电子面单模板不存在
                    continue;
                }

                for (var order : orderList) {
                    var printStyle = "0".equals(electronicSheetInfo.getPrintStyle()) ? "" : electronicSheetInfo.getPrintStyle();
                    var key = renderSingleCacheKey(electronicSheetInfo.getCompany().getExpressNoElectronicSheet(), order, printStyle, parcel.getDeliveryId(), i);
                    var o = cacheOperator.get(key);
                    if (ObjectUtil.isNotEmpty(o)) {
                        var printResult = (ElectronicSheetPrintVo) o;
                        printResult.setDeliveryId(parcel.getDeliveryId());
                        results.add(printResult);
                        continue;
                    }

                    var legalProducts = order.getOrderGoods()
                            .stream()
                            .filter(goods -> goods.getStatus() == 1)
                            .filter(goods -> ObjectUtil.isNotEmpty(goods.getDeliveryId()))
                            .filter(goods -> ObjectUtil.equals(goods.getDeliveryId(), parcel.getDeliveryId()))
                            .toList();
                    if (ObjectUtil.isEmpty(legalProducts)) {
                        continue;
                    }

                    var extraShopGoodsDetail = extraShopGoodsDetail(order.getOrderId(), legalProducts);
                    if (ObjectUtil.isEmpty(extraShopGoodsDetail.getCommodities())) {
                        var printResult = ElectronicSheetPrintVo.getFailedResult();
                        printResult.setOrderNo(order.getOrderNo());
                        results.add(printResult);
                        continue;
                    }

                    var printResult = apply(key, electronicSheetInfo, order.getOrderNo() + i, order, extraShopGoodsDetail);
                    printResult.setDeliveryId(parcel.getDeliveryId());
                    results.add(printResult);
                }
            }

            return results;
        }

        private List<ElectronicSheetPrintVo> printMultiple() {
            var isDefault = ObjectUtil.isEmpty(param.getElectronicSheetId());
            var electronicSheetInfos = getInfo(isDefault ? 1 : 0, isDefault ? null : param.getElectronicSheetId());

            if (ObjectUtil.isEmpty(electronicSheetInfos)) {
                throw new RuntimeException("电子面单模板不存在");
            }

            var electronicSheetInfo = electronicSheetInfos.get(0);
            if ("0".equals(electronicSheetInfo.getPrintStyle())) {
                electronicSheetInfo.setPrintStyle("");
            }

            var orderCountMap = selectShopOrderDeliveryCount();
            var results = new ArrayList<ElectronicSheetPrintVo>(orderList.size());
            var hasOrderGoodsIds = ObjectUtil.isNotEmpty(param.getOrderGoodsIds());
            for (var order : orderList) {
                if (hasOrderGoodsIds && order.getOrderId().equals(param.getOrderId())) {
                    var replaceOrderGoods = order.getOrderGoods()
                            .stream()
                            .filter(goods -> param.getOrderGoodsIds().contains(goods.getOrderGoodsId()))
                            .toList();
                    order.setOrderGoods(replaceOrderGoods);
                }

                var shopOrderLambdaCount = orderCountMap.getOrDefault(order.getOrderId(), 0L);
                shopOrderLambdaCount -= shopOrderLambdaCount > 0 ? 1 : 0;

                var key = renderMultipleCacheKey(electronicSheetInfo, order.getOrderNo(), shopOrderLambdaCount);
                var o = cacheOperator.get(key);
                if (ObjectUtil.isNotEmpty(o)) {
                    results.add((ElectronicSheetPrintVo) o);
                    continue;
                }

                var legalProducts = order.getOrderGoods()
                        .stream()
                        .filter(goods -> ObjectUtil.equals(1, goods.getStatus()) && ObjectUtil.isNotEmpty(goods.getDeliveryId()))
                        .toList();
                if (ObjectUtil.isEmpty(legalProducts)) {
                    continue;
                }

                var shopGoodsDetail = extraShopGoodsDetail(order.getOrderId(), legalProducts);
                if (ObjectUtil.isEmpty(shopGoodsDetail.getCommodities())) {
                    var printResult = ElectronicSheetPrintVo.getFailedResult();
                    printResult.setOrderNo(order.getOrderNo());
                    results.add(printResult);
                    continue;
                }

                var printResult = apply(key, electronicSheetInfo, order.getOrderNo() + shopOrderLambdaCount, order, shopGoodsDetail);
                printResult.setOrderNo(order.getOrderNo());
                results.add(printResult);
            }

            return results;
        }

        private ElectronicSheetPrintVo apply(String cacheKey, ElectronicSheetInfo electronicSheetInfo, String orderCode,
                                             MallOrderInfo order, ExtraShopGoodsDetail extraShopGoodsDetail) {
            var apply = ElectronicSheetFactory.getEngine(config.getInterfaceType(), config.getKdniaoId(), config.getKdniaoApiKey())
                    .apply(cacheKey, getApplyParam(electronicSheetInfo, orderCode, order, extraShopGoodsDetail));
            return BeanUtil.toBean(apply, ElectronicSheetPrintVo.class);
        }

        private ApplyParam getApplyParam(ElectronicSheetInfo electronicSheetInfo, String orderCode,
                                         MallOrderInfo order, ExtraShopGoodsDetail extraShopGoodsDetail) {
            var sender = BeanUtil.toBean(siteAddress, ApplyParam.Sender.class)
                    .setName(siteAddress.getContactName())
                    .setProvinceName(senderProvinceSysArea.getName())
                    .setCityName(senderCitySysArea.getName())
                    .setExpAreaName(senderExpSysArea.getName())
                    .setPostCode("000000");

            var takerProvince = order.getTakerProvince();
            var takerCity = order.getTakerCity();
            var takerDistrict = order.getTakerDistrict();

            var sysAreaMap = groupSysAreaById(takerProvince, takerCity, takerDistrict);
            var receiverProvince = sysAreaMap.getOrDefault(takerProvince, new SysArea());
            var receiverCity = sysAreaMap.getOrDefault(takerCity, new SysArea());
            var receiverExpArea = sysAreaMap.getOrDefault(takerDistrict, new SysArea());

            var receiver = BeanUtil.toBean(order, ApplyParam.Receiver.class)
                    .setName(order.getTakerName())
                    .setMobile(order.getTakerMobile())
                    .setProvinceName(receiverProvince.getName())
                    .setCityName(receiverCity.getName())
                    .setExpAreaName(receiverExpArea.getName())
                    .setAddress(CharSequenceUtil.sub(order.getTakerAddress(), 0, 100))
                    .setPostCode("000000");

            return BeanUtil.toBean(electronicSheetInfo, ApplyParam.class)
                    .setShipperCode(electronicSheetInfo.getCompany().getExpressNoElectronicSheet())
                    .setOrderCode(orderCode)
                    .setExpType(String.valueOf(electronicSheetInfo.getExpType()))
                    .setReceiver(receiver)
                    .setSender(sender)
                    .setQuantity(Math.min(CollectionUtil.size(order.getOrderGoods()), 50))
                    .setCommodity(extraShopGoodsDetail.getCommodities())
                    .setWeight(extraShopGoodsDetail.getWeightTotal())
                    .setVolume(extraShopGoodsDetail.getVolumeTotal())
                    .setRemark("")
                    .setIsReturnPrintTemplate("1")
                    .setCost(order.getDeliveryMoney())
                    .setTemplateSize(electronicSheetInfo.getPrintStyle())
                    .setIsNotice(electronicSheetInfo.getIsNotice());
        }

        private ExtraShopGoodsDetail extraShopGoodsDetail(Integer orderId, List<MallOrderInfo.OrderGoodsInfo> shopGoodsInfoList) {
            var weightTotal = BigDecimal.ZERO; // 包裹总重量kg
            var volumeTotal = BigDecimal.ZERO; // 包裹总体积 m³
            var remarks = new ArrayList<String>();
            var commodities = new ArrayList<ApplyParam.Commodity>(shopGoodsInfoList.size());
            for (var orderGood : shopGoodsInfoList) {
                var orderDeliveryQueryWrapper = new LambdaQueryWrapper<MallOrderDelivery>()
                        .eq(MallOrderDelivery::getOrderId, orderId)
                        .eq(MallOrderDelivery::getId, orderGood.getDeliveryId())
                        .eq(MallOrderDelivery::getDeliveryType, "express")
                        .eq(MallOrderDelivery::getSubDeliveryType, "express");

                var shopOrderDelivery = mallOrderDeliveryMapper.selectOne(orderDeliveryQueryWrapper);
                if (ObjectUtil.isEmpty(shopOrderDelivery)) {
                    continue;
                }

                var goodsName = renderIllegalChar(CharSequenceUtil.sub(orderGood.getGoodsName() + orderGood.getSkuName(), 0, 50));
                var commodity = new ApplyParam.Commodity()
                        .setGoodsName(goodsName)
                        .setGoodsquantity(orderGood.getNum());
                commodities.add(commodity);

                var shopGoodsSkuQueryWrapper = new LambdaQueryWrapper<MallGoodsSku>()
                        .eq(MallGoodsSku::getSkuId, orderGood.getSkuId())
                        .eq(MallGoodsSku::getSiteId, param.getSiteId());

                var goodsSku = mallGoodsSkuMapper.selectOne(shopGoodsSkuQueryWrapper);
                if (ObjectUtil.isEmpty(goodsSku)) {
                    continue;
                }

                weightTotal = weightTotal.add(NumberUtil.round(goodsSku.getWeight(), 3));
                volumeTotal = volumeTotal.add(NumberUtil.round(goodsSku.getVolume(), 3));
            }

            return new ExtraShopGoodsDetail()
                    .setWeightTotal(weightTotal)
                    .setVolumeTotal(volumeTotal)
                    .setRemarks(remarks)
                    .setCommodities(commodities);
        }

        private Map<Integer, Long> selectShopOrderDeliveryCount() {
            var queryWrapper = new QueryWrapper<MallOrderDelivery>()
                    .eq("site_id", param.getSiteId())
                    .in("order_id", CollStreamUtil.toSet(orderList, MallOrderInfo::getOrderId))
                    .eq("delivery_type", "express")
                    .eq("sub_delivery_type", "express")
                    .groupBy("order_id")
                    .select("order_id as orderId", "count(1) as count");

            var result = new HashMap<Integer, Long>();
            mallOrderDeliveryMapper.selectMaps(queryWrapper)
                    .forEach(map -> result.put((Integer) map.get("orderId"), (Long) map.get("count")));
            return result;
        }

        private String renderIllegalChar(String name) {
            var goodsName = name;
            for (var s : new String[]{"'", "\"", "&", "+", "<", ">", "%", "\\", "#", "and"}) {
                goodsName = goodsName.replace(s, "");
            }
            return goodsName;
        }

        private String renderSingleCacheKey(String expressNoElectronicSheet, MallOrderInfo shopOrder, String printStyle, Integer deliveryId, int index) {
            return String.format("%s_%s_%s_%s_%d_%d", expressNoElectronicSheet, shopOrder.getOrderNo(), config.getKdniaoId(), printStyle, deliveryId, index);
        }

        private String renderMultipleCacheKey(ElectronicSheetInfo electronicSheetInfo, String orderNo, Long deliveryCount) {
            return String.format("%s_%s_%s_%s_%d", electronicSheetInfo.getCompany().getExpressNoElectronicSheet(),
                    orderNo, config.getKdniaoId(), electronicSheetInfo.getPrintStyle(), deliveryCount);
        }

        private List<ElectronicSheetInfo> getInfo(Integer isDefault, Integer... ids) {
            var electronicSheetInfoSearchParam = new ElectronicSheetInfoSearchParam()
                    .setSiteId(param.getSiteId())
                    .setIds(ListUtil.toList(ids))
                    .setStatus(1)
                    .setIsDefault(isDefault);
            return CoreElectronicSheetServiceImpl.this.getInfo(electronicSheetInfoSearchParam);
        }

        private ElectronicSheetConfigValueVo getElectronicSheetConfig(Integer siteId) {
            if (ObjectUtil.isEmpty(siteId)) {
                return ElectronicSheetConfigValueVo.newInstance();
            }

            var config = coreConfigService.getConfig(siteId, ShopDeliveryElectronicSheetConst.ELECTRONIC_SHEET_CONFIG_KEY);
            if (ObjectUtil.hasEmpty(config, config.getValue())) {
                return ElectronicSheetConfigValueVo.newInstance();
            }

            ElectronicSheetConfigValueVo parsed = JacksonUtils.toSnakeCaseObject(config.getValue(), ElectronicSheetConfigValueVo.class);
            return parsed != null ? parsed : ElectronicSheetConfigValueVo.newInstance();
        }

        private Map<Integer, SysArea> groupSysAreaById(Integer... areaIds) {
            return sysAreaMapper.selectBatchIds(Arrays.asList(areaIds))
                    .stream()
                    .collect(Collectors.toMap(SysArea::getId, e -> e));
        }

        private void init() {
            initOrderList();

            LambdaQueryWrapper<SiteAddress> addressQueryWrapper = new LambdaQueryWrapper<SiteAddress>()
                    .eq(SiteAddress::getIsDeliveryAddress, 1)
                    .eq(SiteAddress::getIsDefaultDelivery, 1)
                    .eq(SiteAddress::getSiteId, param.getSiteId());
            if (ObjectUtil.isEmpty((siteAddress = shopAddressMapper.selectOne(addressQueryWrapper)))) {
                throw new RuntimeException("商家未设置默认地址");
            }

            int provinceId = siteAddress.getProvinceId();
            int cityId = siteAddress.getCityId();
            int expId = siteAddress.getDistrictId();

            Map<Integer, SysArea> areaIdMap = groupSysAreaById(provinceId, cityId, expId);
            senderProvinceSysArea = areaIdMap.getOrDefault(provinceId, new SysArea());
            senderCitySysArea = areaIdMap.getOrDefault(cityId, new SysArea());
            senderExpSysArea = areaIdMap.getOrDefault(expId, new SysArea());
        }

        private void initOrderList() {
            var orderQueryWrapper = new LambdaQueryWrapper<MallOrder>()
                    .eq(MallOrder::getSiteId, param.getSiteId())
                    .eq(MallOrder::getOrderId, param.getOrderId())
                    .eq(MallOrder::getDeliveryType, OrderDeliveryTypeEnum.EXPRESS.getType())
                    .in(MallOrder::getStatus, ListUtil.of(OrderStatusEnum.WAIT_DELIVERY.getStatus(), OrderStatusEnum.WAIT_TAKE.getStatus()));

            var orders = mallOrderMapper.selectList(orderQueryWrapper);
            if (ObjectUtil.isEmpty(orders)) {
                throw new RuntimeException("订单不存在");
            }

            var orderGoodsQueryWrapper = new LambdaQueryWrapper<MallOrderGoods>()
                    .in(MallOrderGoods::getOrderId, CollStreamUtil.toSet(orders, MallOrder::getOrderId));
            orderGoods = orderGoodsMapper.selectList(orderGoodsQueryWrapper);
            var orderGoodsMap = orderGoods.stream().collect(Collectors.groupingBy(MallOrderGoods::getOrderId));

            orderList = orders.stream().map(order -> {
                // 处理订单商品
                var shopOrderGoods = orderGoodsMap.get(order.getOrderId());
                var orderGoodsInfos = ObjectUtil.isEmpty(shopOrderGoods) ? new ArrayList<MallOrderInfo.OrderGoodsInfo>() :
                        shopOrderGoods.stream().map(goods -> BeanUtil.toBean(goods, MallOrderInfo.OrderGoodsInfo.class)).toList();

                return BeanUtil.toBean(order, MallOrderInfo.class).setOrderGoods(orderGoodsInfos);
            }).toList();
        }
    }

    @Data
    @Accessors(chain = true)
    private static class ElectronicSheetInfo {
        private Integer id; //
        private Integer siteId; // 站点id
        private String templateName; // 模板名称
        private Integer expressCompanyId; // 物流公司id
        private String customerName; // 电子面单客户账号（CustomerName）
        private String customerPwd; // 电子面单密码（CustomerPwd）
        private String sendSite; // SendSite
        private String sendStaff; // SendStaff
        private String monthCode; // MonthCode
        private Integer payType; // 邮费支付方式（1：现付，2：到付，3：月结）
        private Integer isNotice; // 快递员上门揽件（0：否，1：是）
        private Integer status; // 状态（1：开启，0：关闭）
        private Integer expType; // 物流公司业务类型
        private String printStyle; // 电子面单打印模板样式
        private Integer isDefault; // 是否默认（1：是，0：否）
        private Company company;

        @Data
        public static class Company {
            private Integer companyId; //
            private Integer siteId; // 站点id
            private String companyName; // 物流公司名称
            private String logo; // 物流公司logo
            private String url; // 物流公司网站
            private String expressNo; // 物流公司编号(用于物流跟踪)
            private String expressNoElectronicSheet; // 物流公司编号(用于电子面单)
            private Integer electronicSheetSwitch; // 是否支持电子面单（0：不支持，1：支持）
            private String printStyle; // 电子面单打印模板样式，json字符串
            private String expType; // 物流公司业务类型，json字符串
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    private static class MallOrderInfo extends MallOrder {
        private List<OrderGoodsInfo> orderGoods;

        @EqualsAndHashCode(callSuper = true)
        @Data
        public static class OrderGoodsInfo extends MallOrderGoods {
        }
    }

    @Data
    @Accessors(chain = true)
    private static class ExtraShopGoodsDetail {
        private BigDecimal weightTotal;
        private BigDecimal volumeTotal;
        private List<String> remarks = new ArrayList<>();
        private List<ApplyParam.Commodity> commodities;
    }
}
