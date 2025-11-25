package com.niu.mall.service.core.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.mapper.member.MemberAddressMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.order.MallOrderGroup;
import com.niu.mall.entity.order.MallOrderGroupDiscount;
import com.niu.mall.enums.coupon.CouponIsAllSiteEnum;
import com.niu.mall.enums.coupon.CouponTypeEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderGroupEnum;
import com.niu.mall.mapper.coupon.MallCouponGoodsMapper;
import com.niu.mall.mapper.order.MallOrderGroupDiscountMapper;
import com.niu.mall.mapper.order.MallOrderGroupMapper;
import com.niu.mall.service.admin.goods.IMallGoodsService;
import com.niu.mall.service.core.marketing.ICoreCouponMemberService;
import com.niu.mall.service.core.marketing.vo.MallMemberCouponInfoVo;
import com.niu.mall.service.core.marketing.vo.MallMemberCouponListVo;
import com.niu.mall.service.core.order.ICoreMallOrderCreateService;
import com.niu.mall.service.core.order.ICoreOrderCreateService;
import com.niu.mall.service.core.order.ICoreOrderPayService;
import com.niu.mall.service.core.order.param.*;
import com.niu.mall.service.core.order.vo.MallOrderCreateDataVo;
import com.niu.mall.service.core.order.vo.MallOrderCreateResultVo;
import com.niu.mall.service.core.order.vo.OrderCouponListVo;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoreMallOrderCreateServiceImpl implements ICoreMallOrderCreateService {

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private MallOrderGroupMapper mallOrderGroupMapper;

    @Resource
    private MallOrderGroupDiscountMapper mallOrderGroupDiscountMapper;

    @Resource
    private IMemberLevelService memberLevelService;


    @Resource
    private ICoreOrderCreateService coreOrderCreateService;

    @Resource
    private ICoreAddonService coreAddonService;

    @Resource
    private ICoreOrderPayService coreOrderPayService;

    @Resource
    private ICoreCouponMemberService coreCouponMemberService;

    @Resource
    private MemberAddressMapper memberAddressMapper;

    @Resource
    private IMallGoodsService mallGoodsService;

    private MallOrderCreateDataVo mallOrderCreateDataVo = new MallOrderCreateDataVo();
    private MallOrderCalculateParam param;
    @Resource
    MallCouponGoodsMapper mallCouponGoodsMapper;

    @Override
    public MallOrderCreateDataVo calculate(MallOrderCalculateParam param) {
        this.param = param;
        mallOrderCreateDataVo.setBuyerId(param.getMemberId());

        // 查询会员信息
        MallOrderCreateDataVo.Buyer buyer = new MallOrderCreateDataVo.Buyer();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                .select("nickname, point")
                .eq("member_id", param.getMemberId()));
        if (member == null) throw new CommonException("找不到买家");

        BeanUtil.copyProperties(member, buyer);
        buyer.setMemberLevel(memberLevelService.findMemberLevel(param.getMemberId()));
        mallOrderCreateDataVo.setBuyer(buyer);

        selectTakeAddress(param);

        mallOrderCreateDataVo.setOrderHandleList(new HashMap<>());
        for (Map.Entry<Integer, MallOrderCalculateParam.Body> entry : param.getBody().entrySet()) {
            OrderCalculateParam orderCalculateParam = new OrderCalculateParam();
            orderCalculateParam.setSiteId(entry.getKey());
            orderCalculateParam.setOrderKey(entry.getValue().getOrderKey());
            orderCalculateParam.setCartIds(entry.getValue().getCartIds());
            orderCalculateParam.setSkuData(entry.getValue().getSkuData());
            orderCalculateParam.setDiscount(entry.getValue().getDiscount());
            orderCalculateParam.setDelivery(entry.getValue().getDelivery());
            orderCalculateParam.setMemberId(param.getMemberId());
            orderCalculateParam.setOrderFrom(param.getOrderFrom());

            mallOrderCreateDataVo.getOrderHandleList().put(
                    entry.getKey().toString(),
                    coreOrderCreateService.calculate(orderCalculateParam)
            );
        }
        mallOrderCreateDataVo.setOrderList(new HashMap<>());
        Set<String> hasGoodsTypes = new HashSet<>();
        Integer tempVirtualVerifyMobile = 0;
        for(Map.Entry<String, OrderCreateDataVo> entryMap : mallOrderCreateDataVo.getOrderHandleList().entrySet())
        {
            mallOrderCreateDataVo.getOrderList().put(entryMap.getValue().getSiteId(), entryMap.getValue());
            hasGoodsTypes.addAll(entryMap.getValue().getBasic().getHasGoodsTypes());
            if(ObjectUtil.isNotEmpty(entryMap.getValue().getBasic().getVirtualVerifyMobile()) && entryMap.getValue().getBasic().getVirtualVerifyMobile() > 0){
                tempVirtualVerifyMobile = entryMap.getValue().getBasic().getVirtualVerifyMobile();
            }
        }
        mallOrderCreateDataVo.getBasic().setHasGoodsTypes(hasGoodsTypes);
        mallOrderCreateDataVo.getBasic().setVirtualVerifyMobile(tempVirtualVerifyMobile);
        checkHasAddress();

        calculateOfMall();
        mallOrderCreateDataVo.setOrderHandleList(new HashMap<>());
        cacheOrderVo();
        this.param = null;
        return mallOrderCreateDataVo;
    }

    @Override
    @Transactional
    public MallOrderCreateResultVo create(MallOrderCreateParam param) {
        mallOrderCreateDataVo = loadOrderVo(param.getOrderKey(), MallOrderCreateDataVo.class);
        // 判断有没有错误信息
        if (mallOrderCreateDataVo.getError().size() > 0) throw new CommonException(mallOrderCreateDataVo.getError().get(0));

        Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                .select("nickname, point")
                .eq("member_id", param.getMemberId()));
        if (member == null) throw new CommonException("找不到买家");
        //检测供应商插件
        Addon supplyAddon = coreAddonService.getInfoByKey("supply");
        mallOrderCreateDataVo.setIsSupply(ObjectUtil.isNotEmpty(supplyAddon) ? true : false);
        if(!param.getDelivery().getTakerMobile().equals(""))
        {
            mallOrderCreateDataVo.getDelivery().setTakerMobile(param.getDelivery().getTakerMobile());
        }

        //合并订单
        splitOrder();
        createOrderGroup();
        JSONObject formData = param.getFormData();


        for (Map.Entry<String, OrderCreateDataVo> entry : mallOrderCreateDataVo.getOrderHandleList().entrySet()) {
            entry.getValue().setGroupId(mallOrderCreateDataVo.getGroupId());
            if(ObjectUtil.isNotEmpty(formData))
            {
                JSONObject siteFormData = formData.getJSONObject(entry.getValue().getSiteId().toString());
                if(ObjectUtil.isNotEmpty(siteFormData))
                {

                    entry.getValue().setFormData(siteFormData);
                }
            }
            //赋值发票信息
            OrderHelperParam.InvoiceCreateParam invoice =  param.getBody().get(entry.getValue().getSiteId()).getInvoice();
            InvoiceCreateParam invoiceCreateParam = new InvoiceCreateParam();
            if(ObjectUtil.isNotEmpty(invoice)){
                BeanUtils.copyProperties(invoice, invoiceCreateParam);
            }
            entry.getValue().setInvoice(invoiceCreateParam);
            entry.getValue().setOrderFrom(param.getOrderFrom());
            coreOrderCreateService.create(entry.getValue());
        }
        //平台优惠
        useMallDiscount();

        if(mallOrderCreateDataVo.getBasic().getOrderMoney().compareTo(BigDecimal.ZERO)  == 0){
            OrderPayParam orderPayParam = new OrderPayParam();
            orderPayParam.setTradeId(mallOrderCreateDataVo.getGroupId());
            orderPayParam.setMainType(mallOrderCreateDataVo.getMainType());
            orderPayParam.setMainId(mallOrderCreateDataVo.getMainId());
            coreOrderPayService.payOfGroup(orderPayParam);
        }

        //返回数据
        MallOrderCreateResultVo mallOrderCreateResultVo = new MallOrderCreateResultVo();
        mallOrderCreateResultVo.setTradeType("mall");
        mallOrderCreateResultVo.setTradeId(mallOrderCreateDataVo.getGroupId());
        return mallOrderCreateResultVo;
    }

    /**
     * 查询店铺优惠券
     * @return
     */
    @Override
    public Map<Integer, List<OrderCouponListVo>> getCoupon(Map<Integer,Map<String,Object>> body) {

        Map<Integer, List<OrderCouponListVo>> listMap = new HashMap<>();
        if(!body.isEmpty())
        {

            for(Map.Entry<Integer,Map<String,Object>> entry : body.entrySet()){
                Map<String,Object> map = entry.getValue();
                String orderKey = map.containsKey("order_key") ? map.get("order_key").toString() : "";
                if(!orderKey.equals(""))
                {
                    listMap.put(entry.getKey(), coreOrderCreateService.getCoupon(orderKey));
                }
            }
        }
        return listMap;

    }

    /**
     * 查询平台优惠券
     * @param orderKeys
     * @return
     */
    @Override
    public List<MallMemberCouponListVo> getPlatformCoupon(List<Object> orderKeys) {
        List<MallMemberCouponListVo> list = new ArrayList<>();
        if(!orderKeys.isEmpty()){

            for(Object orderKey : orderKeys){
                // 获取订单数据的缓存
                JSONObject jsonObject = loadOrderData((String) orderKey);
                Object memberId =  jsonObject.get("memberId");
                JSONObject basic =  jsonObject.getJSONObject("basic");
                BigDecimal goodsMoney = StringUtils.isNotEmpty(basic.getStr("goodsMoney")) ? new BigDecimal(basic.getStr("goodsMoney"))  : BigDecimal.ZERO;
                BigDecimal couponMoney = StringUtils.isNotEmpty(basic.getStr("couponMoney")) ? new BigDecimal(basic.getStr("couponMoney"))  : BigDecimal.ZERO;
                BigDecimal money = goodsMoney.subtract(couponMoney);
                if(money.compareTo(BigDecimal.ZERO) == 0 ){
                    return list;
                }
                // 获取用户可用的优惠券列表
                list = coreCouponMemberService.getUsePlatformCouponListByMemberId((Integer) memberId);
                List<MallCouponGoods> goods = new ArrayList<>();
                BigDecimal matchGoodsMoney = new BigDecimal(0);
                // 处理优惠券列表
                if(!list.isEmpty()){
                    for(MallMemberCouponListVo vo : list){

                        if (vo.getExpireTime().compareTo(System.currentTimeMillis() / 1000) == -1){
                            vo.setIsNormal(false);
                            vo.setError("当前优惠券已过期");
                        }else {

                            BigDecimal minConditionMoney = vo.getMinConditionMoney();
                            Integer isAllSiteJoin = vo.getCoupon().getIsAllSiteJoin();
                            String joinSiteIds = vo.getCoupon().getJoinSiteIds();

                            int type = Integer.valueOf(vo.getType());
                            //通用券
                            if (type == CouponTypeEnum.ALL.getType()) {
                                vo.setIsNormal(true);
                                goods = vo.getGoods();
                                //查询商品价格
                                JSONObject goodsList =  jsonObject.getJSONObject("goodsData");
                                for(String goodsId : goodsList.keySet()){
                                    matchGoodsMoney = matchGoodsMoney.add(new BigDecimal(goodsList.getJSONObject(goodsId).getStr("salePrice")));
                                }
                            }
                            //品类券
                            if (type == CouponTypeEnum.CATEGORY.getType()) {
                                List<Integer> categoryIds = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                                        .eq("coupon_id", vo.getCouponId())
                                        .select("category_id")).stream().map(i -> i.getCategoryId()).collect(Collectors.toList());
                                goods = new ArrayList<>();
                                matchGoodsMoney = new BigDecimal(0);
                                for (MallCouponGoods couponGoods : vo.getGoods()) {
                                    List<Integer> goodsCategory = JSONUtil.parseArray("[" + String.valueOf(couponGoods.getCategoryId()) + "]").toList(Integer.class);
                                    boolean hasIntersection = goodsCategory.stream().anyMatch(categoryIds::contains);
                                    if (hasIntersection) {
                                        goods.add(couponGoods);
                                        matchGoodsMoney = matchGoodsMoney.add(vo.getBasic().getGoodsMoney());
                                    }
                                }
                            }
                            //商品券
                            if (type == CouponTypeEnum.GOODS.getType()) {
                                List<Integer> goodsIds = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                                        .eq("coupon_id", vo.getCouponId())
                                        .select("goods_id")).stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
                                goods = new ArrayList<>();
                                matchGoodsMoney = new BigDecimal(0);
                                for (MallCouponGoods couponGoods : vo.getGoods()) {
                                    if (goodsIds.contains(couponGoods.getGoodsId())) {
                                        goods.add(couponGoods);
                                        matchGoodsMoney = matchGoodsMoney.add(vo.getBasic().getGoodsMoney());
                                    }
                                }
                            }
                            vo.setIsNormal(true);
                            if (goods.isEmpty()) {
                                vo.setIsNormal(false);
                                vo.setError("当前优惠券在本单不可用");
                            } else {

                                if (matchGoodsMoney.compareTo(minConditionMoney) == -1) {
                                    vo.setIsNormal(false);
                                    vo.setError("未达到当前优惠券的最低使用条件");
                                }
                                // 当前优惠券设置指定店铺参与，且当前优惠券的适用店铺中不包含当前订单的店铺，不满足使用条件
                                if(isAllSiteJoin.equals(CouponIsAllSiteEnum.NOT_ALL_SITE_JOIN.getType()) && StringUtils.isNotEmpty(joinSiteIds) && !joinSiteIds.contains(jsonObject.get("siteId").toString())){
                                    vo.setError("适用店铺中不包含当前订单的店铺，不满足使用条件");
                                    vo.setIsNormal(false);
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 拆分订单
     */
    private void splitOrder() {
        //重构订单对象
        for(Map.Entry<Integer, OrderCreateDataVo> entry : mallOrderCreateDataVo.getOrderList().entrySet()){

            if(!mallOrderCreateDataVo.getIsSupply()){
                mallOrderCreateDataVo.getOrderHandleList().put("shop" + entry.getKey().toString(), entry.getValue());
                continue;
            }
            OrderCreateDataVo commonOrderCreateDataVo = new OrderCreateDataVo();
            commonOrderCreateDataVo.setMemberId(entry.getValue().getMemberId());
            commonOrderCreateDataVo.setSiteId(entry.getValue().getSiteId());
            commonOrderCreateDataVo.setSiteInfo(entry.getValue().getSiteInfo());
            commonOrderCreateDataVo.setBuyer(entry.getValue().getBuyer());
            commonOrderCreateDataVo.setOrderKey(entry.getValue().getOrderKey());
            commonOrderCreateDataVo.setInvoice(entry.getValue().getInvoice());
            commonOrderCreateDataVo.setError(entry.getValue().getError());
            commonOrderCreateDataVo.setOrderId(entry.getValue().getOrderId());
            commonOrderCreateDataVo.setBasic(new OrderCreateDataVo.Basic());
            commonOrderCreateDataVo.setDiscount(entry.getValue().getDiscount());
            commonOrderCreateDataVo.setDelivery(entry.getValue().getDelivery());
            //重新组装订单项
            Map<Integer, List<OrderCreateDataVo.Sku>> orderGoods = new HashMap<>();

            entry.getValue().getGoodsData().forEach((key, value) -> {

                if (orderGoods.containsKey(value.getSupplyId())){
                    orderGoods.get(value.getSupplyId()).add(value);
                }else{
                    List<OrderCreateDataVo.Sku> list = new ArrayList<>();
                    list.add(value);
                    orderGoods.put(value.getSupplyId(), list);
                }
            });

            boolean isFirst = true;
            for(Map.Entry<Integer,  List<OrderCreateDataVo.Sku>> supplyEntry : orderGoods.entrySet()){
                OrderCreateDataVo supplyOrderCreateDataVo = new OrderCreateDataVo();
                BeanUtil.copyProperties(commonOrderCreateDataVo, supplyOrderCreateDataVo);
                supplyOrderCreateDataVo.setSupplyId(supplyEntry.getKey());

                if(ObjectUtil.isNotEmpty(entry.getValue().getDiscount())){
                    if(ObjectUtil.isEmpty(supplyOrderCreateDataVo.getDiscount().get("coupon")))
                    {
                        supplyOrderCreateDataVo.getDiscount().put("coupon", new OrderCreateDataVo.Discount());
                    }
                    if(isFirst)
                    {
                        isFirst = false;
                        supplyOrderCreateDataVo.getDiscount().get("coupon").setIsCan(true);
                    }else{
                        supplyOrderCreateDataVo.getDiscount().get("coupon").setIsCan(false);
                    }
                }
                Map<Integer, OrderCreateDataVo.Sku> goodsData = new HashMap<>();
                supplyOrderCreateDataVo.setBasic(new OrderCreateDataVo.Basic());
                for (OrderCreateDataVo.Sku sku : supplyEntry.getValue()) {

                    supplyOrderCreateDataVo.getBasic().setGoodsMoney(supplyOrderCreateDataVo.getBasic().getGoodsMoney().add(sku.getGoodsMoney()));
                    supplyOrderCreateDataVo.getBasic().setCostGoodsMoney(supplyOrderCreateDataVo.getBasic().getCostGoodsMoney().add(sku.getCostGoodsMoney()));
                    supplyOrderCreateDataVo.getBasic().setDiscountMoney(supplyOrderCreateDataVo.getBasic().getDiscountMoney().add(sku.getDiscountMoney()));
                    supplyOrderCreateDataVo.getBasic().setMallDiscountMoney(supplyOrderCreateDataVo.getBasic().getMallDiscountMoney().add(sku.getMallDiscountMoney()));
                    supplyOrderCreateDataVo.getBasic().getHasGoodsTypes().add(sku.getGoods().getGoodsType());
                    supplyOrderCreateDataVo.getBasic().setVirtualVerifyMobile(sku.getGoods().getVirtualVerifyMobile());
                    supplyOrderCreateDataVo.getBasic().setMallCouponMoney(supplyOrderCreateDataVo.getBasic().getMallCouponMoney().add(sku.getMallCouponMoney()));

                    if (entry.getValue().getDelivery().getDeliveryType().equals(DeliveryTypeEnum.EXPRESS.getType())) {
                        supplyOrderCreateDataVo.getBasic().setDeliveryMoney(supplyOrderCreateDataVo.getBasic().getDeliveryMoney().add(sku.getDeliveryMoney()));

                    }
                    supplyOrderCreateDataVo.getBasic().setTotalNum(supplyOrderCreateDataVo.getBasic().getTotalNum() + sku.getNum());
                    supplyOrderCreateDataVo.getBasic().setBody(supplyOrderCreateDataVo.getBasic().getBody() + sku.getSkuName() + sku.getGoods().getGoodsName() + " ");
                    goodsData.put(sku.getSkuId(), sku);

                    supplyOrderCreateDataVo.getCartIds().add(sku.getCardId());

                }
                supplyOrderCreateDataVo.setGoodsData(goodsData);
                if (entry.getValue().getDelivery().getDeliveryType().equals(DeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                    supplyOrderCreateDataVo.getBasic().setDeliveryMoney(entry.getValue().getBasic().getDeliveryMoney().add(supplyOrderCreateDataVo.getBasic().getDeliveryMoney()));

                }
                supplyOrderCreateDataVo.getBasic().setOrderMoney(supplyOrderCreateDataVo.getBasic().getGoodsMoney().subtract(supplyOrderCreateDataVo.getBasic().getDiscountMoney()).subtract(supplyOrderCreateDataVo.getBasic().getMallDiscountMoney()).add(supplyOrderCreateDataVo.getBasic().getDeliveryMoney()));
                supplyOrderCreateDataVo.getBasic().setHasGoodsTypes(new HashSet<>(supplyOrderCreateDataVo.getBasic().getHasGoodsTypes()));
                if (supplyOrderCreateDataVo.getBasic().getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
                    supplyOrderCreateDataVo.getDelivery().setDeliveryType(entry.getValue().getDelivery().getDeliveryType());
                } else {
                    supplyOrderCreateDataVo.getDelivery().setDeliveryType(OrderDeliveryTypeEnum.VIRTUAL.getType());
                    supplyOrderCreateDataVo.getDelivery().setTakerMobile(supplyOrderCreateDataVo.getDelivery().getTakerMobile());
                }
                if(supplyOrderCreateDataVo.getSupplyId() != 0)
                {
                    mallOrderCreateDataVo.getOrderHandleList().put("supply_" + supplyOrderCreateDataVo.getSupplyId().toString(), supplyOrderCreateDataVo);
                }else{
                    mallOrderCreateDataVo.getOrderHandleList().put("shop_" + supplyOrderCreateDataVo.getSiteId().toString(), supplyOrderCreateDataVo);
                }
            }

        }

    }
    /**
     * 订单分组
     */
    private void createOrderGroup() {
        MallOrderGroup orderGroup = new MallOrderGroup();
        orderGroup.setMemberId(mallOrderCreateDataVo.getBuyerId());
        orderGroup.setBody(mallOrderCreateDataVo.getBasic().getBody());
        orderGroup.setGoodsMoney(mallOrderCreateDataVo.getBasic().getGoodsMoney());
        orderGroup.setShopDiscountMoney(mallOrderCreateDataVo.getBasic().getShopDiscountMoney());
        orderGroup.setMallDiscountMoney(mallOrderCreateDataVo.getBasic().getMallDiscountMoney());
        orderGroup.setOrderMoney(mallOrderCreateDataVo.getBasic().getOrderMoney());
        orderGroup.setStatus(OrderGroupEnum.WAIT_PAY.getStatus());
        JSONObject takeAddress = JSONUtil.parseObj(mallOrderCreateDataVo.getDelivery().getTakeAddress());
        orderGroup.setTakerFullAddress(takeAddress.containsKey("fullAddress") ? takeAddress.getStr("fullAddress") : "");
        orderGroup.setIsBundlePay(mallOrderCreateDataVo.getBasic().getMallDiscountMoney().compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
        mallOrderGroupMapper.insert(orderGroup);

        mallOrderCreateDataVo.setGroupId(orderGroup.getGroupId());

    }


    /**
     * 使用平台优惠
     */
    private void useMallDiscount() {

        if (!mallOrderCreateDataVo.getDiscount().isEmpty()) {
            for (Map.Entry<String, MallOrderCreateDataVo.MallDiscountVo> entry : mallOrderCreateDataVo.getDiscount().entrySet()) {
                String key = entry.getKey();
                MallOrderCreateDataVo.MallDiscountVo value = entry.getValue();

                switch (key) {
                    case "platform_coupon":
                        // 使用优惠券
                        coreCouponMemberService.use(value.getDiscountTypeId(), mallOrderCreateDataVo.getGroupId());
                        break;
                }
                MallOrderGroupDiscount model = new MallOrderGroupDiscount();
                model.setGroupId(mallOrderCreateDataVo.getGroupId());
                model.setType(value.getType());
                model.setMoney(value.getMoney());
                model.setNum(value.getNum());
                model.setDiscountType(value.getDiscountType());
                model.setDiscountTypeId(value.getDiscountTypeId());
                model.setContent(value.getContent());
                mallOrderGroupDiscountMapper.insert(model);

            }

            // TODO: 批量保存优惠数据
        }
    }

    /**
     * 选中收货地址
     */
    protected void selectTakeAddress(MallOrderCalculateParam param) {
        //获取存在的配送方式
        Set<String> hasSelectDeliveryTypes = new HashSet<>();
        for (Integer siteId : param.getBody().keySet()){
            String itemDeliveryType = StringUtils.isNotEmpty(param.getBody().get(siteId).getDelivery().getDeliveryType()) ? param.getBody().get(siteId).getDelivery().getDeliveryType() : DeliveryTypeEnum.EXPRESS.getType();
            hasSelectDeliveryTypes.add(itemDeliveryType);
        }
        mallOrderCreateDataVo.getBasic().setHasSelectDeliveryTypes(hasSelectDeliveryTypes);


        MemberAddress address = null;
        if (ObjectUtil.isEmpty(param.getDelivery()) && param.getDelivery().getTakeAddressId() > 0) {
            address = memberAddressMapper.selectOne(new QueryWrapper<MemberAddress>()
                    .eq("id", param.getDelivery().getTakeAddressId())
                    .eq("member_id", param.getMemberId()));
        } else {
            address = memberAddressMapper.selectOne(new QueryWrapper<MemberAddress>()
                    .eq("is_default", 1)
                    .eq("member_id", param.getMemberId()).last("LIMIT 1"));

            //如果没有默认地址，将第一个地址设置作为订单地址
            if (ObjectUtil.isEmpty(address)) {
                address = memberAddressMapper.selectOne(new QueryWrapper<MemberAddress>()
                        .eq("member_id", param.getMemberId())
                        .last("LIMIT 1"));
            }
        }

        if (address == null) {
            mallOrderCreateDataVo.getDelivery().setTakeAddress(new JSONObject());
        } else {
            mallOrderCreateDataVo.getDelivery().setTakeAddress(address);
        }
    }

    /**
     * 检测是否需要地址
     * @return
     */
    private Boolean checkHasAddress(){

        for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {
            if(order.getBasic().getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType()))
            {
                return true;
            }
        }
        mallOrderCreateDataVo.getDelivery().setTakeAddress(new JSONObject());
        return false;
    }

    /**
     * 统筹计算订单
     */
    private void calculateOfMall(){

        //初始化
        mallOrderCreateDataVo.getBasic().setShopDiscountMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setDeliveryMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setGoodsMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setOrderMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setBody("");

        for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {
            mallOrderCreateDataVo.getBasic().setShopDiscountMoney(mallOrderCreateDataVo.getBasic().getShopDiscountMoney().add(order.getBasic().getDiscountMoney()));
            mallOrderCreateDataVo.getBasic().setDeliveryMoney(mallOrderCreateDataVo.getBasic().getDeliveryMoney().add(order.getBasic().getDeliveryMoney()));
            mallOrderCreateDataVo.getBasic().setGoodsMoney(mallOrderCreateDataVo.getBasic().getGoodsMoney().add(order.getBasic().getGoodsMoney()));
            mallOrderCreateDataVo.getBasic().setOrderMoney(mallOrderCreateDataVo.getBasic().getOrderMoney().add(order.getBasic().getOrderMoney()));
            mallOrderCreateDataVo.getBasic().setBody(mallOrderCreateDataVo.getBasic().getBody() + order.getBasic().getBody());
        }
        calculateDiscount();
    }

    /**
     * 计算折扣
     */
    private void calculateDiscount(){
        calculateCoupon();
    }

    /**
     * 通过站点id数组查询订单列表
     * @param siteIds
     * @return
     */
    private Map<String, OrderCreateDataVo> getOrderListBySiteIds(JSONArray siteIds)
    {
        if(ObjectUtil.isEmpty(siteIds)) return mallOrderCreateDataVo.getOrderHandleList();

        Map<String, OrderCreateDataVo> list = new HashMap<>();
        for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {

            if(siteIds.contains(order.getSiteId())){
                list.put(order.getSiteId().toString(), order);
            }
        }
        return list;
    }

    /**
     * 计算平台优惠金额
     * @throws CommonException 如果优惠券不存在或不满足使用条件
     */
    private void calculateCoupon() throws CommonException {
        mallOrderCreateDataVo.getBasic().setMallDiscountMoney(BigDecimal.ZERO);
        MallOrderCalculateParam.MallDiscount mallDiscount = param.getMallDiscount();
        Integer platformCouponId = mallDiscount != null ? mallDiscount.getPlatformCouponId() : null;
        mallOrderCreateDataVo.setDiscount(new HashMap<>());
        if (platformCouponId != null && platformCouponId > 0) {
            // 查询优惠券信息
            MallMemberCouponInfoVo couponInfo = coreCouponMemberService.getUsePlatformCouponInfoById(platformCouponId);

            if (couponInfo != null) {
                String couponType = couponInfo.getType();
                List<MallCouponGoods> goodsData = couponInfo.getGoods();
                BigDecimal mallDiscountMoney = couponInfo.getCouponPrice();
                BigDecimal minConditionMoney = couponInfo.getMinConditionMoney();
                Integer isAllSiteJoin = couponInfo.getCoupon().getIsAllSiteJoin();
                JSONArray joinSiteIds = new JSONArray();
                if(ObjectUtil.isNotEmpty(couponInfo.getCoupon().getJoinSiteIds()))
                {
                    joinSiteIds = JSONUtil.parseArray(couponInfo.getCoupon().getJoinSiteIds());
                }


                Map<String, OrderCreateDataVo> orderList = getOrderListBySiteIds(
                        isAllSiteJoin == 1 ? null : joinSiteIds
                );

                BigDecimal matchGoodsMoney = BigDecimal.ZERO;
                List<Integer> discountSiteIds = new ArrayList<>();
                boolean usable = false;

                switch (couponType) {
                    case "1":
                        // 通用优惠券
                        usable = true;
                        for (OrderCreateDataVo order : orderList.values()) {
                            discountSiteIds.add(order.getSiteId());
                            matchGoodsMoney = matchGoodsMoney.add(
                                    order.getBasic().getGoodsMoney()
                                            .subtract(order.getBasic().getDiscountMoney())
                            );
                        }
                        break;
                    case "2":
                        // 品类优惠券
                        List<Integer> categoryIds = goodsData.stream()
                                .map(g -> (Integer) g.getMallCategoryId())
                                .collect(Collectors.toList());

                        for (OrderCreateDataVo order : orderList.values()) {
                            for (OrderCreateDataVo.Sku sku : order.getGoodsData().values()) {
                                Integer goodsMallCategory = sku.getGoods().getGoodsMallCategory();
                                if (categoryIds.contains(goodsMallCategory)) {
                                    usable = true;
                                    discountSiteIds.add(order.getSiteId());
                                    matchGoodsMoney = matchGoodsMoney.add(
                                            sku.getGoodsMoney()
                                                    .subtract(sku.getDiscountMoney())
                                    );
                                }
                            }
                        }
                        break;
                    case "3":
                        // 品牌优惠券
                        List<Integer> brandIds = goodsData.stream()
                                .map(g -> (Integer) g.getBrandId())
                                .collect(Collectors.toList());

                        for (OrderCreateDataVo order : orderList.values()) {
                            for (OrderCreateDataVo.Sku sku : order.getGoodsData().values()) {
                                Integer brandId = sku.getGoods().getBrandId();
                                if (brandIds.contains(brandId)) {
                                    usable = true;
                                    discountSiteIds.add(order.getSiteId());
                                    matchGoodsMoney = matchGoodsMoney.add(
                                            sku.getGoodsMoney()
                                                    .subtract(sku.getDiscountMoney())
                                    );
                                }
                            }
                        }
                        break;
                }

                if (matchGoodsMoney.compareTo(BigDecimal.ZERO) <= 0) {
                    param.setMallDiscount(null);
                    if (usable) {
                        throw new CommonException("商品金额已减免至0元，不能继续使用平台优惠券");
                    } else {
                        throw new CommonException("订单未满足当前优惠券的最低使用条件");
                    }
                }

                // 满足平台优惠券的使用门槛
                if (matchGoodsMoney.compareTo(minConditionMoney) >= 0 && usable) {
                    // 设置平台优惠券信息
                    MallOrderCreateDataVo.MallDiscountVo mallDiscountVo = new MallOrderCreateDataVo.MallDiscountVo();
                    mallDiscountVo.setMatchGoodsIds(new ArrayList<>());
                    mallDiscountVo.setDiscountType("platform_coupon");
                    mallDiscountVo.setNum(1);
                    mallDiscountVo.setMoney(mallDiscountMoney);
                    mallDiscountVo.setType("discount");
                    mallDiscountVo.setDiscountTypeId(platformCouponId);
                    mallDiscountVo.setTitle(couponInfo.getTitle());
                    mallOrderCreateDataVo.getDiscount().put("platform_coupon", mallDiscountVo);

                    // 计算实际优惠金额
                    BigDecimal realMallDiscountGoodsMoney = BigDecimal.ZERO;
                    BigDecimal tempMallDiscountMoney = mallDiscountMoney;

                    for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {
                        Integer siteId = order.getSiteId();
                        if (discountSiteIds.contains(siteId)) {
                            BigDecimal tempRealGoodsMoney = order.getBasic().getGoodsMoney()
                                    .subtract(order.getBasic().getDiscountMoney());
                            BigDecimal tempRate = tempRealGoodsMoney.divide(matchGoodsMoney, 4, RoundingMode.HALF_UP);
                            BigDecimal itemMallDiscountMoney = tempRate.multiply(mallDiscountMoney)
                                    .setScale(2, RoundingMode.HALF_UP);

                            if (itemMallDiscountMoney.compareTo(tempMallDiscountMoney) > 0) {
                                itemMallDiscountMoney = tempMallDiscountMoney;
                            }

                            if (itemMallDiscountMoney.compareTo(tempRealGoodsMoney) > 0) {
                                itemMallDiscountMoney = tempRealGoodsMoney;
                            }

                            realMallDiscountGoodsMoney = realMallDiscountGoodsMoney.add(itemMallDiscountMoney);
                            tempMallDiscountMoney = tempMallDiscountMoney.subtract(itemMallDiscountMoney);

                            // 累加订单的平台优惠
                            order.getBasic().setMallDiscountMoney(itemMallDiscountMoney);
                            order.getBasic().setMallCouponMoney(itemMallDiscountMoney);

                            if (order.getBasic().getMallDiscountMoney().compareTo(tempRealGoodsMoney) > 0) {
                                order.getBasic().setMallDiscountMoney(tempRealGoodsMoney);
                            }

                            order.getBasic().setOrderMoney(
                                    order.getBasic().getOrderMoney().subtract(order.getBasic().getMallDiscountMoney())
                            );

                            // 循环计算给订单项分配平台优惠
                            BigDecimal tempOrderGoodsMoney = order.getBasic().getGoodsMoney()
                                    .subtract(order.getBasic().getDiscountMoney());
                            BigDecimal tempSuprItemMallDiscountMoney = itemMallDiscountMoney;

                            for (OrderCreateDataVo.Sku sku : order.getGoodsData().values()) {
                                BigDecimal orderGoodsMoney = sku.getGoodsMoney()
                                        .subtract(sku.getDiscountMoney());
                                BigDecimal tempItemMallDiscountMoney = BigDecimal.ZERO;
                                if (orderGoodsMoney.compareTo(BigDecimal.ZERO) > 0) {
                                    BigDecimal tempOrderGoodsRate = orderGoodsMoney.divide(tempOrderGoodsMoney, 4, RoundingMode.HALF_UP);
                                    tempItemMallDiscountMoney = tempOrderGoodsRate.multiply(itemMallDiscountMoney)
                                            .setScale(2, RoundingMode.HALF_UP);

                                    if (tempItemMallDiscountMoney.compareTo(tempSuprItemMallDiscountMoney) > 0) {
                                        tempItemMallDiscountMoney = tempSuprItemMallDiscountMoney.setScale(2, RoundingMode.HALF_UP);
                                    }

                                    tempSuprItemMallDiscountMoney = tempSuprItemMallDiscountMoney.subtract(tempItemMallDiscountMoney);
                                }

                                sku.setMallDiscountMoney(tempItemMallDiscountMoney);
                                sku.setMallCouponMoney(tempItemMallDiscountMoney);
                            }

                            // 维护订单的平台优惠项
                            OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                            discount.setType("discount");
                            discount.setDiscountType("platform_coupon");
                            discount.setMoney(itemMallDiscountMoney);
                            discount.setDiscountTypeId(platformCouponId);
                            discount.setTitle(couponInfo.getTitle());
                            order.getDiscount().put("platform_coupon", discount);
                        }
                    }

                    mallOrderCreateDataVo.getBasic().setMallDiscountMoney(realMallDiscountGoodsMoney);

                    if (mallOrderCreateDataVo.getBasic().getMallDiscountMoney()
                            .compareTo(mallOrderCreateDataVo.getBasic().getOrderMoney()) > 0) {

                        mallOrderCreateDataVo.getBasic().setMallDiscountMoney(
                                mallOrderCreateDataVo.getBasic().getOrderMoney()
                        );
                    }

                    mallOrderCreateDataVo.getBasic().setOrderMoney(
                            mallOrderCreateDataVo.getBasic().getOrderMoney()
                                    .subtract(mallOrderCreateDataVo.getBasic().getMallDiscountMoney())
                    );
                }
            } else {
                throw new CommonException("MALL_COUPON_NOT_EXIST");
            }
        }
    }


    /**
     * @param orderKey
     * @return
     */
    protected <T extends MallOrderCreateDataVo> T loadOrderVo(String orderKey, Class<T> clazz) {
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
     * 移除订单数据缓存
     *
     * @param orderKey
     */
    protected void removeOrderVo(String orderKey) {
        Cached cache = CacheFactory.getCacheOperator();
        cache.remove(RequestUtils.memberId() + "_member_order_" + orderKey);
    }

    /**
     * 缓存订单数据
     *
     */
    protected void cacheOrderVo() {
        if (ObjectUtil.isEmpty(mallOrderCreateDataVo.getOrderKey())) mallOrderCreateDataVo.setOrderKey(createOrderNo());
        Cached cache = CacheFactory.getCacheOperator();
        cache.put(RequestUtils.memberId() + "_member_order_" + mallOrderCreateDataVo.getOrderKey(), JSONUtil.parseObj(mallOrderCreateDataVo).toString());
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


}
