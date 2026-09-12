package com.niu.mall.service.core.order.helper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.DistanceCalculateUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryLocalDelivery;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStoreTypeEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.member.MemberAddressMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.core.entity.coupon.MallCouponGoods;
import com.niu.core.entity.coupon.MallCouponMember;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.entity.order.*;
import com.niu.mall.enums.active.ActiveClassEnum;
import com.niu.mall.enums.common.CommonActiveEnum;
import com.niu.core.enums.coupon.CouponMemberStatusEnum;
import com.niu.core.enums.coupon.CouponTypeEnum;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderDiscountTypeEnum;
import com.niu.mall.event.order.ShopOrderDiscountCreateEventDefiner;
import com.niu.core.mapper.coupon.MallCouponGoodsMapper;
import com.niu.core.mapper.coupon.MallCouponMemberMapper;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.order.MallOrderGiftGoodsMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.delivery.ICoreExpressService;
import com.niu.mall.service.core.delivery.ICoreLocalDeliveryService;
import com.niu.mall.service.core.delivery.ICoreStoreService;
import com.niu.core.service.core.marketing.ICoreCouponMemberService;
import com.niu.mall.service.core.marketing.ICoreFullGiftService;
import com.niu.core.service.admin.marketing.param.FullReductionParam;
import com.niu.core.service.core.marketing.ICoreFullReductionService;
import com.niu.mall.service.core.marketing.param.FullGiftCalculateParam;
import com.niu.core.service.core.marketing.param.FullReductionCalculateParam;
import com.niu.mall.service.core.marketing.vo.FullGiftCalculateVo;
import com.niu.core.service.core.marketing.vo.FullReductionCalculateVo;
import com.niu.mall.service.core.order.*;
import com.niu.mall.service.core.order.param.OrderCalculateParam;
import com.niu.mall.service.core.order.param.OrderHelperParam;
import com.niu.mall.service.core.order.vo.*;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public abstract class OrderCreateHelper {

    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;


    @Resource
    ICoreExpressService coreExpressService;

    @Resource
    ICoreLocalDeliveryService coreLocalDeliveryService;
    @Resource
    ICoreStoreService coreStoreService;

    @Resource
    MemberAddressMapper memberAddressMapper;

    @Resource
    MallCouponMemberMapper mallCouponMemberMapper;

    @Resource
    MallCouponGoodsMapper mallCouponGoodsMapper;

    @Resource
    ICoreCouponMemberService coreCouponMemberService;

    @Resource
    ICoreFullGiftService coreFullGiftService;

    @Resource
    ICoreFullReductionService coreFullReductionService;

    @Resource
    ICoreOrderDiscountService coreOrderDiscountService;

    @Resource
    com.niu.core.service.core.point_deduct.ICorePointDeductService corePointDeductService;

    @Resource
    com.niu.core.service.core.member.ICoreMemberAccountService coreMemberAccountService;

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    MallOrderGiftGoodsMapper mallOrderGiftGoodsMapper;

    @Resource
    com.niu.mall.mapper.order.MallOrderDiscountGoodsMapper mallOrderDiscountGoodsMapper;

    @Resource
    ICoreOrderEventService coreOrderEventService;

    @Resource
    ICoreOrderService coreOrderService;

    @Resource
    ICoreDiyFormRecordsService coreDiyFormRecordsService;

    @Resource
    ICoreOrderCloseService coreOrderCloseService;
    @Resource
    ICoreOrderConfigService coreOrderConfigService;
    @Resource
    ICoreOrderPayService coreOrderPayService;
    @Resource
    ICoreInvoiceService coreInvoiceService;
    @Resource
    DeliveryStoreMapper deliveryStoreMapper;
    @Resource
    DeliveryLocalDeliveryMapper deliveryLocalDeliveryMapper;
    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;
    @Resource
    MallGoodsMapper mallGoodsMapper;

    /**
     * @param orderKey
     * @return
     */
    protected <T extends OrderCreateDataVo> T loadOrderVo(String orderKey, Class<T> clazz) {
        // 按 memberId + orderKey 读缓存，反序列化为指定 VO 类型
        Cached cache = CacheFactory.getCacheOperator();
        Object data = cache.get(RequestUtils.memberId() + "_member_order_" + orderKey);
        if (data == null) throw new CommonException("订单数据已失效");

        return JSONUtil.toBean(JSONUtil.parseObj(data), clazz);
    }

    /**
     * 获取订单数据
     *
     * @param orderKey
     * @return
     */
    protected JSONObject loadOrderData(String orderKey) {
        // 读取订单缓存原始 JSON，供部分场景直接解析
        Cached cache = CacheFactory.getCacheOperator();
        Object data = cache.get(RequestUtils.memberId() + "_member_order_" + orderKey);
        if (data == null) throw new CommonException("订单数据已失效");

        return JSONUtil.parseObj(data);
    }

    /**
     * 缓存订单数据
     *
     * @param vo
     */
    protected <T extends OrderCreateDataVo> void cacheOrderVo(T vo) {
        // 无 orderKey 则先生成，再写入完整订单快照
        if (ObjectUtil.isEmpty(vo.getOrderKey())) vo.setOrderKey(createOrderNo());

        Cached cache = CacheFactory.getCacheOperator();
        cache.put(RequestUtils.memberId() + "_member_order_" + vo.getOrderKey(), JSONUtil.parseObj(vo).toString());
    }

    /**
     * 写入 orderKey + "_basic" 轻量快照，供 calculate 二次进入时快速加载。
     */
    protected <T extends OrderCreateDataVo> void cacheOrderVoBasic(T vo) {
        // 写入 order_key + _basic 快照，供 calculate 二次进入读取
        if (ObjectUtil.isEmpty(vo.getOrderKey())) {
            return;
        }
        Cached cache = CacheFactory.getCacheOperator();
        cache.put(RequestUtils.memberId() + "_member_order_" + vo.getOrderKey() + "_basic",
                JSONUtil.parseObj(vo).toString());
    }

    /**
     * 移除订单数据缓存
     *
     * @param orderKey
     */
    protected void removeOrderVo(String orderKey) {
        // 清除主缓存及 _basic 快照
        Cached cache = CacheFactory.getCacheOperator();
        String prefix = RequestUtils.memberId() + "_member_order_" + orderKey;
        cache.remove(prefix);
        cache.remove(prefix + "_basic");
    }

    /**
     * 创建交易号
     *
     * @return
     */
    protected String createOrderNo() {
        // 日期 + 雪花 ID 生成唯一 orderKey
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }

    /**
     * 获取配送方式
     *
     * @param vo
     * @param param
     */
    protected void getDelivery(OrderCreateDataVo vo, OrderCalculateParam param) {
        // 实物商品：取站点启用配送方式，与商品支持的配送方式求交集写入 deliveryTypeList
        OrderCreateDataVo.Basic basic = vo.getBasic();
        if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
            List<DeliveryConfigVo> deliveryList = coreDeliveryConfigService.getDeliveryConfig(param.getSiteId()).stream().filter(i -> i.getStatus().equals(1)).collect(Collectors.toList());
            Map<String, DeliveryConfigVo> map = new LinkedHashMap<>();
            if (ObjectUtil.isNotEmpty(basic.getHasDeliveryTypes())) {
                // 【循环】deliveryList：筛选站点启用且商品支持的配送方式，写入 map
                for (DeliveryConfigVo conf : deliveryList) {
                    if (conf.getStatus() == 1 && basic.getHasDeliveryTypes().contains(conf.getKey())) {
                        map.put(conf.getKey(), conf);
                    }
                }
            }
            vo.getDelivery().setDeliveryTypeList(map);
            // 含同城配送时附带同城配置
            if (map.containsKey(DeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                DeliveryLocalDelivery deliveryLocalDelivery = deliveryLocalDeliveryMapper.selectOne(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", param.getSiteId()).last("limit 1"));
                vo.getDelivery().setLocalDeliveryConfig(deliveryLocalDelivery);
            }
        } else if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.VIRTUAL.getType())) {
            // 虚拟商品：补齐 delivery_type_list，供前端按 delivery_type 取 name（避免空对象 truthy 导致 .name 报错）
            Map<String, DeliveryConfigVo> virtualMap = new LinkedHashMap<>();
            DeliveryConfigVo virtualConf = new DeliveryConfigVo();
            virtualConf.setKey(OrderDeliveryTypeEnum.VIRTUAL.getType());
            virtualConf.setName(OrderDeliveryTypeEnum.VIRTUAL.getName());
            virtualConf.setStatus(1);
            virtualMap.put(OrderDeliveryTypeEnum.VIRTUAL.getType(), virtualConf);
            vo.getDelivery().setDeliveryTypeList(virtualMap);
        } else {
            vo.getDelivery().setDeliveryTypeList(null);
        }
    }

    /**
     * 计算运费
     *
     * @param vo
     * @param param
     */
    protected void calculateDelivery(OrderCreateDataVo vo, OrderCalculateParam param, MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 确定配送方式、选地址/门店，再按类型调用对应运费计算器写入 deliveryMoney
        OrderCreateDataVo.Basic basic = vo.getBasic();
        String deliveryType = ObjectUtil.isNotEmpty(param.getDelivery()) ? param.getDelivery().getDeliveryType() : "";
        if (param.getOrderFrom().equals("pc")) deliveryType = OrderDeliveryTypeEnum.EXPRESS.getType(); // PC 端固定快递
        basic.setDeliveryMoney(new BigDecimal(0));
        if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
            if (ObjectUtil.isEmpty(vo.getDelivery().getDeliveryTypeList())) {
                vo.getError().add(vo.getSiteInfo().getSiteName() + "未设置配送方式");
                vo.getDelivery().setError("商家未设置配送方式");
            }
            if (!deliveryType.isEmpty()) {
                // 非 PC 端校验所选配送方式在可用列表内
                if (!vo.getOrderFrom().equals("pc")) {
                    if (!vo.getDelivery().getDeliveryTypeList().containsKey(deliveryType))
                        throw new CommonException("请选择正确的配送方式");
                }
            } else {
                // 未传配送方式时默认取可用列表第一项
                if (!vo.getDelivery().getDeliveryTypeList().isEmpty()) {
                    deliveryType = vo.getDelivery().getDeliveryTypeList().entrySet().iterator().next().getKey();
                }
            }

        } else {
            // 虚拟商品：固定虚拟配送，核销类商品单独标记 subDeliveryType
            if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.VIRTUAL.getType())) {
                deliveryType = OrderDeliveryTypeEnum.VIRTUAL.getType();
                String virtualDeliveryType = "";
                if (!vo.getGoodsData().isEmpty()) {
                    OrderCreateDataVo.Sku firstSku = vo.getGoodsData().values().iterator().next();
                    if (firstSku.getGoods() != null && ObjectUtil.isNotEmpty(firstSku.getGoods().getVirtualDeliveryType())) {
                        virtualDeliveryType = firstSku.getGoods().getVirtualDeliveryType();
                    }
                }
                if ("verify".equals(virtualDeliveryType)) {
                    vo.getDelivery().setSubDeliveryType("verify");
                } else {
                    vo.getDelivery().setSubDeliveryType(OrderDeliveryTypeEnum.VIRTUAL.getType());
                }
            }
        }


        if (deliveryType.isEmpty()) {
            vo.getError().add("请选择正确的配送方式");
        } else {
            vo.getDelivery().setDeliveryType(deliveryType);
            selectTakeAddress(vo, mallOrderCreateDataVo); // 继承平台级收货地址
            selectTakeStore(deliveryType, vo, param); // 同城/自提选门店
            if (deliveryType.equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
                coreExpressService.calculate(vo); // 快递运费
            }
            if (deliveryType.equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                coreLocalDeliveryService.calculate(vo); // 同城配送运费
            }
            if (deliveryType.equals(OrderDeliveryTypeEnum.STORE.getType())) {
                coreStoreService.calculate(vo); // 门店自提（通常无运费）
            }
        }
    }

    /**
     * 配置设置或查询
     *
     * @param vo
     * @param key
     */
    public void getConfig(OrderCreateDataVo vo, String key, Integer siteIdParam, OrderCalculateParam param) {
        // 按 key 懒加载订单关闭/配送/发票配置，已有则直接复用
        OrderCreateDataVo.ConfigInfo config = vo.getConfig();
        Integer siteId = ObjectUtil.isNotEmpty(siteIdParam) ? siteIdParam : param.getSiteId();
        Object obj = null;
        if (ObjectUtil.isNotEmpty(vo.getConfig())) {
            if (key.equals("order")) {
                obj = vo.getConfig().getCloseOrderInfo();
            } else if (key.equals("delivery_type")) {
                obj = vo.getConfig().getDeliveryType();
            } else if (key.equals("invoice")) {
                obj = vo.getConfig().getInvoice();
            }
        }
        if (ObjectUtil.isEmpty(obj)) {
            if (ObjectUtil.isEmpty(vo.getConfig())) {
                config = new OrderCreateDataVo.ConfigInfo();
            }
            switch (key) {
                case "order": // 订单关闭配置
                    OrderConfigVo orderConfig = coreOrderConfigService.getConfig();
                    OrderCreateDataVo.ConfigInfo.CloseOrder closeOrder = new OrderCreateDataVo.ConfigInfo.CloseOrder();
                    closeOrder.setIsClose(orderConfig.getCloseOrderInfo().getIsClose());
                    closeOrder.setCloseLength(orderConfig.getCloseOrderInfo().getCloseLength());
                    config.setCloseOrderInfo(closeOrder);
                    break;
                case "point":
                    break;
                case "delivery_type": // 站点配送方式列表
                    List<DeliveryConfigVo> deliveryList = coreDeliveryConfigService.getDeliveryConfig(siteId);
                    Map<String, Object> map = new LinkedHashMap<>();
                    // 【循环】deliveryList：组装 key->DeliveryConfigVo 映射写入 config.deliveryType
                    for (DeliveryConfigVo Obj : deliveryList) {
                        map.put(Obj.getKey(), Obj);
                    }
                    config.setDeliveryType(map);
                    break;
                case "invoice": // 发票配置
                    InvoiceInfoVo invoice = coreInvoiceService.invoice(siteId);
                    OrderCreateDataVo.ConfigInfo.InvoiceInfo invoiceInfo = new OrderCreateDataVo.ConfigInfo.InvoiceInfo();
                    invoiceInfo.setIsInvoice(invoice.getIsInvoice());
                    invoiceInfo.setInvoiceContent(invoice.getInvoiceContent());
                    config.setInvoice(invoiceInfo);
                    break;
            }
        }
        vo.setConfig(config);
    }


    /**
     * 选中收货地址
     *
     * @param vo
     * @param mallOrderCreateDataVo
     */
    protected void selectTakeAddress(OrderCreateDataVo vo, MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 实物订单继承平台级收货地址，同城配送校验经纬度
        if (vo.getBasic().getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
            vo.getDelivery().setTakeAddress(mallOrderCreateDataVo.getDelivery().getTakeAddress());
            if (vo.getDelivery().getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType()) &&
                    ObjectUtil.isNotEmpty(vo.getDelivery().getTakeAddress())) {
                JSONObject takeAddress = JSONUtil.parseObj(vo.getDelivery().getTakeAddress());
                if (ObjectUtil.isEmpty(takeAddress.get("lng"))) {
                    vo.getError().add("所选的地址没有设置地图定位");
                }
            }
        } else {
            vo.getDelivery().setTakeAddress(null);
        }
    }

    /**
     * 选中配送/自提门店
     *
     * @param deliveryType
     */
    protected void selectTakeStore(String deliveryType, OrderCreateDataVo vo, OrderCalculateParam param) {
        // 同城/自提：查可用门店，有定位则算距离并排序，默认选最近或用户指定门店
        OrderHelperParam.Delivery delivery = param.getDelivery();
        OrderHelperParam.Position position = param.getPosition();
        vo.getDelivery().setTakeStore("[]");
        if (LocalDeliveryStoreTypeEnum.getMap().containsKey(deliveryType)) {

            QueryWrapper<DeliveryStore> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("site_id", vo.getSiteId()).eq("status", 1);
            if (LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType().equals(deliveryType)) {
                queryWrapper.eq("support_local_delivery", 1); // 同城配送门店
            } else if (LocalDeliveryStoreTypeEnum.STORE.getType().equals(deliveryType)) {
                queryWrapper.eq("support_store", 1); // 自提门店
            }
            List<OrderCreateDataVo.TakeStore> list = CollectUtils.convert(deliveryStoreMapper.selectList(queryWrapper), OrderCreateDataVo.TakeStore.class);
            if (list.isEmpty()) {
                if (LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType().equals(deliveryType)) {
                    vo.getError().add(vo.getSiteInfo().getSiteName() + "商家未设置配送门店");
                    vo.getDelivery().setError(vo.getSiteInfo().getSiteName() + "商家未设置配送门店");
                } else {
                    vo.getError().add(vo.getSiteInfo().getSiteName() + "商家未设置提货点");
                    vo.getDelivery().setError(vo.getSiteInfo().getSiteName() + "商家未设置提货点");
                }
            }
            if (!list.isEmpty() && ObjectUtil.isNotEmpty(position) && ObjectUtil.isNotEmpty(position.getLatitude()) && ObjectUtil.isNotEmpty(position.getLongitude())) {
                // 【循环】list：按用户定位计算各门店距离（km）并排序
                for (OrderCreateDataVo.TakeStore item : list) {
                    Double distance = DistanceCalculateUtils.calculateDistance(
                            Double.parseDouble(position.getLatitude()),
                            Double.parseDouble(position.getLongitude()),
                            Double.parseDouble(item.getLatitude()),
                            Double.parseDouble(item.getLongitude())
                    );
                    item.setDistance(BigDecimal.valueOf(distance).divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP));
                }
                list = list.stream().sorted(Comparator.comparing(OrderCreateDataVo.TakeStore::getDistance)).toList();
            }
            vo.getDelivery().setTakeStoreList(list);
            if (ObjectUtil.isEmpty(delivery.getTakeStoreId()) || (ObjectUtil.isNotEmpty(delivery.getTakeStoreId()) && delivery.getTakeStoreId() == 0)) {
                // 未指定门店：默认选距离最近（或列表首项）
                if (!list.isEmpty()) {
                    vo.getDelivery().setTakeStore(list.get(0));
                }
            } else {
                // 用户指定门店：校验存在性与配送能力，回写 takeStore
                DeliveryStore deliveryStore = deliveryStoreMapper.selectById(delivery.getTakeStoreId());
                if (ObjectUtil.isEmpty(deliveryStore)) {
                    vo.getError().add("提货点不存在");
                } else {
                    switch (deliveryType) {
                        case "local_delivery":
                            if (deliveryStore.getSupportLocalDelivery() == null || deliveryStore.getSupportLocalDelivery() != 1) {
                                vo.getError().add("该门店不支持同城配送");
                            }
                            break;
                        case "store":
                            if (deliveryStore.getSupportStore() == null || deliveryStore.getSupportStore() != 1) {
                                vo.getError().add("该门店不支持门店自提");
                            }
                            break;
                    }
                }
                OrderCreateDataVo.TakeStore deliveryStoreVo = new OrderCreateDataVo.TakeStore();
                if (ObjectUtil.isNotEmpty(deliveryStore)) {
                    BeanUtils.copyProperties(deliveryStore, deliveryStoreVo);
                }
                List<OrderCreateDataVo.TakeStore> listThis = list.stream().filter(item -> item.getStoreId().equals(delivery.getTakeStoreId())).toList();
                if (!listThis.isEmpty() && ObjectUtil.isNotEmpty(deliveryStoreVo)) {
                    deliveryStoreVo.setDistance(listThis.get(0).getDistance()); // 复用已算距离
                }
                vo.getDelivery().setTakeStore(deliveryStoreVo);
            }
        }
    }

    /**
     * 计算优惠
     *
     * @param vo
     * @param param
     */
    protected void calculateDiscount(OrderCreateDataVo vo, OrderCalculateParam param) {
        // 重算店铺优惠：先清零订单/SKU 各级优惠金额，再依次算券与满减
        vo.getBasic().setDiscountMoney(new BigDecimal(0));
        vo.getBasic().setFullReductionMoney(BigDecimal.ZERO);
        vo.getBasic().setMallDiscountMoney(BigDecimal.ZERO);
        vo.getBasic().setMallCouponMoney(BigDecimal.ZERO);
        vo.getBasic().setPointDeductMoney(BigDecimal.ZERO);
        vo.getBasic().setUsePoint(0);
        // 【循环】goodsData：清零各 SKU 及嵌套 Goods 的优惠券/满减/平台/积分优惠字段
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            sku.setDiscountMoney(BigDecimal.ZERO);
            sku.setFullReductionMoney(BigDecimal.ZERO);
            sku.setFullReduction(null);
            sku.setMallDiscountMoney(BigDecimal.ZERO);
            sku.setPointDeductMoney(BigDecimal.ZERO);
            sku.setUsePoint(0);
            OrderCreateDataVo.Goods goods = sku.getGoods();
            if (goods != null) {
                goods.setMallDiscountMoney(BigDecimal.ZERO);
                goods.setPointDeductMoney(BigDecimal.ZERO);
                goods.setUsePoint(0);
            }
        }

        this.calculateCoupon(vo, param);

        this.calculateFullReduction(vo, param);
    }


    /**
     * 计算优惠券优惠
     *
     * @param vo
     * @param param
     */
    protected void calculateCoupon(OrderCreateDataVo vo, OrderCalculateParam param) {
        // 校验会员券有效性，按券类型筛选可抵扣商品行并比例分摊写入 discount.coupon
        vo.getBasic().setCouponMoney(BigDecimal.ZERO);
        if (ObjectUtil.isNotEmpty(param.getDiscount().getCouponId()) && param.getDiscount().getCouponId() > 0 && vo.getIsShopCoupon() > 0) {
            MallCouponMember memberCoupon = mallCouponMemberMapper.selectOne(new QueryWrapper<MallCouponMember>()
                    .eq("member_id", vo.getMemberId())
                    .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                    .eq("id", param.getDiscount().getCouponId()));
            if (memberCoupon == null) throw new CommonException("优惠券已使用或已过期");
            if (memberCoupon.getExpireTime().compareTo(System.currentTimeMillis() / 1000) == -1)
                throw new CommonException("当前优惠券已过期");

            List<OrderCreateDataVo.Sku> matchGoods = new LinkedList<>();
            BigDecimal matchGoodsMoney = new BigDecimal(0);
            BigDecimal matchOrderGoodsMoney = new BigDecimal(0);
            // 通用券：全单商品参与匹配
            if (memberCoupon.getType().equals(CouponTypeEnum.ALL.getType().toString())) {
                matchGoods = vo.getGoodsData().values().stream().collect(Collectors.toList());
                matchGoodsMoney = vo.getBasic().getGoodsMoney();
                matchOrderGoodsMoney = vo.getBasic().getGoodsMoney().subtract(vo.getBasic().getDiscountMoney());
            }
            // 品类券：商品分类与券适用分类有交集才计入
            if (memberCoupon.getType().equals(CouponTypeEnum.CATEGORY.getType().toString())) {
                List<Integer> categoryIds = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                        .eq("coupon_id", memberCoupon.getCouponId())
                        .select("category_id")).stream().map(i -> i.getCategoryId()).collect(Collectors.toList());
                matchGoodsMoney = BigDecimal.ZERO;
                // 【循环】goodsData：筛选分类与券适用分类有交集的 SKU，累加匹配金额
                for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                    List<Integer> goodsCategory = JSONUtil.parseArray(sku.getGoods().getGoodsCategory()).toList(Integer.class);
                    boolean hasIntersection = goodsCategory.stream().anyMatch(categoryIds::contains);
                    if (hasIntersection) {
                        matchGoods.add(sku);
                        matchGoodsMoney = matchGoodsMoney.add(sku.getGoodsMoney().subtract(sku.getDiscountMoney()));
                        matchOrderGoodsMoney = matchOrderGoodsMoney.add(calculateOrderGoodsMoney(sku));
                    }
                }
            }
            // 商品券：仅券指定 goods_id 的行参与
            if (memberCoupon.getType().equals(CouponTypeEnum.GOODS.getType().toString())) {
                List<Integer> goodsIds = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                        .eq("coupon_id", memberCoupon.getCouponId())
                        .select("goods_id")).stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
                matchGoodsMoney = BigDecimal.ZERO;
                // 【循环】goodsData：筛选 goods_id 在券指定列表内的 SKU，累加匹配金额
                for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                    if (goodsIds.contains(sku.getGoodsId())) {
                        matchGoods.add(sku);
                        matchGoodsMoney = matchGoodsMoney.add(sku.getGoodsMoney().subtract(sku.getDiscountMoney()));
                        matchOrderGoodsMoney = matchOrderGoodsMoney.add(calculateOrderGoodsMoney(sku));
                    }
                }
            }

            if (matchGoods.isEmpty()) {
                vo.getError().add("当前优惠券在本单不可用");
            } else {
                if (matchGoodsMoney.compareTo(new BigDecimal(0)) == 0 || matchGoodsMoney.compareTo(memberCoupon.getMinConditionMoney()) == -1) {
                    vo.getError().add("未达到当前优惠券的最低使用条件");
                } else {
                    BigDecimal couponMoney = memberCoupon.getPrice().compareTo(matchOrderGoodsMoney) > 0 ? matchOrderGoodsMoney : memberCoupon.getPrice(); // 券面额不超过匹配行实付
                    BigDecimal surplusMoney = couponMoney;
                    List<OrderCreateDataVo.Sku> matchGoodsList = matchGoods.stream().filter(sku -> {
                        Boolean er = (sku.getGoodsMoney().subtract(sku.getDiscountMoney())).compareTo(BigDecimal.ZERO) != 0;
                        return er;
                    }).toList();
                    // 【循环】matchGoodsList：按各行实付占比分摊券面额，最后一行用剩余金额避免舍入误差
                    for (int i = 0; i < matchGoodsList.size(); i++) {
                        OrderCreateDataVo.Sku sku = matchGoodsList.get(i);
                        BigDecimal itemOrderGoodsMoney = calculateOrderGoodsMoney(sku);
                        Integer itemSkuId = sku.getSkuId();
                        BigDecimal itemCouponMoney;
                        if (i == matchGoodsList.size() - 1) {
                            itemCouponMoney = surplusMoney;
                        } else {
                            itemCouponMoney = itemOrderGoodsMoney.divide(matchOrderGoodsMoney, 2, RoundingMode.HALF_UP);
                            itemCouponMoney = (itemCouponMoney.multiply(couponMoney)).setScale(2, RoundingMode.HALF_UP);
                        }
                        vo.getGoodsData().get(itemSkuId).setDiscountMoney(vo.getGoodsData().get(itemSkuId).getDiscountMoney().add(itemCouponMoney));
                        surplusMoney = surplusMoney.subtract(itemCouponMoney);
                    }
                    // 累加订单级优惠金额并写入 discount.coupon
                    vo.getBasic().setDiscountMoney(vo.getBasic().getDiscountMoney().add(couponMoney));
                    vo.getBasic().setCouponMoney(vo.getBasic().getCouponMoney().add(couponMoney));

                    OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                    discount.setMatchGoodsIds(matchGoodsList.stream().map(OrderCreateDataVo.Sku::getSkuId).toArray(Integer[]::new));
                    discount.setNum(1);
                    discount.setType("discount");
                    discount.setMoney(couponMoney);
                    discount.setDiscountType("coupon");
                    discount.setDiscountTypeId(memberCoupon.getId());
                    discount.setTitle(memberCoupon.getTitle());
                    discount.setContent("");
                    discount.setIsCan(true);
                    vo.getDiscount().put("coupon", discount);
                }
            }
        } else {
            // 未选券时移除缓存中的 coupon 优惠项
            if (vo.getDiscount().get("coupon") != null) {
                vo.getDiscount().remove("coupon");
                vo.getBasic().setCouponMoney(BigDecimal.ZERO);
            }
        }
    }

    /**
     * 计算订单项金额
     *
     * @param goods 商品信息Map
     * @return 订单商品金额
     */
    public static BigDecimal calculateOrderGoodsMoney(OrderCreateDataVo.Sku goods) {
        // 行实付 = goods_money - discount_money - mall_discount_money，最低为 0
        BigDecimal goodsMoney = goods.getGoodsMoney();
        BigDecimal discountMoney = ObjectUtil.isNotEmpty(goods.getDiscountMoney()) ? goods.getDiscountMoney() : BigDecimal.ZERO;
        BigDecimal mallDiscountMoney = resolveSkuMallDiscountMoney(goods); // 含嵌套 Goods 上的积分抵扣
        BigDecimal orderGoodsMoney = goodsMoney.subtract(discountMoney);
        orderGoodsMoney = orderGoodsMoney.subtract(mallDiscountMoney);
        if (orderGoodsMoney.compareTo(BigDecimal.ZERO) < 0) {
            orderGoodsMoney = BigDecimal.ZERO;
        }
        return orderGoodsMoney.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 解析 SKU 平台优惠金额（含积分抵扣），兼容积分仅写在嵌套 Goods 上的算价缓存。
     */
    public static BigDecimal resolveSkuMallDiscountMoney(OrderCreateDataVo.Sku sku) {
        // 汇总 SKU 平台优惠（含嵌套 Goods 上的积分抵扣）
        BigDecimal mallDiscountMoney = ObjectUtil.isNotEmpty(sku.getMallDiscountMoney()) ? sku.getMallDiscountMoney() : BigDecimal.ZERO;
        if (sku.getGoods() == null) {
            return mallDiscountMoney;
        }
        BigDecimal goodsPointDeduct = ObjectUtil.isNotEmpty(sku.getGoods().getPointDeductMoney())
                ? sku.getGoods().getPointDeductMoney() : BigDecimal.ZERO;
        // 积分仅写在嵌套 Goods 上时，合并到 mallDiscountMoney
        if (goodsPointDeduct.compareTo(BigDecimal.ZERO) > 0 && mallDiscountMoney.compareTo(goodsPointDeduct) < 0) {
            BigDecimal skuPointDeduct = ObjectUtil.isNotEmpty(sku.getPointDeductMoney()) ? sku.getPointDeductMoney() : BigDecimal.ZERO;
            if (skuPointDeduct.compareTo(BigDecimal.ZERO) == 0) {
                return mallDiscountMoney.add(goodsPointDeduct);
            }
        }
        return mallDiscountMoney;
    }

    /**
     * 解析 SKU 参与满减的实际支付基数（扣除限时折扣、优惠券、平台优惠）。
     */
    public static BigDecimal resolveSkuPayMoneyForFullReduction(OrderCreateDataVo.Sku sku) {
        // 满减匹配基数：行金额减店铺/平台已优惠
        BigDecimal effectiveGoodsMoney = resolveSkuEffectiveGoodsMoney(sku);
        BigDecimal discountMoney = ObjectUtil.isNotEmpty(sku.getDiscountMoney()) ? sku.getDiscountMoney() : BigDecimal.ZERO;
        BigDecimal mallDiscountMoney = resolveSkuMallDiscountMoney(sku);

        BigDecimal payMoney = effectiveGoodsMoney.subtract(discountMoney).subtract(mallDiscountMoney);
        if (payMoney.compareTo(BigDecimal.ZERO) < 0) {
            payMoney = BigDecimal.ZERO;
        }
        return payMoney.setScale(2, RoundingMode.DOWN);
    }

    /**
     * 限时折扣等店铺营销后行金额：goods_money 仍为原价行金额时，按售价×数量折算。
     */
    public static BigDecimal resolveSkuEffectiveGoodsMoney(OrderCreateDataVo.Sku sku) {
        // 限时折扣后行金额，售价×数量低于 goods_money 时取售价
        BigDecimal goodsMoney = ObjectUtil.isNotEmpty(sku.getGoodsMoney()) ? sku.getGoodsMoney() : BigDecimal.ZERO;
        if (sku.getPrice() == null || sku.getNum() == null || sku.getNum() <= 0) {
            return goodsMoney;
        }
        BigDecimal saleLineMoney = sku.getPrice().multiply(BigDecimal.valueOf(sku.getNum())).setScale(2, RoundingMode.HALF_UP);
        if (saleLineMoney.compareTo(goodsMoney) < 0) {
            return saleLineMoney;
        }
        return goodsMoney;
    }

    /**
     * 计算满减 (多商品版)
     * 支持满件/满元、阶梯/循环、满减/折扣，按商品实际支付金额比例分摊，并记录商品级和订单级优惠明细。
     */
    protected void calculateFullReduction(OrderCreateDataVo vo, OrderCalculateParam param) {
        // 按当前商品行实付匹配满减活动，计算优惠并按占比分摊到 SKU，写入 discount/goodsDiscount/mallData
        if (!Integer.valueOf(1).equals(vo.getIsJoinFullReduction())) {
            return; // 未参与满减则直接返回
        }
        if (ObjectUtil.isEmpty(vo.getGoodsData()) || vo.getGoodsData().isEmpty()) return;

        stripFullReductionDiscount(vo); // 重算前剥离缓存快照中的旧满减，避免 _basic 脏数据累加

        // ---------- 1. 构建满减请求参数 ----------
        List<FullReductionCalculateParam.GoodsSkuData> goodsSkuDataList = new ArrayList<>();
        // 【循环】goodsData：收集各 SKU 满减匹配基数 payMoney，初始化满减字段
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            BigDecimal payMoney = resolveSkuPayMoneyForFullReduction(sku);

            FullReductionCalculateParam.GoodsSkuData data = new FullReductionCalculateParam.GoodsSkuData();
            data.setGoodsId(sku.getGoodsId());
            data.setSkuId(sku.getSkuId());
            data.setNum(sku.getNum());
            data.setPayMoney(payMoney);
            data.setSkuKey(sku.getGoodsId() + "_" + sku.getSkuId());
            goodsSkuDataList.add(data);

            sku.setFullReduction(null);
            sku.setFullReductionMoney(BigDecimal.ZERO);
        }
        FullReductionCalculateParam calcParam = new FullReductionCalculateParam();
        calcParam.setGoodsSkuData(goodsSkuDataList);
        calcParam.setMemberId(vo.getMemberId());
        calcParam.setSiteId(vo.getSiteId());
        calcParam.setCheckRule(true);

        // ---------- 2. 获取所有可用的满减活动 ----------
        List<FullReductionCalculateVo> activeList = coreFullReductionService.getFullReductionInfo(calcParam);
        if (activeList == null || activeList.isEmpty()) {
            // 无可用活动：清零满减金额并清除 discount/mallData 中的满减明细
            if (vo.getBasic() != null) {
                vo.getBasic().setFullReductionMoney(BigDecimal.ZERO);
            }
            if (vo.getDiscount() != null) {
                vo.getDiscount().entrySet().removeIf(e -> isFullReductionDiscountValue(e.getValue()));
            }
            if (vo.getMallData() != null) {
                Map<String, OrderCreateDataVo.Discount> mallDiscount = getTypedValue(
                        vo.getMallData(),
                        "discount",
                        new TypeReference<Map<String, OrderCreateDataVo.Discount>>() {
                        },
                        new HashMap<>()
                );
                mallDiscount.remove(CommonActiveEnum.FULL_REDUCTION.getType());
            }
            return;
        }

        StringBuilder allTitleStr = new StringBuilder(); // 汇总各活动标题写入 mallData

        // 【外层循环】activeList：逐个满减活动计算优惠并分摊到匹配 SKU
        for (FullReductionCalculateVo active : activeList) {
            List<FullReductionCalculateVo.MatchedGoods> matchedGoods = active.getMatchedGoods();
            ObjectMapper objectMapper = new ObjectMapper();

            List<FullReductionParam.Rule> ruleJson = null;
            try {
                ruleJson = objectMapper.readValue(active.getRuleJson(), new TypeReference<List<FullReductionParam.Rule>>() {
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            String type = active.getType();               // amount/num：满元/满件
            String ruleType = active.getRuleType();       // LADDER/CYCLE：阶梯/循环
            String promotionType = active.getPromotionType(); // REDUCTION/DISCOUNT：减钱/打折
            BigDecimal matchedTotal = active.getMatchedTotal(); // 匹配的总件数或总金额
            Integer id = active.getId();
            String title = active.getTitle();
            if (title != null && !title.isEmpty()) {
                allTitleStr.append(title).append(" ");
            }

            if (matchedGoods == null || matchedGoods.isEmpty() || ruleJson == null || ruleJson.isEmpty()) {
                continue;
            }

            BigDecimal goodsTotalPrice = BigDecimal.ZERO;
            // 【循环】matchedGoods：累加本活动匹配商品 payMoney 合计
            for (FullReductionCalculateVo.MatchedGoods goods : matchedGoods) {
                goodsTotalPrice = goodsTotalPrice.add(goods.getPayMoney());
            }

            // ---------- 4. 计算本次活动总优惠金额 ----------
            BigDecimal fullReductionMoney = coreFullReductionService.calculateFullReductionDiscount(
                    type, ruleType, promotionType, ruleJson, matchedTotal, goodsTotalPrice);

            if (fullReductionMoney.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // ---------- 5. 按商品实际支付金额占比分摊优惠 ----------
            BigDecimal allPayMoney = BigDecimal.ZERO;
            // 【循环】matchedGoods：累加分摊基数 allPayMoney
            for (FullReductionCalculateVo.MatchedGoods goods : matchedGoods) {
                allPayMoney = allPayMoney.add(goods.getPayMoney());
            }

            BigDecimal calculateFullReductionMoney = BigDecimal.ZERO; // 本活动已分摊优惠累计
            int matchedSize = matchedGoods.size();
            // 【内层循环】matchedGoods：按比例分摊 fullReductionMoney 到各 SKU，末行用剩余金额
            for (int i = 0; i < matchedSize; i++) {
                FullReductionCalculateVo.MatchedGoods goods = matchedGoods.get(i);
                Integer skuId = goods.getSkuId();
                BigDecimal goodsPayMoney = goods.getPayMoney();
                BigDecimal goodsReduce;
                if (i == matchedSize - 1) {
                    goodsReduce = fullReductionMoney.subtract(calculateFullReductionMoney);
                } else {
                    BigDecimal payMoneyRate = goodsPayMoney.divide(allPayMoney, 4, RoundingMode.DOWN);
                    goodsReduce = payMoneyRate.multiply(fullReductionMoney).setScale(2, RoundingMode.DOWN);
                }
                if (goodsReduce.compareTo(goodsPayMoney) > 0) {
                    goodsReduce = goodsPayMoney; // 分摊不超过行实付
                }

                OrderCreateDataVo.Sku sku = vo.getGoodsData().get(skuId);
                sku.setFullReduction(active);
                sku.setFullReductionMoney(goodsReduce);
                sku.setDiscountMoney(sku.getDiscountMoney().add(goodsReduce));
                calculateFullReductionMoney = calculateFullReductionMoney.add(goodsReduce);

                String uniqueKey = sku.getSiteId() + "_" + sku.getGoodsId() + "_" + skuId + ":" + CommonActiveEnum.FULL_REDUCTION.getType();

                // 写入商品级优惠明细 goodsDiscount
                OrderCreateDataVo.Discount goodsDiscount = new OrderCreateDataVo.Discount();
                goodsDiscount.setMatchGoodsIds(new Integer[]{skuId});
                goodsDiscount.setType(ActiveClassEnum.DISCOUNT.getType());
                goodsDiscount.setDiscountType(CommonActiveEnum.FULL_REDUCTION.getType());
                goodsDiscount.setNum(0);
                goodsDiscount.setMoney(goodsReduce);
                goodsDiscount.setOrderDiscountId(id);
                goodsDiscount.setContent("参与满减优惠活动");
                goodsDiscount.setTitle(title == null ? "" : title);
                vo.getGoodsDiscount().put(uniqueKey, goodsDiscount);
                // 写入订单级优惠明细 discount
                OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                discount.setMatchGoodsIds(new Integer[]{skuId});
                discount.setType(ActiveClassEnum.DISCOUNT.getType());
                discount.setNum(0);
                discount.setMoney(goodsReduce);
                discount.setDiscountType(CommonActiveEnum.FULL_REDUCTION.getType());
                discount.setDiscountTypeId(0);
                discount.setContent("参与满减优惠活动");
                discount.setTitle(title == null ? "" : title);
                vo.getDiscount().put(uniqueKey, discount);
            }

            // 累加本活动优惠到订单 basic 汇总
            vo.getBasic().setFullReductionMoney(vo.getBasic().getFullReductionMoney().add(calculateFullReductionMoney));
            vo.getBasic().setDiscountMoney(vo.getBasic().getDiscountMoney().add(calculateFullReductionMoney));
        }

        // ---------- 7. 满减总额>0 时在 mallData 写入平台级汇总（多站点） ----------
        if (vo.getBasic().getFullReductionMoney().compareTo(BigDecimal.ZERO) > 0) {
            Map<String, OrderCreateDataVo.Discount> map = getTypedValue(
                    vo.getMallData(),
                    "discount",
                    new TypeReference<Map<String, OrderCreateDataVo.Discount>>() {
                    },
                    new HashMap<>()
            );
            OrderCreateDataVo.Discount mallDiscount = map.computeIfAbsent(CommonActiveEnum.FULL_REDUCTION.getType(), k -> new OrderCreateDataVo.Discount());

            mallDiscount.setMatchGoodsIds(new Integer[]{});
            mallDiscount.setType(ActiveClassEnum.DISCOUNT.getType());
            mallDiscount.setNum(0);
            mallDiscount.setMoney(vo.getBasic().getFullReductionMoney());
            mallDiscount.setDiscountType(CommonActiveEnum.FULL_REDUCTION.getType());
            mallDiscount.setDiscountTypeId(0);
            mallDiscount.setContent("参与满减优惠活动");
            mallDiscount.setTitle(allTitleStr.toString().trim());
        }
    }

    /**
     * 计算满赠（多活动支持，赠品单独记录，更新商品活动信息）
     * 注意：赠品金额为0，不参与金额计算，仅作为优惠记录和赠品列表输出。
     */
    protected void calculateFullGift(OrderCreateDataVo vo, OrderCalculateParam param) {
        // 匹配满赠活动，补全赠品 SKU 信息，写入 discount/mallData 及 basic 赠品列表（赠品金额恒为 0）
        if (!Integer.valueOf(1).equals(vo.getIsJoinFullGift())) {
            return; // 未参与满赠则直接返回
        }

        // ---------- 1. 构建满赠计算参数 ----------
        List<FullGiftCalculateParam.GoodsSkuData> goodsSkuDataList = new ArrayList<>();
        // 【循环】goodsData：收集各 SKU 满赠匹配基数 payMoney，初始化 fullGift 字段
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            FullGiftCalculateParam.GoodsSkuData data = new FullGiftCalculateParam.GoodsSkuData();
            data.setSkuId(sku.getSkuId());
            BigDecimal payMoney = sku.getGoodsMoney().subtract(
                    sku.getDiscountMoney() != null ? sku.getDiscountMoney() : BigDecimal.ZERO); // 行金额减店铺优惠，不含平台优惠
            data.setPayMoney(payMoney);
            data.setGoodsId(sku.getGoodsId());
            data.setNum(sku.getNum());
            data.setSkuKey(sku.getGoodsId() + "_" + sku.getSkuId());
            goodsSkuDataList.add(data);

            sku.setFullGift(null);
        }

        FullGiftCalculateParam calcParam = new FullGiftCalculateParam();
        calcParam.setSiteId(vo.getSiteId());
        calcParam.setMemberId(vo.getMemberId());
        calcParam.setCheckRule(true);
        calcParam.setGoodsSkuData(goodsSkuDataList);

        // ---------- 2. 获取所有匹配的满赠活动 ----------
        List<FullGiftCalculateVo> activeList = coreFullGiftService.getFullGiftInfo(calcParam);

        List<Map<String, Object>> giftSkuList = new ArrayList<>();
        List<FullGiftCalculateVo> processedActiveList = new ArrayList<>();

        if (ObjectUtil.isNotEmpty(activeList)) {
            // 【外层循环】activeList：逐个满赠活动处理赠品与匹配商品
            for (FullGiftCalculateVo active : activeList) {
                // 【内层循环】active.giftList：补全赠品 SKU 信息并写入优惠明细
                for (FullGiftCalculateVo.GiftSkuData gift : active.getGiftList()) {
                    Map<String, Object> giftInfo = buildGiftSkuInfo(gift);
                    giftSkuList.add(giftInfo);

                    // 订单级优惠记录（每个赠品一条，money=0）
                    OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                    discount.setMatchGoodsIds(new Integer[]{});
                    discount.setType(ActiveClassEnum.DISCOUNT.getType());
                    discount.setNum(gift.getNum());
                    discount.setMoney(BigDecimal.ZERO);
                    discount.setDiscountType(CommonActiveEnum.FULL_GIFT.getType());
                    discount.setDiscountTypeId(active.getFullGiftId());
                    discount.setContent(JSONUtil.toJsonStr(Map.of("sku_id", gift.getSkuId())));
                    discount.setTitle(active.getTitle() != null ? active.getTitle() : "");

                    vo.getDiscount().put(vo.getSiteId() + "_" + gift.getGoodsId() + "_" + gift.getSkuId() + ":" + CommonActiveEnum.FULL_GIFT.getType(), discount);

                    // 平台级优惠记录（多站点汇总，可多条赠品）
                    Map<String, List<OrderCreateDataVo.Discount>> map = getTypedValue(
                            vo.getMallData(),
                            "discount",
                            new TypeReference<Map<String, List<OrderCreateDataVo.Discount>>>() {
                            },
                            new HashMap<>()
                    );
                    map.computeIfAbsent(CommonActiveEnum.FULL_GIFT.getType(), k -> new ArrayList<>()).add(discount);
                }

                // 【循环】active.matchedGoods：标记参与本满赠活动的 SKU
                for (FullGiftCalculateVo.MatchedGoods matched : active.getMatchedGoods()) {
                    Integer skuId = matched.getSkuId();
                    if (vo.getGoodsData().containsKey(skuId)) {
                        vo.getGoodsData().get(skuId).setFullGift(active);
                    }
                }

                // 克隆活动摘要（去掉 giftList/matchedGoods 避免冗余）
                FullGiftCalculateVo cloned = BeanUtil.copyProperties(active, FullGiftCalculateVo.class);
                cloned.setGiftList(null);
                cloned.setMatchedGoods(null);
                processedActiveList.add(cloned);
            }
        }

        // 始终回写赠品 SKU 列表与活动摘要（无活动时为空列表）
        vo.getBasic().setFullGiftSkuList(giftSkuList);
        vo.getBasic().setFullGiftList(processedActiveList);
    }

    /**
     * 提交订单前按当前时间重算满减/满赠，清除缓存中已过期的优惠与赠品。
     */
    public void recheckFullActiveBeforeCreate(OrderCreateDataVo vo) {
        // 提交前按当前时间重算满减/满赠：先剥离旧数据再计算，最后刷新应付金额
        if (vo == null) {
            return;
        }
        stripFullReductionDiscount(vo);
        calculateFullReduction(vo, new OrderCalculateParam());
        stripFullGiftDiscount(vo);
        calculateFullGift(vo, new OrderCalculateParam());
        refreshBasicOrderMoney(vo);
    }

    /**
     * 剥离订单中已缓存的满减优惠（SKU、basic、discount 明细）。
     */
    protected void stripFullReductionDiscount(OrderCreateDataVo vo) {
        // 重算满减前剥离 SKU/basic/discount/goodsDiscount/mallData 中的旧满减金额与明细
        if (vo.getGoodsData() != null) {
            // 【循环】goodsData：从 discountMoney 扣回 fullReductionMoney，清零满减字段
            for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                BigDecimal fr = sku.getFullReductionMoney() != null ? sku.getFullReductionMoney() : BigDecimal.ZERO;
                if (fr.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal discountMoney = sku.getDiscountMoney() != null ? sku.getDiscountMoney() : BigDecimal.ZERO;
                    sku.setDiscountMoney(discountMoney.subtract(fr));
                }
                sku.setFullReductionMoney(BigDecimal.ZERO);
                sku.setFullReduction(null);
            }
        }
        if (vo.getBasic() != null) {
            BigDecimal basicFr = vo.getBasic().getFullReductionMoney() != null ? vo.getBasic().getFullReductionMoney() : BigDecimal.ZERO;
            if (basicFr.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountMoney = vo.getBasic().getDiscountMoney() != null ? vo.getBasic().getDiscountMoney() : BigDecimal.ZERO;
                vo.getBasic().setDiscountMoney(discountMoney.subtract(basicFr));
            }
            vo.getBasic().setFullReductionMoney(BigDecimal.ZERO);
        }
        if (vo.getDiscount() != null) {
            vo.getDiscount().entrySet().removeIf(e -> isFullReductionDiscountValue(e.getValue()));
        }
        if (vo.getGoodsDiscount() != null) {
            vo.getGoodsDiscount().entrySet().removeIf(e -> isFullReductionDiscountValue(e.getValue()));
        }
        if (vo.getMallData() != null) {
            Map<String, OrderCreateDataVo.Discount> mallDiscount = getTypedValue(
                    vo.getMallData(),
                    "discount",
                    new TypeReference<Map<String, OrderCreateDataVo.Discount>>() {
                    },
                    new HashMap<>()
            );
            mallDiscount.remove(CommonActiveEnum.FULL_REDUCTION.getType());
        }
    }

    /**
     * 剥离订单中已缓存的满赠优惠与赠品列表。
     */
    protected void stripFullGiftDiscount(OrderCreateDataVo vo) {
        // 重算满赠前清除 discount/goodsDiscount/mallData 满赠明细、SKU fullGift 及 basic 赠品列表
        if (vo.getDiscount() != null) {
            vo.getDiscount().entrySet().removeIf(e -> isFullGiftDiscountValue(e.getValue()));
        }
        if (vo.getGoodsDiscount() != null) {
            vo.getGoodsDiscount().entrySet().removeIf(e -> isFullGiftDiscountValue(e.getValue()));
        }
        if (vo.getMallData() != null) {
            Map<String, List<OrderCreateDataVo.Discount>> mallDiscount = getTypedValue(
                    vo.getMallData(),
                    "discount",
                    new TypeReference<Map<String, List<OrderCreateDataVo.Discount>>>() {
                    },
                    new HashMap<>()
            );
            mallDiscount.remove(CommonActiveEnum.FULL_GIFT.getType());
        }
        if (vo.getGoodsData() != null) {
            // 【循环】goodsData：清零各 SKU 的 fullGift 活动标记
            for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                sku.setFullGift(null);
            }
        }
        if (vo.getBasic() != null) {
            vo.getBasic().setFullGiftSkuList(new ArrayList<>());
            vo.getBasic().setFullGiftList(new ArrayList<>());
        }
        vo.setGiftGoods(null);
    }

    /**
     * 重算子单应付金额（calculate 阶段，不含平台 mall_discount）。
     */
    protected void refreshShopOrderMoney(OrderCreateDataVo vo) {
        // 子单 calculate 阶段重算 order_money（不含 mall_discount）
        refreshShopOrderMoneyStatic(vo);
    }

    public static void refreshShopOrderMoneyStatic(OrderCreateDataVo vo) {
        // order_money = goods + delivery - discount
        if (vo == null || vo.getBasic() == null) {
            return;
        }
        OrderCreateDataVo.Basic basic = vo.getBasic();
        BigDecimal discountMoney = formatOrderMoney(basic.getDiscountMoney());
        BigDecimal deliveryMoney = formatOrderMoney(basic.getDeliveryMoney());
        BigDecimal goodsMoney = formatOrderMoney(basic.getGoodsMoney());
        BigDecimal orderMoney = formatOrderMoney(calculateOrderMoney(deliveryMoney, goodsMoney, discountMoney.negate()));
        basic.setOrderMoney(orderMoney.max(BigDecimal.ZERO));
        basic.setDiscountMoney(discountMoney);
        basic.setDeliveryMoney(deliveryMoney);
        basic.setGoodsMoney(goodsMoney);
        basic.setFullReductionMoney(formatOrderMoney(basic.getFullReductionMoney()));
    }

    /**
     * 重算子单应付金额（与 calculate 末尾一致）。
     */
    protected void refreshBasicOrderMoney(OrderCreateDataVo vo) {
        // 子单应付金额重算（含 mall_discount）
        refreshBasicOrderMoneyStatic(vo);
    }

    /**
     * 统一公式：order_money = goods + delivery - discount - mall_discount
     */
    public static void refreshBasicOrderMoneyStatic(OrderCreateDataVo vo) {
        // order_money = goods + delivery - discount - mall_discount
        if (vo == null || vo.getBasic() == null) {
            return;
        }
        OrderCreateDataVo.Basic basic = vo.getBasic();
        BigDecimal discountMoney = formatOrderMoney(basic.getDiscountMoney());
        BigDecimal deliveryMoney = formatOrderMoney(basic.getDeliveryMoney());
        BigDecimal goodsMoney = formatOrderMoney(basic.getGoodsMoney());
        BigDecimal mallDiscountMoney = formatOrderMoney(basic.getMallDiscountMoney());
        BigDecimal orderMoney = formatOrderMoney(calculateOrderMoney(deliveryMoney, goodsMoney, discountMoney.negate(), mallDiscountMoney.negate()));
        basic.setOrderMoney(orderMoney.max(BigDecimal.ZERO));
        basic.setDiscountMoney(discountMoney);
        basic.setDeliveryMoney(deliveryMoney);
        basic.setGoodsMoney(goodsMoney);
        basic.setFullReductionMoney(formatOrderMoney(basic.getFullReductionMoney()));
    }

    private boolean isFullReductionDiscountValue(Object value) {
        // 判断 discount 明细是否为满减类型
        OrderCreateDataVo.Discount discount = toDiscount(value);
        return discount != null && CommonActiveEnum.FULL_REDUCTION.getType().equals(discount.getDiscountType());
    }

    private boolean isFullGiftDiscountValue(Object value) {
        // 判断 discount 明细是否为满赠类型
        OrderCreateDataVo.Discount discount = toDiscount(value);
        return discount != null && CommonActiveEnum.FULL_GIFT.getType().equals(discount.getDiscountType());
    }

    private OrderCreateDataVo.Discount toDiscount(Object raw) {
        // 缓存反序列化后 discount 可能为 JSONObject，统一转为 Discount
        if (raw == null) {
            return null;
        }
        if (raw instanceof OrderCreateDataVo.Discount d) {
            return d;
        }
        return JSONUtil.toBean(JSONUtil.parseObj(raw), OrderCreateDataVo.Discount.class);
    }

    private static BigDecimal formatOrderMoney(BigDecimal money) {
        // 金额转分后向下取整，再还原为元
        if (money == null) {
            return BigDecimal.ZERO;
        }
        return money.multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.FLOOR)
                .divide(new BigDecimal("100"), 2, RoundingMode.UNNECESSARY);
    }

    private static BigDecimal calculateOrderMoney(BigDecimal... amounts) {
        // 各金额先转分再累加，避免 BigDecimal 直接相加的精度问题
        BigDecimal totalInCents = BigDecimal.ZERO;
        // 【循环】amounts：逐项转分累加到 totalInCents
        for (BigDecimal amount : amounts) {
            BigDecimal amountInCents = (amount != null ? amount : BigDecimal.ZERO).multiply(new BigDecimal("100"));
            totalInCents = totalInCents.add(amountInCents);
        }
        return totalInCents.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public static <T> T getTypedValue(Map<String, Object> mallData, String key, TypeReference<T> typeRef, T defaultObject) {
        // mallData 中嵌套 Map/List 类型安全读取
        Object raw = mallData.get(key);
        if (raw == null) {
            mallData.put(key, defaultObject);
            return defaultObject;
        }
        // 直接序列化再反序列化，保证类型正确
        String jsonStr = JSONUtil.toJsonStr(raw);
        T result = JSONUtil.toBean(jsonStr, typeRef.getType(), false);
        // 缓存结果，下次直接使用
        mallData.put(key, result);
        return result;
    }

    /**
     * 构建赠品 SKU 详细信息（价格、库存、图片等）
     */
    private Map<String, Object> buildGiftSkuInfo(FullGiftCalculateVo.GiftSkuData gift) {
        // 查 SKU/商品表补全赠品展示字段，写入 full_gift_sku_list
        Map<String, Object> giftInfo = new HashMap<>();
        MallGoodsSku sku = mallGoodsSkuMapper.selectById(gift.getSkuId());
        if (sku == null) {
            giftInfo.put("sku_id", gift.getSkuId());
            giftInfo.put("num", gift.getNum());
            return giftInfo;
        }
        MallGoods goods = mallGoodsMapper.selectById(sku.getGoodsId());
        giftInfo.put("goods_id", gift.getGoodsId());
        giftInfo.put("goods", goods);
        giftInfo.put("sku_id", gift.getSkuId());
        giftInfo.put("num", gift.getNum());
        giftInfo.put("sku_name", sku.getSkuName());
        giftInfo.put("sku_image", sku.getSkuImage());
        giftInfo.put("price", sku.getPrice());
        giftInfo.put("stock", sku.getStock());
        giftInfo.put("goods_name", goods != null ? goods.getGoodsName() : "");
        giftInfo.put("goods_image", goods != null ? goods.getGoodsCover() : "");
        giftInfo.put("goods_type", goods != null ? goods.getGoodsType() : "");
        giftInfo.put("goods_type_name", goods != null ? GoodsTypeEnum.getNameByType(goods.getGoodsType()) : "");
        return giftInfo;
    }

    public Map<String, Object> orderGoodsDiscountFormat(String type, String discountType,
                                                        Integer num, BigDecimal money,
                                                        Integer orderDiscountId,
                                                        String content, String title) {
        // 组装订单项优惠 Map，供落库或序列化
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("discount_type", discountType);
        result.put("num", num);
        result.put("money", money);
        result.put("order_discount_id", orderDiscountId);
        result.put("content", content);
        result.put("title", title == null ? "" : title);
        return result;
    }


    protected void addFormData(OrderCreateDataVo vo, JSONObject orderFormData, JSONObject goodsFormData) {
        // 订单级/商品级万能表单写入 diy_form_records 并回写 form_record_id 到 mall_order/mall_order_goods
        Integer orderId = vo.getOrderId();
        Integer siteId = vo.getSiteId();
        Integer memberId = vo.getMemberId();

        if (ObjectUtil.isNotEmpty(orderFormData)) {
            orderFormData.put("member_id", memberId);
            orderFormData.put("relate_id", orderId);

            DiyFormRecordsParam checkParam = new DiyFormRecordsParam();
            checkParam.setMemberId(memberId);
            checkParam.setRelateId(orderId);
            checkParam.setFormId(orderFormData.getInt("form_id"));
            if (ObjectUtil.isEmpty(coreDiyFormRecordsService.check(checkParam))) {
                DiyFormRecordsParam orderParam = new DiyFormRecordsParam();
                orderParam.setMemberId(memberId);
                orderParam.setRelateId(orderId);
                orderParam.setFormId(orderFormData.getInt("form_id"));
                orderParam.setValue(orderFormData.getJSONArray("value"));

                Integer formRecordId = coreDiyFormRecordsService.add(orderParam);
                if (formRecordId != null) {
                    MallOrder mallOrder = mallOrderMapper.selectById(orderId);
                    mallOrder.setFormRecordId(formRecordId);
                    mallOrderMapper.updateById(mallOrder); // 回写订单 form_record_id
                }
            }
        }

        if (ObjectUtil.isNotEmpty(goodsFormData)) {
            List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper.selectList(
                    new QueryWrapper<MallOrderGoods>()
                            .eq("order_id", orderId)
                            .select("order_goods_id", "sku_id", "supply_id")
            );

            // 【外层循环】goodsFormData：key=skuId，value=该 SKU 的表单数据
            for (String key : goodsFormData.keySet()) {
                JSONObject goodsFormItem = goodsFormData.getJSONObject(key);
                if (ObjectUtil.isNotEmpty(goodsFormItem)) {
                    // 【内层循环】orderGoodsList：按 skuId 匹配订单商品行并落库表单
                    for (MallOrderGoods orderGoods : orderGoodsList) {
                        if (key.equals(orderGoods.getSkuId().toString())) {
                            goodsFormItem.put("member_id", memberId);
                            goodsFormItem.put("relate_id", orderGoods.getOrderGoodsId());

                            DiyFormRecordsParam checkParam = new DiyFormRecordsParam();
                            checkParam.setMemberId(memberId);
                            checkParam.setRelateId(orderGoods.getOrderGoodsId());
                            checkParam.setFormId(goodsFormItem.getInt("form_id"));
                            if (ObjectUtil.isEmpty(coreDiyFormRecordsService.check(checkParam))) {
                                DiyFormRecordsParam goodsParam = new DiyFormRecordsParam();
                                goodsParam.setMemberId(memberId);
                                goodsParam.setRelateId(orderGoods.getOrderGoodsId());
                                goodsParam.setFormId(goodsFormItem.getInt("form_id"));
                                goodsParam.setValue(goodsFormItem.getJSONArray("value"));

                                Integer formRecordId = coreDiyFormRecordsService.add(goodsParam);
                                if (formRecordId != null) {
                                    orderGoods.setFormRecordId(formRecordId);
                                    mallOrderGoodsMapper.updateById(orderGoods); // 回写订单商品 form_record_id
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 优惠写入（订单维度）
     */
    protected void useDiscount(OrderCreateDataVo vo) {
        // 按优惠类型依次核销（店铺券）并组装 mall_order_discount 批量落库
        if (ObjectUtil.isEmpty(vo.getDiscount())) {
            return;
        }
        List<MallOrderDiscount> insertDiscountData = new ArrayList<>();

        // ---------- 店铺券：isCan=true 时核销会员券，再写入优惠记录 ----------
        String couponType = OrderDiscountTypeEnum.COUPON.getType();
        OrderCreateDataVo.Discount coupon = vo.getDiscount().get(couponType);
        if (coupon == null) {
            coupon = findDiscountByType(vo, couponType);
        }
        if (coupon != null && !handleDiscountByEvent(vo, coupon)) {
            if (Boolean.TRUE.equals(coupon.getIsCan()) && coupon.getDiscountTypeId() != null) {
                coreCouponMemberService.use(coupon.getDiscountTypeId(), vo.getOrderId()); // 核销会员券
            }
            insertDiscountData.add(getDiscountInfo(vo, coupon));
        }

        // ---------- 平台券：本层仅落优惠记录（核销在 mall 统筹层） ----------
        String platformCouponType = OrderDiscountTypeEnum.PLATFORM_COUPON.getType();
        OrderCreateDataVo.Discount platformCoupon = vo.getDiscount().get(platformCouponType);
        if (platformCoupon != null && !handleDiscountByEvent(vo, platformCoupon)) {
            insertDiscountData.add(getDiscountInfo(vo, platformCoupon));
        }

        // ---------- 满减汇总项：聚合槽位 money>0 才落库 ----------
        String fullReductionType = OrderDiscountTypeEnum.FULL_REDUCTION.getType();
        OrderCreateDataVo.Discount fullReduction = vo.getDiscount().get(fullReductionType);
        if (isAggregatedDiscountNotEmpty(fullReduction) && !handleDiscountByEvent(vo, fullReduction)) {
            insertDiscountData.add(getDiscountInfo(vo, fullReduction));
        }

        // ---------- 积分抵扣汇总项：同上，只处理聚合槽位 ----------
        String pointDeductType = OrderDiscountTypeEnum.POINT_DEDUCT.getType();
        OrderCreateDataVo.Discount pointDeduct = vo.getDiscount().get(pointDeductType);
        if (isAggregatedDiscountNotEmpty(pointDeduct) && !handleDiscountByEvent(vo, pointDeduct)) {
            insertDiscountData.add(getDiscountInfo(vo, pointDeduct));
        }

        // ---------- 满赠：每条 full_gift 明细单独落库（可多条赠品） ----------
        String fullGiftType = OrderDiscountTypeEnum.FULL_GIFT.getType();
        // 【循环】discount.values：筛选满赠类型明细逐条落库
        for (OrderCreateDataVo.Discount item : vo.getDiscount().values()) {
            if (!fullGiftType.equals(item.getDiscountType())) {
                continue;
            }
            if (!handleDiscountByEvent(vo, item)) {
                insertDiscountData.add(getDiscountInfo(vo, item));
            }
        }

        if (!insertDiscountData.isEmpty()) {
            coreOrderDiscountService.inserAll(insertDiscountData); // 批量插入 mall_order_discount
        }
    }

    private OrderCreateDataVo.Discount findDiscountByType(OrderCreateDataVo vo, String discountType) {
        // 扁平 discount Map 中按 discountType 查找首条匹配项
        // 【循环】discount.values：按 discountType 匹配返回
        for (OrderCreateDataVo.Discount item : vo.getDiscount().values()) {
            if (discountType.equals(item.getDiscountType())) {
                return item;
            }
        }
        return null;
    }

    private boolean isAggregatedDiscountNotEmpty(OrderCreateDataVo.Discount discount) {
        // 汇总满减/积分优惠 money>0 才落库
        return discount != null
                && discount.getMoney() != null
                && discount.getMoney().compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean handleDiscountByEvent(OrderCreateDataVo vo, OrderCreateDataVo.Discount item) {
        // 扩展点：listener 处理则跳过默认落库
        ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEvent event = new ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEvent();
        event.setSiteId(vo.getSiteId());
        event.addAppSign("mall");
        event.setName("ShopOrderDiscountCreateEvent");
        event.setDiscount(item);
        event.setOrderCreateData(vo);
        List<ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEventResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);
        return ObjectUtil.isNotEmpty(eventData);
    }

    /**
     * 将 Discount VO 转为 mall_order_discount 实体。
     */
    private MallOrderDiscount getDiscountInfo(OrderCreateDataVo vo, OrderCreateDataVo.Discount datum) {
        // 填充订单优惠表字段
        MallOrderDiscount orderDiscount = new MallOrderDiscount();
        orderDiscount.setType(datum.getType());
        orderDiscount.setNum(datum.getNum() != null ? datum.getNum() : 0);
        orderDiscount.setMoney(datum.getMoney() != null ? datum.getMoney() : BigDecimal.ZERO);
        orderDiscount.setDiscountType(datum.getDiscountType());
        orderDiscount.setDiscountTypeId(datum.getDiscountTypeId() != null ? datum.getDiscountTypeId() : 0);
        orderDiscount.setContent(datum.getContent() != null ? datum.getContent() : "");
        orderDiscount.setOrderId(vo.getOrderId());
        orderDiscount.setSiteId(vo.getSiteId());
        orderDiscount.setCreateTime(System.currentTimeMillis() / 1000);
        return orderDiscount;
    }

    /**
     * 使用订单项优惠，写入 mall_order_discount_goods。
     *
     * @param vo
     * @param orderGoodsList 订单商品列表（包含orderGoodsId）
     */
    protected void useGoodsDiscount(OrderCreateDataVo vo, List<MallOrderGoods> orderGoodsList) {
        // 按 matchGoodsIds 匹配 SKU，逐行插入 mall_order_discount_goods
        if (ObjectUtil.isEmpty(vo.getGoodsDiscount())) {
            return;
        }
        // 【外层循环】goodsDiscount：遍历各商品级优惠明细
        for (var item : vo.getGoodsDiscount().entrySet()) {
            var key = item.getKey();
            var value = item.getValue();
            // 【内层循环】orderGoodsList：matchGoodsIds 含该 SKU 则落库商品级优惠
            for (MallOrderGoods mallOrderGoods : orderGoodsList) {

                if (Arrays.stream(value.getMatchGoodsIds()).anyMatch(num -> num == mallOrderGoods.getSkuId())) {
                    MallOrderDiscountGoods discountGoods = buildOrderGoodsDiscount(vo, value, mallOrderGoods.getOrderGoodsId());
                    mallOrderDiscountGoodsMapper.insert(discountGoods);
                }


            }

        }
    }

    private MallOrderDiscountGoods buildOrderGoodsDiscount(OrderCreateDataVo vo, OrderCreateDataVo.Discount item, Integer orderGoodsId) {
        // 组装 mall_order_discount_goods 实体
        MallOrderDiscountGoods discountGoods = new MallOrderDiscountGoods();
        discountGoods.setSiteId(vo.getSiteId());
        discountGoods.setOrderId(vo.getOrderId());
        discountGoods.setOrderGoodsId(orderGoodsId.toString());
        discountGoods.setType(item.getType());
        discountGoods.setDiscountType(item.getDiscountType());
        discountGoods.setOrderDiscountId(item.getOrderDiscountId());
        discountGoods.setTitle(item.getTitle() != null ? item.getTitle() : "");
        discountGoods.setNum(item.getNum() != null ? item.getNum() : 0);
        discountGoods.setMoney(item.getMoney() != null ? item.getMoney() : BigDecimal.ZERO);
        discountGoods.setContent(item.getContent() != null ? item.getContent() : "");
        discountGoods.setCreateTime(System.currentTimeMillis() / 1000);
        return discountGoods;
    }

    /**
     * 创建订单
     *
     * @param vo
     * @return
     */
    protected OrderCreateResultVo createOrder(OrderCreateDataVo vo, MallOrder order, List<MallOrderGoods> orderGoods,
                                              List<MallOrderGiftGoods> orderGiftGoods) {
        // 落库订单主表→商品行→赠品行→订单优惠→商品级优惠→万能表单，返回 orderId
        order.setCreateTime(System.currentTimeMillis() / 1000);
        mallOrderMapper.insert(order);
        vo.setOrderId(order.getOrderId());

        // ---------- 写入 mall_order_goods（主商品；赠品走 mall_order_gift_goods） ----------
        // 【循环】orderGoods：计算行实付并插入订单商品表
        for (MallOrderGoods item : orderGoods) {
            item.setOrderId(order.getOrderId());
            OrderCreateDataVo.Sku sku = new OrderCreateDataVo.Sku();
            sku.setGoodsMoney(item.getGoodsMoney());
            sku.setDiscountMoney(item.getDiscountMoney());
            sku.setMallDiscountMoney(item.getMallDiscountMoney());
            item.setOrderGoodsMoney(calculateOrderGoodsMoney(sku));
            item.setStatus(1);
            mallOrderGoodsMapper.insert(item);
        }

        List<MallOrderGiftGoods> giftRows = orderGiftGoods != null ? orderGiftGoods : Collections.emptyList();
        if (ObjectUtil.isNotEmpty(giftRows)) {
            // 【循环】giftRows：补全默认值并插入赠品行
            for (MallOrderGiftGoods gift : giftRows) {
                gift.setOrderId(order.getOrderId());
                if (gift.getSiteId() == null) {
                    gift.setSiteId(vo.getSiteId());
                }
                if (gift.getMemberId() == null) {
                    gift.setMemberId(vo.getMemberId());
                }
                if (gift.getPrice() == null) {
                    gift.setPrice(BigDecimal.ZERO);
                }
                if (gift.getGoodsMoney() == null) {
                    gift.setGoodsMoney(BigDecimal.ZERO);
                }
                mallOrderGiftGoodsMapper.insert(gift);
            }
        }

        this.useDiscount(vo); // 订单级优惠落库

        this.useGoodsDiscount(vo, orderGoods); // 商品级优惠落库

        JSONObject orderFormData = null;
        JSONObject goodsFormData = null;

        if (ObjectUtil.isNotEmpty(vo.getFormData())) {
            orderFormData = vo.getFormData().getJSONObject("order");

            goodsFormData = vo.getFormData().getJSONObject("goods");
        }

        this.addFormData(vo, orderFormData, goodsFormData); // 万能表单落库

        OrderCreateResultVo result = new OrderCreateResultVo();
        result.setTradeType(vo.getOrderType());
        result.setOrderId(order.getOrderId());
        return result;
    }
}
