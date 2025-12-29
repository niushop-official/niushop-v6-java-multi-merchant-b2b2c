package com.niu.mall.service.core.order.helper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.DistanceCalculateUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryLocalDelivery;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStoreTypeEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.mapper.member.MemberAddressMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.coupon.MallCouponMember;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDiscount;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.coupon.CouponMemberStatusEnum;
import com.niu.mall.enums.coupon.CouponTypeEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderLogMainTypeEnum;
import com.niu.mall.event.order.OrderCreateAfterEvent;
import com.niu.mall.event.order.ShopOrderDiscountCreateEventDefiner;
import com.niu.mall.mapper.coupon.MallCouponGoodsMapper;
import com.niu.mall.mapper.coupon.MallCouponMemberMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.mall.service.core.delivery.ICoreExpressService;
import com.niu.mall.service.core.delivery.ICoreLocalDeliveryService;
import com.niu.mall.service.core.delivery.ICoreStoreService;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.mall.service.core.marketing.ICoreCouponMemberService;
import com.niu.mall.service.core.order.*;
import com.niu.mall.service.core.order.param.OrderCalculateParam;
import com.niu.mall.service.core.order.param.OrderHelperParam;
import com.niu.mall.service.core.order.param.OrderPayParam;
import com.niu.mall.service.core.order.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
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
    ICoreOrderDiscountService coreOrderDiscountService;

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

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
    ICoreInvoiceService  coreInvoiceService;
    @Resource
    DeliveryStoreMapper  deliveryStoreMapper;
    @Resource
    DeliveryLocalDeliveryMapper deliveryLocalDeliveryMapper;
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
     * 移除订单数据缓存
     *
     * @param orderKey
     */
    protected void removeOrderVo(String orderKey) {
        Cached cache = CacheFactory.getCacheOperator();
        cache.remove(RequestUtils.memberId() + "_member_order_" + orderKey);
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
            Map<String, DeliveryConfigVo> map = new HashMap<>();
            if(ObjectUtil.isNotEmpty(basic.getHasDeliveryTypes())){
                for(DeliveryConfigVo  conf : deliveryList){
                    if(conf.getStatus() == 1 && basic.getHasDeliveryTypes().contains(conf.getKey())){
                        map.put(conf.getKey(), conf);
                    }
                }
            }
            vo.getDelivery().setDeliveryTypeList(map);
            if(map.containsKey(DeliveryTypeEnum.LOCAL_DELIVERY.getType())){
                DeliveryLocalDelivery deliveryLocalDelivery =  deliveryLocalDeliveryMapper.selectOne(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", param.getSiteId()).last("limit 1"));
                vo.getDelivery().setLocalDeliveryConfig(deliveryLocalDelivery);
            }
        }else{
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
        if(param.getOrderFrom().equals("pc")) deliveryType = OrderDeliveryTypeEnum.EXPRESS.getType();
        basic.setDeliveryMoney(new BigDecimal(0));
        if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
            if(ObjectUtil.isEmpty(vo.getDelivery().getDeliveryTypeList()))
            {
                vo.getError().add(vo.getSiteInfo().getSiteName() + "未设置配送方式");
                vo.getDelivery().setError("商家未设置配送方式");
            }
            if(!deliveryType.isEmpty())
            {
                //校验合法
                if(!vo.getOrderFrom().equals("pc"))
                {
                    if(!vo.getDelivery().getDeliveryTypeList().containsKey(deliveryType)) throw new CommonException("请选择正确的配送方式");
                }
            }else{
                if (!vo.getDelivery().getDeliveryTypeList().isEmpty()) {
                    deliveryType = vo.getDelivery().getDeliveryTypeList().entrySet().iterator().next().getKey();
                }
            }

        } else {
            if(basic.getHasGoodsTypes().contains(GoodsTypeEnum.VIRTUAL.getType())){
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
            selectTakeStore(deliveryType,vo, param);
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
    public void getConfig(OrderCreateDataVo vo, String key,Integer siteIdParam, OrderCalculateParam param) {
        //查询购物配置
        OrderCreateDataVo.ConfigInfo config = vo.getConfig();
        Integer siteId = ObjectUtil.isNotEmpty(siteIdParam) ? siteIdParam : param.getSiteId();
        Object obj = null;
        if(ObjectUtil.isNotEmpty(vo.getConfig())){
            if(key.equals("order")){
                obj = vo.getConfig().getCloseOrderInfo();
            }else if(key.equals("delivery_type")){
                obj = vo.getConfig().getDeliveryType();
            }else if(key.equals("invoice")){
                obj = vo.getConfig().getInvoice();
            }
        }
        if(ObjectUtil.isEmpty(obj)){
            if(ObjectUtil.isEmpty(vo.getConfig())){
                config = new OrderCreateDataVo.ConfigInfo();
            }
            switch ( key){
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
                    Map<String, Object> map = deliveryList.stream().collect(Collectors.toMap(DeliveryConfigVo::getKey, i -> i));
                    config.setDeliveryType(map);
                    break;
                case "invoice":
                    InvoiceInfoVo invoice = coreInvoiceService.invoice(siteId);
                    OrderCreateDataVo.ConfigInfo.InvoiceInfo invoiceInfo = new OrderCreateDataVo.ConfigInfo.InvoiceInfo();
                    invoiceInfo.setIsInvoice(invoice.getIsInvoice());
                    invoiceInfo.setInvoiceType(invoice.getInvoiceType());
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
        if (vo.getBasic().getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())){
            vo.getDelivery().setTakeAddress(mallOrderCreateDataVo.getDelivery().getTakeAddress());
            if(vo.getDelivery().getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType()) &&
                ObjectUtil.isNotEmpty(vo.getDelivery().getTakeAddress())){
                JSONObject takeAddress = JSONUtil.parseObj(vo.getDelivery().getTakeAddress());
                if( ObjectUtil.isEmpty(takeAddress.get("lng"))){
                    vo.getError().add("所选的地址没有设置地图定位");
                }
            }
        }else{
            vo.getDelivery().setTakeAddress(null);
        }
    }

    /**
     * 选中配送/自提门店
     *
     * @param deliveryType
     */
    protected void selectTakeStore(String deliveryType,OrderCreateDataVo vo, OrderCalculateParam param) {

        OrderHelperParam.Delivery delivery = param.getDelivery();
        OrderHelperParam.Position position = param.getPosition();
        vo.getDelivery().setTakeStore("[]");
        if(LocalDeliveryStoreTypeEnum.getMap().containsKey(deliveryType)){
            List<OrderCreateDataVo.TakeStore> list = CollectUtils.convert(deliveryStoreMapper.selectList(new QueryWrapper<DeliveryStore>().eq("site_id", vo.getSiteId()).eq("status", 1)
                    .eq("support_store", 1)), OrderCreateDataVo.TakeStore.class);
            if(list.isEmpty()){
                if(LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType().equals(deliveryType)){
                    vo.getError().add(vo.getSiteInfo().getSiteName() + "商家未设置配送门店");
                    vo.getDelivery().setError(vo.getSiteInfo().getSiteName() + "商家未设置配送门店");
                }else{
                    vo.getError().add(vo.getSiteInfo().getSiteName() + "商家未设置提货点");
                    vo.getDelivery().setError(vo.getSiteInfo().getSiteName() + "商家未设置提货点");
                }
            }
            if(!list.isEmpty() && ObjectUtil.isNotEmpty( position) && ObjectUtil.isNotEmpty( position.getLatitude()) && ObjectUtil.isNotEmpty( position.getLongitude())){
                for (OrderCreateDataVo.TakeStore item : list){
                    Double distance = DistanceCalculateUtils.calculateDistance(
                            Double.parseDouble(position.getLatitude()),
                            Double.parseDouble(position.getLongitude()),
                            Double.parseDouble(item.getLatitude()),
                            Double.parseDouble(item.getLongitude())
                    );
                    item.setDistance( new BigDecimal( distance).setScale(3, RoundingMode.HALF_UP));
                }
                list = list.stream().sorted(Comparator.comparing(OrderCreateDataVo.TakeStore::getDistance)).toList();
            }
            vo.getDelivery().setTakeStoreList(list);
            if(ObjectUtil.isEmpty(delivery.getTakeStoreId()) || (ObjectUtil.isNotEmpty(delivery.getTakeStoreId()) && delivery.getTakeStoreId() == 0)){
                if(!list.isEmpty()){
                    vo.getDelivery().setTakeStore(list.get(0));
                }
            }else{
                DeliveryStore deliveryStore = deliveryStoreMapper.selectById(delivery.getTakeStoreId());
                if(ObjectUtil.isEmpty(deliveryStore)){
                    vo.getError().add("提货点不存在");
                }else{
                    switch (deliveryType) {
                        case "local_delivery":
                            if(!(ObjectUtil.isNotEmpty(deliveryStore.getSupportLocalDelivery()) && deliveryStore.getSupportLocalDelivery() == 1)){
                                vo.getError().add("该门店不支持同城配送");
                            }
                            break;
                        case "store":
                            if(!(ObjectUtil.isNotEmpty(deliveryStore.getSupportStore()) && deliveryStore.getSupportStore() == 1)){
                                vo.getError().add("该门店不支持门店自提");
                            }
                            break;
                    }
                }
                OrderCreateDataVo.TakeStore deliveryStoreVo = new OrderCreateDataVo.TakeStore();
                if(ObjectUtil.isNotEmpty(deliveryStore)){
                    BeanUtils.copyProperties(deliveryStore, deliveryStoreVo);
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


    }


    /**
     * 计算优惠券优惠
     *
     * @param vo
     * @param param
     */
    protected void calculateCoupon(OrderCreateDataVo vo, OrderCalculateParam param) {
        vo.getBasic().setCouponMoney(BigDecimal.ZERO);
        if (ObjectUtil.isNotEmpty(param.getDiscount().getCouponId()) && param.getDiscount().getCouponId() > 0) {
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
                        if(i == matchGoodsList.size() - 1){
                            itemCouponMoney = surplusMoney;
                        }else{
                            itemCouponMoney = itemOrderGoodsMoney.divide(matchOrderGoodsMoney, 2, RoundingMode.HALF_UP);
                            itemCouponMoney = (itemCouponMoney.multiply(couponMoney)).setScale(2, RoundingMode.HALF_UP);
                        }
                        vo.getGoodsData().get(itemSkuId).setDiscountMoney(vo.getGoodsData().get(itemSkuId).getDeliveryMoney().add(itemCouponMoney));
                        surplusMoney = surplusMoney.subtract(itemCouponMoney);
                    }
                    //优惠累增
                    vo.getBasic().setDiscountMoney(vo.getBasic().getDiscountMoney().add(couponMoney));

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
     * @param goods 商品信息Map
     * @return 订单商品金额
     */
    public static BigDecimal calculateOrderGoodsMoney(OrderCreateDataVo.Sku goods) {
        // 获取商品金额
        BigDecimal goodsMoney = goods.getGoodsMoney();

        // 获取折扣金额（如果不存在则为0）
        BigDecimal discountMoney = ObjectUtil.isNotEmpty(goods.getDiscountMoney()) ? goods.getDiscountMoney() : BigDecimal.ZERO;

        // 获取商城折扣金额（如果不存在则为0）
        BigDecimal mallDiscountMoney = ObjectUtil.isNotEmpty(goods.getMallDiscountMoney()) ? goods.getMallDiscountMoney() : BigDecimal.ZERO;

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

    protected void addFormData(OrderCreateDataVo vo, JSONObject orderFormData, JSONObject goodsFormData)
    {
        Integer orderId = vo.getOrderId();
        Integer siteId = vo.getSiteId();
        Integer memberId = vo.getMemberId();

        if(ObjectUtil.isNotEmpty(orderFormData))
        {
            orderFormData.put("member_id", memberId);
            orderFormData.put("relate_id", orderId);
            
            // 检查表单数据是否已存在，避免重复创建
            DiyFormRecordsParam checkParam = new DiyFormRecordsParam();
            checkParam.setMemberId(memberId);
            checkParam.setRelateId(orderId);
            checkParam.setFormId(orderFormData.getInt("form_id"));
            //表单填写信息校验
            if(ObjectUtil.isEmpty(coreDiyFormRecordsService.check(checkParam)))
            {
                DiyFormRecordsParam orderParam = new DiyFormRecordsParam();
                orderParam.setMemberId(memberId);
                orderParam.setRelateId(orderId);
                orderParam.setFormId(orderFormData.getInt("form_id"));
                orderParam.setValue(orderFormData.getJSONArray("value"));
                
                Integer formRecordId = coreDiyFormRecordsService.add(orderParam);
                if(formRecordId != null)
                {
                    MallOrder mallOrder = mallOrderMapper.selectById(orderId);
                    mallOrder.setFormRecordId(formRecordId);
                    mallOrderMapper.updateById(mallOrder);
                }
            }
        }

        if(ObjectUtil.isNotEmpty(goodsFormData))
        {
            List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper.selectList(
                new QueryWrapper<MallOrderGoods>()
                    .eq("order_id", orderId)
                    .select("order_goods_id", "sku_id", "supply_id")
            );
            
            for (String key : goodsFormData.keySet()) {
                JSONObject goodsFormItem = goodsFormData.getJSONObject(key);
                if(ObjectUtil.isNotEmpty(goodsFormItem))
                {
                    for (MallOrderGoods orderGoods : orderGoodsList) {
                        if(key.equals(orderGoods.getSkuId().toString()))
                        {
                            goodsFormItem.put("member_id", memberId);
                            goodsFormItem.put("relate_id", orderGoods.getOrderGoodsId());
                            
                            // 检查表单数据是否已存在，避免重复创建
                            DiyFormRecordsParam checkParam = new DiyFormRecordsParam();
                            checkParam.setMemberId(memberId);
                            checkParam.setRelateId(orderGoods.getOrderGoodsId());
                            checkParam.setFormId(goodsFormItem.getInt("form_id"));
                            if(ObjectUtil.isEmpty(coreDiyFormRecordsService.check(checkParam)))
                            {
                                DiyFormRecordsParam goodsParam = new DiyFormRecordsParam();
                                goodsParam.setMemberId(memberId);
                                goodsParam.setRelateId(orderGoods.getOrderGoodsId());
                                goodsParam.setFormId(goodsFormItem.getInt("form_id"));
                                goodsParam.setValue(goodsFormItem.getJSONArray("value"));
                                
                                Integer formRecordId = coreDiyFormRecordsService.add(goodsParam);
                                if(formRecordId != null)
                                {
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

                if (eventData.size() == 0) {
                    switch (item.getDiscountType()) {
                        case "coupon":
                            coreCouponMemberService.use(item.getDiscountTypeId(), vo.getOrderId());
                            break;
                    }
                }

                MallOrderDiscount orderDiscount = new MallOrderDiscount();
                BeanUtil.copyProperties(item, orderDiscount);
                orderDiscount.setOrderId(vo.getOrderId());
                orderDiscount.setCreateTime(System.currentTimeMillis() / 1000);
                orderDiscount.setSiteId(vo.getSiteId());
                discountList.add(orderDiscount);
            }

            coreOrderDiscountService.inserAll(discountList);

        }

        //扩展增加类似满减

        if(ObjectUtil.isNotEmpty(vo.getDiscountList()))
        {
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

    /**
     * 创建订单
     *
     * @param vo
     * @return
     */
    protected OrderCreateResultVo createOrder(OrderCreateDataVo vo, MallOrder order, List<MallOrderGoods> orderGoods) {
        // 添加订单表
        order.setCreateTime(System.currentTimeMillis() / 1000);
        mallOrderMapper.insert(order);
        vo.setOrderId(order.getOrderId());

        // 添加订单项表
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

        // 使用优惠
        this.useDiscount(vo);
        
        JSONObject orderFormData = null;
        JSONObject goodsFormData = null;
        
        if(ObjectUtil.isNotEmpty(vo.getFormData()))
        {
            orderFormData = vo.getFormData().getJSONObject("order");
            
            goodsFormData = vo.getFormData().getJSONObject("goods");
        }
        
        // 添加万能表单数据 - 对应PHP第130行
        this.addFormData(vo, orderFormData, goodsFormData);
        coreOrderEventService.orderCreateAfter(vo);

        // 订单创建之后事件
        if(ObjectUtil.isNotEmpty(vo.getSupplyId()) && order.getSupplyId() > 0){
            OrderCreateAfterEvent event = new OrderCreateAfterEvent();
            event.setSiteId(vo.getSiteId());
            event.addAppSign("mall");
            event.setName("OrderCreateAfterEvent");
            event.setOrderData(vo);
            event.setOrder(order);
            EventAndSubscribeOfPublisher.publishAll(event);
        }


        // 清除订单缓存
        removeOrderVo(vo.getOrderKey());

        OrderCreateResultVo result = new OrderCreateResultVo();
        result.setTradeType(vo.getOrderType());
        result.setOrderId(order.getOrderId());
        return result;
    }
}
