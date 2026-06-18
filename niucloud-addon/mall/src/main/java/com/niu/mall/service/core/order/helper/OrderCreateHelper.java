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
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.coupon.MallCouponMember;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.entity.order.*;
import com.niu.mall.enums.active.ActiveClassEnum;
import com.niu.mall.enums.common.CommonActiveEnum;
import com.niu.mall.enums.coupon.CouponMemberStatusEnum;
import com.niu.mall.enums.coupon.CouponTypeEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.event.order.OrderCreateAfterEvent;
import com.niu.mall.event.order.ShopOrderDiscountCreateEventDefiner;
import com.niu.mall.mapper.coupon.MallCouponGoodsMapper;
import com.niu.mall.mapper.coupon.MallCouponMemberMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.order.MallOrderGiftGoodsMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.delivery.ICoreExpressService;
import com.niu.mall.service.core.delivery.ICoreLocalDeliveryService;
import com.niu.mall.service.core.delivery.ICoreStoreService;
import com.niu.mall.service.core.marketing.ICoreCouponMemberService;
import com.niu.mall.service.core.marketing.ICoreFullGiftService;
import com.niu.mall.service.core.marketing.ICoreFullReductionService;
import com.niu.mall.service.core.marketing.param.FullGiftCalculateParam;
import com.niu.mall.service.core.marketing.param.FullReductionCalculateParam;
import com.niu.mall.service.core.marketing.vo.FullGiftCalculateVo;
import com.niu.mall.service.core.marketing.vo.FullReductionCalculateVo;
import com.niu.mall.service.core.order.*;
import com.niu.mall.service.core.order.param.OrderCalculateParam;
import com.niu.mall.service.core.order.param.OrderHelperParam;
import com.niu.mall.service.core.order.vo.*;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
        if (ObjectUtil.isEmpty(vo.getOrderKey())) vo.setOrderKey(createOrderNo());

        Cached cache = CacheFactory.getCacheOperator();
        cache.put(RequestUtils.memberId() + "_member_order_" + vo.getOrderKey(), JSONUtil.parseObj(vo).toString());
    }

    /**
     * 与 CoreOrderCreateService::confirm 中 order_key + "_basic" 快照一致，供 calculate 二次进入时 getOrderCache 等价读取。
     */
    protected <T extends OrderCreateDataVo> void cacheOrderVoBasic(T vo) {
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
        OrderCreateDataVo.Basic basic = vo.getBasic();
        if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
            List<DeliveryConfigVo> deliveryList = coreDeliveryConfigService.getDeliveryConfig(param.getSiteId()).stream().filter(i -> i.getStatus().equals(1)).collect(Collectors.toList());
            Map<String, DeliveryConfigVo> map = new LinkedHashMap<>();
            if (ObjectUtil.isNotEmpty(basic.getHasDeliveryTypes())) {
                for (DeliveryConfigVo conf : deliveryList) {
                    if (conf.getStatus() == 1 && basic.getHasDeliveryTypes().contains(conf.getKey())) {
                        map.put(conf.getKey(), conf);
                    }
                }
            }
            vo.getDelivery().setDeliveryTypeList(map);
            if (map.containsKey(DeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                DeliveryLocalDelivery deliveryLocalDelivery = deliveryLocalDeliveryMapper.selectOne(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", param.getSiteId()).last("limit 1"));
                vo.getDelivery().setLocalDeliveryConfig(deliveryLocalDelivery);
            }
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
        OrderCreateDataVo.Basic basic = vo.getBasic();
        String deliveryType = ObjectUtil.isNotEmpty(param.getDelivery()) ? param.getDelivery().getDeliveryType() : "";
        if (param.getOrderFrom().equals("pc")) deliveryType = OrderDeliveryTypeEnum.EXPRESS.getType();
        basic.setDeliveryMoney(new BigDecimal(0));
        if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
            if (ObjectUtil.isEmpty(vo.getDelivery().getDeliveryTypeList())) {
                vo.getError().add(vo.getSiteInfo().getSiteName() + "未设置配送方式");
                vo.getDelivery().setError("商家未设置配送方式");
            }
            if (!deliveryType.isEmpty()) {
                //校验合法
                if (!vo.getOrderFrom().equals("pc")) {
                    if (!vo.getDelivery().getDeliveryTypeList().containsKey(deliveryType))
                        throw new CommonException("请选择正确的配送方式");
                }
            } else {
                if (!vo.getDelivery().getDeliveryTypeList().isEmpty()) {
                    deliveryType = vo.getDelivery().getDeliveryTypeList().entrySet().iterator().next().getKey();
                }
            }

        } else {
            if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.VIRTUAL.getType())) {
                deliveryType = OrderDeliveryTypeEnum.VIRTUAL.getType();
            }
        }


        if (deliveryType.isEmpty()) {
            vo.getError().add("请选择正确的配送方式");
        } else {
            vo.getDelivery().setDeliveryType(deliveryType);
            //选中收货地址
            selectTakeAddress(vo, mallOrderCreateDataVo);
            //选择配送/自提门店
            selectTakeStore(deliveryType, vo, param);
            if (deliveryType.equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
                coreExpressService.calculate(vo);
            }
            if (deliveryType.equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                coreLocalDeliveryService.calculate(vo);
            }
            if (deliveryType.equals(OrderDeliveryTypeEnum.STORE.getType())) {
                coreStoreService.calculate(vo);
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
        //查询购物配置
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
                case "order":
                    OrderConfigVo orderConfig = coreOrderConfigService.getConfig();
                    OrderCreateDataVo.ConfigInfo.CloseOrder closeOrder = new OrderCreateDataVo.ConfigInfo.CloseOrder();
                    closeOrder.setIsClose(orderConfig.getCloseOrderInfo().getIsClose());
                    closeOrder.setCloseLength(orderConfig.getCloseOrderInfo().getCloseLength());
                    config.setCloseOrderInfo(closeOrder);
                    break;
                case "point":
                    break;
                case "delivery_type":
                    List<DeliveryConfigVo> deliveryList = coreDeliveryConfigService.getDeliveryConfig(siteId);
                    Map<String, Object> map = new LinkedHashMap<>();
                    for (DeliveryConfigVo Obj : deliveryList) {
                        map.put(Obj.getKey(), Obj);
                    }
                    config.setDeliveryType(map);
                    break;
                case "invoice":
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

        OrderHelperParam.Delivery delivery = param.getDelivery();
        OrderHelperParam.Position position = param.getPosition();
        vo.getDelivery().setTakeStore("[]");
        if (LocalDeliveryStoreTypeEnum.getMap().containsKey(deliveryType)) {

            QueryWrapper<DeliveryStore> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("site_id", vo.getSiteId()).eq("status", 1);
            if (LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType().equals(deliveryType)) {

                queryWrapper.eq("support_local_delivery", 1);
            } else if (LocalDeliveryStoreTypeEnum.STORE.getType().equals(deliveryType)) {

                queryWrapper.eq("support_store", 1);
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
                for (OrderCreateDataVo.TakeStore item : list) {
                    Double distance = DistanceCalculateUtils.calculateDistance(
                            Double.parseDouble(position.getLatitude()),
                            Double.parseDouble(position.getLongitude()),
                            Double.parseDouble(item.getLatitude()),
                            Double.parseDouble(item.getLongitude())
                    );
                    item.setDistance(new BigDecimal(distance).setScale(3, RoundingMode.HALF_UP));
                }
                list = list.stream().sorted(Comparator.comparing(OrderCreateDataVo.TakeStore::getDistance)).toList();
            }
            vo.getDelivery().setTakeStoreList(list);
            if (ObjectUtil.isEmpty(delivery.getTakeStoreId()) || (ObjectUtil.isNotEmpty(delivery.getTakeStoreId()) && delivery.getTakeStoreId() == 0)) {
                if (!list.isEmpty()) {
                    vo.getDelivery().setTakeStore(list.get(0));
                }
            } else {
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
                    deliveryStoreVo.setDistance(listThis.get(0).getDistance());
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
        vo.getBasic().setDiscountMoney(new BigDecimal(0));
        for (Integer key : vo.getGoodsData().keySet()) {
            vo.getGoodsData().get(key).setDiscountMoney(new BigDecimal(0));
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
            // 通用券
            if (memberCoupon.getType().equals(CouponTypeEnum.ALL.getType().toString())) {
                matchGoods = vo.getGoodsData().values().stream().collect(Collectors.toList());
                matchGoodsMoney = vo.getBasic().getGoodsMoney();
                matchOrderGoodsMoney = vo.getBasic().getGoodsMoney().subtract(vo.getBasic().getDiscountMoney());
            }
            // 品类券
            if (memberCoupon.getType().equals(CouponTypeEnum.CATEGORY.getType().toString())) {
                List<Integer> categoryIds = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                        .eq("coupon_id", memberCoupon.getCouponId())
                        .select("category_id")).stream().map(i -> i.getCategoryId()).collect(Collectors.toList());
                matchGoodsMoney = BigDecimal.ZERO;
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
            // 商品券
            if (memberCoupon.getType().equals(CouponTypeEnum.GOODS.getType().toString())) {
                List<Integer> goodsIds = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                        .eq("coupon_id", memberCoupon.getCouponId())
                        .select("goods_id")).stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
                matchGoodsMoney = BigDecimal.ZERO;
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
                    BigDecimal couponMoney = memberCoupon.getPrice().compareTo(matchOrderGoodsMoney) > 0 ? matchOrderGoodsMoney : memberCoupon.getPrice();
                    BigDecimal surplusMoney = couponMoney;
                    List<OrderCreateDataVo.Sku> matchGoodsList = matchGoods.stream().filter(sku -> {
                        Boolean er = (sku.getGoodsMoney().subtract(sku.getDiscountMoney())).compareTo(BigDecimal.ZERO) != 0;
                        return er;
                    }).toList();
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
                    //优惠累增
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
                    vo.getDiscount().put("coupon", discount);
                }
            }
        } else {
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
        // 获取商品金额
        BigDecimal goodsMoney = goods.getGoodsMoney();

        // 获取折扣金额（如果不存在则为0）
        BigDecimal discountMoney = ObjectUtil.isNotEmpty(goods.getDiscountMoney()) ? goods.getDiscountMoney() : BigDecimal.ZERO;

        // 获取商城折扣金额（含积分抵扣；兼容仅写在嵌套 Goods 上的缓存数据）
        BigDecimal mallDiscountMoney = resolveSkuMallDiscountMoney(goods);

        // 减去折扣金额
        BigDecimal orderGoodsMoney = goodsMoney.subtract(discountMoney);

        // 减去商城折扣金额
        orderGoodsMoney = orderGoodsMoney.subtract(mallDiscountMoney);

        // 确保金额不小于0
        if (orderGoodsMoney.compareTo(BigDecimal.ZERO) < 0) {
            orderGoodsMoney = BigDecimal.ZERO;
        }

        return orderGoodsMoney.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 解析 SKU 平台优惠金额（含积分抵扣），兼容积分仅写在嵌套 Goods 上的算价缓存。
     */
    public static BigDecimal resolveSkuMallDiscountMoney(OrderCreateDataVo.Sku sku) {
        BigDecimal mallDiscountMoney = ObjectUtil.isNotEmpty(sku.getMallDiscountMoney()) ? sku.getMallDiscountMoney() : BigDecimal.ZERO;
        if (sku.getGoods() == null) {
            return mallDiscountMoney;
        }
        BigDecimal goodsPointDeduct = ObjectUtil.isNotEmpty(sku.getGoods().getPointDeductMoney())
                ? sku.getGoods().getPointDeductMoney() : BigDecimal.ZERO;
        if (goodsPointDeduct.compareTo(BigDecimal.ZERO) > 0 && mallDiscountMoney.compareTo(goodsPointDeduct) < 0) {
            BigDecimal skuPointDeduct = ObjectUtil.isNotEmpty(sku.getPointDeductMoney()) ? sku.getPointDeductMoney() : BigDecimal.ZERO;
            if (skuPointDeduct.compareTo(BigDecimal.ZERO) == 0) {
                return mallDiscountMoney.add(goodsPointDeduct);
            }
        }
        return mallDiscountMoney;
    }

    protected void calculateFullReduction1(OrderCreateDataVo vo, OrderCalculateParam param) {
        if (ObjectUtil.isEmpty(vo.getGoodsData()) || vo.getGoodsData().isEmpty()) {
            return;
        }

        FullReductionCalculateParam calculateParam = new FullReductionCalculateParam();
        calculateParam.setSiteId(vo.getSiteId());
        calculateParam.setMemberId(vo.getMemberId());
        calculateParam.setCheckRule(true);

        List<FullReductionCalculateParam.GoodsSkuData> goodsSkuDataList = new ArrayList<>();
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            FullReductionCalculateParam.GoodsSkuData goodsSkuData = new FullReductionCalculateParam.GoodsSkuData();
            goodsSkuData.setGoodsId(sku.getGoodsId());
            goodsSkuData.setSkuId(sku.getSkuId());
            goodsSkuData.setPayMoney(sku.getGoodsMoney().subtract(
                    ObjectUtil.isNotEmpty(sku.getDiscountMoney()) ? sku.getDiscountMoney() : BigDecimal.ZERO
            ));
            goodsSkuData.setNum(sku.getNum());
            goodsSkuData.setSkuKey(sku.getGoodsId() + "_" + sku.getSkuId());
            goodsSkuDataList.add(goodsSkuData);
        }
        calculateParam.setGoodsSkuData(goodsSkuDataList);

        List<FullReductionCalculateVo> fullReductionList = coreFullReductionService.getFullReductionInfo(calculateParam);

        if (ObjectUtil.isNotEmpty(fullReductionList) && !fullReductionList.isEmpty()) {
            FullReductionCalculateVo maxDiscountReduction = fullReductionList.stream()
                    .max(Comparator.comparing(vo1 -> vo1.getDiscountAmount() != null ? vo1.getDiscountAmount() : BigDecimal.ZERO))
                    .orElse(null);

            if (maxDiscountReduction != null && maxDiscountReduction.getDiscountAmount() != null && maxDiscountReduction.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountAmount = maxDiscountReduction.getDiscountAmount();
                BigDecimal surplusMoney = discountAmount;

                List<OrderCreateDataVo.Sku> matchedGoodsList = new ArrayList<>();
                for (FullReductionCalculateVo.MatchedGoods matchedGoods : maxDiscountReduction.getMatchedGoods()) {
                    Integer skuId = matchedGoods.getSkuId();
                    if (vo.getGoodsData().containsKey(skuId)) {
                        matchedGoodsList.add(vo.getGoodsData().get(skuId));
                    }
                }

                for (int i = 0; i < matchedGoodsList.size(); i++) {
                    OrderCreateDataVo.Sku sku = matchedGoodsList.get(i);
                    BigDecimal itemOrderGoodsMoney = calculateOrderGoodsMoney(sku);
                    Integer itemSkuId = sku.getSkuId();
                    BigDecimal itemDiscountMoney;

                    if (i == matchedGoodsList.size() - 1) {
                        itemDiscountMoney = surplusMoney;
                    } else {
                        BigDecimal matchOrderGoodsMoney = matchedGoodsList.stream()
                                .map(OrderCreateHelper::calculateOrderGoodsMoney)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        itemDiscountMoney = itemOrderGoodsMoney.divide(matchOrderGoodsMoney, 2, RoundingMode.HALF_UP);
                        itemDiscountMoney = itemDiscountMoney.multiply(discountAmount).setScale(2, RoundingMode.HALF_UP);
                    }

                    vo.getGoodsData().get(itemSkuId).setDiscountMoney(
                            vo.getGoodsData().get(itemSkuId).getDiscountMoney().add(itemDiscountMoney)
                    );
                    surplusMoney = surplusMoney.subtract(itemDiscountMoney);
                }

                vo.getBasic().setDiscountMoney(vo.getBasic().getDiscountMoney().add(discountAmount));

                OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                discount.setMatchGoodsIds(matchedGoodsList.stream().map(OrderCreateDataVo.Sku::getSkuId).toArray(Integer[]::new));
                discount.setNum(1);
                discount.setType("discount");
                discount.setMoney(discountAmount);
                discount.setDiscountType("full_reduction");
                discount.setDiscountTypeId(maxDiscountReduction.getId());
                discount.setTitle(maxDiscountReduction.getTitle());
                discount.setContent("");
                vo.getDiscount().put("full_reduction", discount);
            }
        } else {
            if (vo.getDiscount().get("full_reduction") != null) {
                vo.getDiscount().remove("full_reduction");
            }
        }
    }

    /**
     * 计算满减 (多商品版)
     * 支持满件/满元、阶梯/循环、满减/折扣，按商品实际支付金额比例分摊，并记录商品级和订单级优惠明细。
     */
    protected void calculateFullReduction(OrderCreateDataVo vo, OrderCalculateParam param) {
        if (!Integer.valueOf(1).equals(vo.getIsJoinFullReduction())) {
            return;
        }
        if (ObjectUtil.isEmpty(vo.getGoodsData()) || vo.getGoodsData().isEmpty()) return;

        // 重算前剥离缓存快照中的旧满减，避免 _basic 脏数据导致满减被跳过或金额累加错误
        stripFullReductionDiscount(vo);

        // 1. 构建满减请求参数
        List<FullReductionCalculateParam.GoodsSkuData> goodsSkuDataList = new ArrayList<>();
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            // pay_money = goods_money - discount_money - mall_discount_money (使用截断保留2位)
            BigDecimal payMoney = sku.getGoodsMoney()
                    .subtract(sku.getDiscountMoney() != null ? sku.getDiscountMoney() : BigDecimal.ZERO)
                    .subtract(sku.getMallDiscountMoney() != null ? sku.getMallDiscountMoney() : BigDecimal.ZERO);
            payMoney = payMoney.setScale(2, RoundingMode.DOWN);

            FullReductionCalculateParam.GoodsSkuData data = new FullReductionCalculateParam.GoodsSkuData();
            data.setGoodsId(sku.getGoodsId());
            data.setSkuId(sku.getSkuId());
            data.setNum(sku.getNum());
            data.setPayMoney(payMoney);
            data.setSkuKey(sku.getGoodsId() + "_" + sku.getSkuId());
            goodsSkuDataList.add(data);

            // 初始化商品满减字段
            sku.setFullReduction(null);
            sku.setFullReductionMoney(BigDecimal.ZERO);
        }
        FullReductionCalculateParam calcParam = new FullReductionCalculateParam();
        calcParam.setGoodsSkuData(goodsSkuDataList);
        calcParam.setMemberId(vo.getMemberId());
        calcParam.setSiteId(vo.getSiteId());
        calcParam.setCheckRule(true);

        // 2. 获取所有可用的满减活动
        List<FullReductionCalculateVo> activeList = coreFullReductionService.getFullReductionInfo(calcParam);
        if (activeList == null || activeList.isEmpty()) {
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

        StringBuilder allTitleStr = new StringBuilder();

        // 3. 遍历每个活动
        for (FullReductionCalculateVo active : activeList) {
            List<FullReductionCalculateVo.MatchedGoods> matchedGoods = active.getMatchedGoods();
            ObjectMapper objectMapper = new ObjectMapper();

            List<Map<String, Object>> ruleJson = null; // 假设规则列表
            try {
                ruleJson = objectMapper.readValue(active.getRuleJson(), new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            String type = active.getType();               // amount/num
            String ruleType = active.getRuleType();       // LADDER / CYCLE
            String promotionType = active.getPromotionType(); // REDUCTION / DISCOUNT
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
            for (FullReductionCalculateVo.MatchedGoods goods : matchedGoods) {
                goodsTotalPrice = goodsTotalPrice.add(goods.getPayMoney());
            }

            // 4. 计算本次活动总优惠金额 fullReductionMoney
            BigDecimal fullReductionMoney = coreFullReductionService.calculateFullReductionDiscount(
                    type, ruleType, promotionType, ruleJson, matchedTotal, goodsTotalPrice);

            if (fullReductionMoney.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // 5. 按商品实际支付金额占比分摊优惠
            BigDecimal allPayMoney = BigDecimal.ZERO;
            for (FullReductionCalculateVo.MatchedGoods goods : matchedGoods) {
                allPayMoney = allPayMoney.add(goods.getPayMoney());
            }

            BigDecimal calculateFullReductionMoney = BigDecimal.ZERO;
            for (FullReductionCalculateVo.MatchedGoods goods : matchedGoods) {
                Integer skuId = goods.getSkuId();
                BigDecimal goodsPayMoney = goods.getPayMoney();
                // 比例 = goodsPayMoney / allPayMoney，保留4位小数（截断）
                BigDecimal payMoneyRate = goodsPayMoney.divide(allPayMoney, 4, RoundingMode.DOWN);
                // 商品分摊金额 = 比例 * 总优惠，保留2位小数（截断）
                BigDecimal goodsReduce = payMoneyRate.multiply(fullReductionMoney)
                        .setScale(2, RoundingMode.DOWN);
                // 不能超过商品实际金额
                if (goodsReduce.compareTo(goodsPayMoney) > 0) {
                    goodsReduce = goodsPayMoney;
                }

                // 更新商品数据
                OrderCreateDataVo.Sku sku = vo.getGoodsData().get(skuId);
                sku.setFullReduction(active);
                sku.setFullReductionMoney(goodsReduce);
                sku.setDiscountMoney(sku.getDiscountMoney().add(goodsReduce));
                calculateFullReductionMoney = calculateFullReductionMoney.add(goodsReduce);

                String uniqueKey = sku.getSiteId() + "_" + sku.getGoodsId() + "_" + skuId + ":" + CommonActiveEnum.FULL_REDUCTION.getType();

                // 记录订单商品级折扣
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
                // 记录订单级折扣
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

            // 6. 累加订单总满减优惠和总折扣
            vo.getBasic().setFullReductionMoney(vo.getBasic().getFullReductionMoney().add(calculateFullReductionMoney));
            vo.getBasic().setDiscountMoney(vo.getBasic().getDiscountMoney().add(calculateFullReductionMoney));
        }

        // 7. 如果有任何满减优惠，在 mall_data 中记录一条汇总优惠（用于多站点）
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
        // 与 CoreOrderCreateTrait::calculateFullGift 中 !$this->is_join_full_gift 一致（null 视为不参与）
        if (!Integer.valueOf(1).equals(vo.getIsJoinFullGift())) {
            return;
        }

        // 1. 构建满赠计算参数
        List<FullGiftCalculateParam.GoodsSkuData> goodsSkuDataList = new ArrayList<>();
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            FullGiftCalculateParam.GoodsSkuData data = new FullGiftCalculateParam.GoodsSkuData();
            data.setSkuId(sku.getSkuId());
            // pay_money = goods_money - discount_money（不扣除平台优惠，与PHP一致）
            BigDecimal payMoney = sku.getGoodsMoney().subtract(
                    sku.getDiscountMoney() != null ? sku.getDiscountMoney() : BigDecimal.ZERO);
            data.setPayMoney(payMoney);
            data.setGoodsId(sku.getGoodsId());
            data.setNum(sku.getNum());
            data.setSkuKey(sku.getGoodsId() + "_" + sku.getSkuId());
            goodsSkuDataList.add(data);

            // 初始化商品满赠字段
            sku.setFullGift(null);
        }

        FullGiftCalculateParam calcParam = new FullGiftCalculateParam();
        calcParam.setSiteId(vo.getSiteId());
        calcParam.setMemberId(vo.getMemberId());
        calcParam.setCheckRule(true);
        calcParam.setGoodsSkuData(goodsSkuDataList);

        // 2. 获取所有匹配的满赠活动
        List<FullGiftCalculateVo> activeList = coreFullGiftService.getFullGiftInfo(calcParam);

        List<Map<String, Object>> giftSkuList = new ArrayList<>();
        List<FullGiftCalculateVo> processedActiveList = new ArrayList<>();

        if (ObjectUtil.isNotEmpty(activeList)) {
            for (FullGiftCalculateVo active : activeList) {
                // 处理赠品
                for (FullGiftCalculateVo.GiftSkuData gift : active.getGiftList()) {
                    // 补充赠品 SKU 详细信息（价格、库存、图片等）
                    Map<String, Object> giftInfo = buildGiftSkuInfo(gift);
                    giftSkuList.add(giftInfo);

                    // 订单级优惠记录（每个赠品一条）
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

                    // 平台级优惠记录（多站点汇总）
//                    HashMap<String, List<OrderCreateDataVo.Discount>> map = (HashMap<String, List<OrderCreateDataVo.Discount>>)
//                            vo.getMallData().computeIfAbsent("discount", k -> new HashMap<>());
                    Map<String, List<OrderCreateDataVo.Discount>> map = getTypedValue(
                            vo.getMallData(),
                            "discount",
                            new TypeReference<Map<String, List<OrderCreateDataVo.Discount>>>() {
                            },
                            new HashMap<>()
                    );
                    map.computeIfAbsent(CommonActiveEnum.FULL_GIFT.getType(), k -> new ArrayList<>()).add(discount);
                }

                // 更新商品数据中的 full_gift 字段（标记该商品参与了哪个满赠活动）
                for (FullGiftCalculateVo.MatchedGoods matched : active.getMatchedGoods()) {
                    Integer skuId = matched.getSkuId();
                    if (vo.getGoodsData().containsKey(skuId)) {
                        vo.getGoodsData().get(skuId).setFullGift(active);
                    }
                }

                // 移除活动中的 gift_list 和 matched_goods（仅保留活动基础信息）
                FullGiftCalculateVo cloned = BeanUtil.copyProperties(active, FullGiftCalculateVo.class);
                cloned.setGiftList(null);
                cloned.setMatchedGoods(null);
                processedActiveList.add(cloned);
            }
        }

        // 4. 与 CoreOrderCreateTrait::calculateFullGift 末尾一致：始终写入 full_gift_sku_list / full_gift_list（活动为空时为 []）
        vo.getBasic().setFullGiftSkuList(giftSkuList);
        vo.getBasic().setFullGiftList(processedActiveList);
    }

    /**
     * 提交订单前按当前时间重算满减/满赠，清除缓存中已过期的优惠与赠品。
     */
    public void recheckFullActiveBeforeCreate(OrderCreateDataVo vo) {
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
        if (vo.getGoodsData() != null) {
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
     * 重算子单应付金额（与 calculate 末尾一致）。
     */
    protected void refreshBasicOrderMoney(OrderCreateDataVo vo) {
        refreshBasicOrderMoneyStatic(vo);
    }

    /**
     * 统一公式：order_money = goods + delivery - discount - mall_discount
     */
    public static void refreshBasicOrderMoneyStatic(OrderCreateDataVo vo) {
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
        OrderCreateDataVo.Discount discount = toDiscount(value);
        return discount != null && CommonActiveEnum.FULL_REDUCTION.getType().equals(discount.getDiscountType());
    }

    private boolean isFullGiftDiscountValue(Object value) {
        OrderCreateDataVo.Discount discount = toDiscount(value);
        return discount != null && CommonActiveEnum.FULL_GIFT.getType().equals(discount.getDiscountType());
    }

    private OrderCreateDataVo.Discount toDiscount(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof OrderCreateDataVo.Discount d) {
            return d;
        }
        return JSONUtil.toBean(JSONUtil.parseObj(raw), OrderCreateDataVo.Discount.class);
    }

    private static BigDecimal formatOrderMoney(BigDecimal money) {
        if (money == null) {
            return BigDecimal.ZERO;
        }
        return money.multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.FLOOR)
                .divide(new BigDecimal("100"), 2, RoundingMode.UNNECESSARY);
    }

    private static BigDecimal calculateOrderMoney(BigDecimal... amounts) {
        BigDecimal totalInCents = BigDecimal.ZERO;
        for (BigDecimal amount : amounts) {
            BigDecimal amountInCents = (amount != null ? amount : BigDecimal.ZERO).multiply(new BigDecimal("100"));
            totalInCents = totalInCents.add(amountInCents);
        }
        return totalInCents.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public static <T> T getTypedValue(Map<String, Object> mallData, String key, TypeReference<T> typeRef, T defaultObject) {
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
        Map<String, Object> giftInfo = new HashMap<>();
        // 查询 SKU 信息（可批量优化，此处示例）
        MallGoodsSku sku = mallGoodsSkuMapper.selectById(gift.getSkuId());
        if (sku == null) {
            giftInfo.put("sku_id", gift.getSkuId());
            giftInfo.put("num", gift.getNum());
            return giftInfo;
        }
        // 查询商品信息
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
        Integer orderId = vo.getOrderId();
        Integer siteId = vo.getSiteId();
        Integer memberId = vo.getMemberId();

        if (ObjectUtil.isNotEmpty(orderFormData)) {
            orderFormData.put("member_id", memberId);
            orderFormData.put("relate_id", orderId);

            // 检查表单数据是否已存在，避免重复创建
            DiyFormRecordsParam checkParam = new DiyFormRecordsParam();
            checkParam.setMemberId(memberId);
            checkParam.setRelateId(orderId);
            checkParam.setFormId(orderFormData.getInt("form_id"));
            //表单填写信息校验
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
                    mallOrderMapper.updateById(mallOrder);
                }
            }
        }

        if (ObjectUtil.isNotEmpty(goodsFormData)) {
            List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper.selectList(
                    new QueryWrapper<MallOrderGoods>()
                            .eq("order_id", orderId)
                            .select("order_goods_id", "sku_id", "supply_id")
            );

            for (String key : goodsFormData.keySet()) {
                JSONObject goodsFormItem = goodsFormData.getJSONObject(key);
                if (ObjectUtil.isNotEmpty(goodsFormItem)) {
                    for (MallOrderGoods orderGoods : orderGoodsList) {
                        if (key.equals(orderGoods.getSkuId().toString())) {
                            goodsFormItem.put("member_id", memberId);
                            goodsFormItem.put("relate_id", orderGoods.getOrderGoodsId());

                            // 检查表单数据是否已存在，避免重复创建
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
                                    // 更新订单商品表的form_record_id字段
                                    orderGoods.setFormRecordId(formRecordId);
                                    mallOrderGoodsMapper.updateById(orderGoods);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 使用优惠
     *
     * @param vo
     */
    protected void useDiscount(OrderCreateDataVo vo) {
        if (ObjectUtil.isNotEmpty(vo.getDiscount())) {
            List<MallOrderDiscount> discountList = new LinkedList<>();
            for (OrderCreateDataVo.Discount item : vo.getDiscount().values()) {
                ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEvent event = new ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEvent();
                event.setSiteId(vo.getSiteId());
                event.addAppSign("mall");
                event.setName("ShopOrderDiscountCreateEvent");
                event.setDiscount(item);
                event.setOrderCreateData(vo);
                List<ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEventResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);

                if (eventData.isEmpty()) {
                    switch (item.getDiscountType()) {
                        case "coupon":
                            coreCouponMemberService.use(item.getDiscountTypeId(), vo.getOrderId());
                            break;
                        case "full_reduction":
                        case "point_deduct":
                            if (item.getMoney() != null && item.getMoney().compareTo(BigDecimal.ZERO) > 0) {
                                discountList.add(buildOrderDiscount(vo, item));
                            }
                            break;
                        case "full_gift":
                            discountList.add(buildOrderDiscount(vo, item));
                            break;
                    }
                }
            }
            coreOrderDiscountService.inserAll(discountList);
        }

        //扩展增加类似满减

        if (ObjectUtil.isNotEmpty(vo.getDiscountList())) {
            List<MallOrderDiscount> discountListAll = new LinkedList<>();
            for (Map.Entry<String, Map<Integer, OrderCreateDataVo.Discount>> entry : vo.getDiscountList().entrySet()) {

                for (OrderCreateDataVo.Discount item : entry.getValue().values()) {

                    MallOrderDiscount orderDiscount = new MallOrderDiscount();
                    BeanUtil.copyProperties(item, orderDiscount);
                    orderDiscount.setOrderId(vo.getOrderId());
                    orderDiscount.setCreateTime(System.currentTimeMillis() / 1000);
                    orderDiscount.setSiteId(vo.getSiteId());
                    discountListAll.add(orderDiscount);
                }

                coreOrderDiscountService.inserAll(discountListAll);
            }
        }
    }

    private MallOrderDiscount buildOrderDiscount(OrderCreateDataVo vo, OrderCreateDataVo.Discount item) {
        MallOrderDiscount orderDiscount = new MallOrderDiscount();
        BeanUtil.copyProperties(item, orderDiscount);
        orderDiscount.setOrderId(vo.getOrderId());
        orderDiscount.setCreateTime(System.currentTimeMillis() / 1000);
        orderDiscount.setSiteId(vo.getSiteId());
        return orderDiscount;
    }

    /**
     * 使用订单项优惠
     * 对应PHP的useGoodsDiscount方法
     *
     * @param vo
     * @param orderGoodsList 订单商品列表（包含orderGoodsId）
     */
    protected void useGoodsDiscount(OrderCreateDataVo vo, List<MallOrderGoods> orderGoodsList) {
        if (ObjectUtil.isEmpty(vo.getGoodsDiscount())) {
            return;
        }
        for (var item : vo.getGoodsDiscount().entrySet()) {
            var key = item.getKey();
            var value = item.getValue();
            for (MallOrderGoods mallOrderGoods : orderGoodsList) {

                if (Arrays.stream(value.getMatchGoodsIds()).anyMatch(num -> num == mallOrderGoods.getSkuId())) {
                    MallOrderDiscountGoods discountGoods = buildOrderGoodsDiscount(vo, value, mallOrderGoods.getOrderGoodsId());
                    mallOrderDiscountGoodsMapper.insert(discountGoods);
                }


            }

        }
    }

    private MallOrderDiscountGoods buildOrderGoodsDiscount(OrderCreateDataVo vo, OrderCreateDataVo.Discount item, Integer orderGoodsId) {
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
        // 添加订单表
        order.setCreateTime(System.currentTimeMillis() / 1000);
        mallOrderMapper.insert(order);
        vo.setOrderId(order.getOrderId());

        // 添加订单项表（仅主商品；赠品写入订单赠品表，与 gift_goods_data 分表一致）
        for (MallOrderGoods item : orderGoods) {
            item.setOrderId(order.getOrderId());
            //计算订单商品金额
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

        // 使用优惠
        this.useDiscount(vo);

        // 使用订单项优惠
        this.useGoodsDiscount(vo, orderGoods);

        JSONObject orderFormData = null;
        JSONObject goodsFormData = null;

        if (ObjectUtil.isNotEmpty(vo.getFormData())) {
            orderFormData = vo.getFormData().getJSONObject("order");

            goodsFormData = vo.getFormData().getJSONObject("goods");
        }

        // 添加万能表单数据（与 CoreOrderCreateTrait::createOrder 中 addFormData 调用顺序一致）
        this.addFormData(vo, orderFormData, goodsFormData);
        coreOrderEventService.orderCreateAfter(vo);
        // 订单创建之后事件
        OrderCreateAfterEvent event = new OrderCreateAfterEvent();
        event.setSiteId(vo.getSiteId());
        event.setOrderData(vo);
        event.setOrder(order);
        applicationEventPublisher.publishEvent(event);
//        EventAndSubscribeOfPublisher.publishAll(event);

        // 清除订单缓存
        removeOrderVo(vo.getOrderKey());

        OrderCreateResultVo result = new OrderCreateResultVo();
        result.setTradeType(vo.getOrderType());
        result.setOrderId(order.getOrderId());
        return result;
    }

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
}
