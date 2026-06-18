package com.niu.mall.service.core.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
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
import com.niu.core.entity.point_deduct.PointDeduct;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.mapper.member.MemberAddressMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.point_deduct.ICorePointDeductService;
import com.niu.core.service.core.point_deduct.vo.DeductMoneyVo;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.order.MallOrderGroup;
import com.niu.mall.entity.order.MallOrderGroupDiscount;
import com.niu.mall.enums.common.CommonActiveEnum;
import com.niu.mall.enums.coupon.CouponIsAllSiteEnum;
import com.niu.mall.enums.coupon.CouponStatusEnum;
import com.niu.mall.enums.coupon.CouponTypeEnum;
import com.niu.mall.enums.goods.GoodsDictEnum;
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
import com.niu.mall.service.core.order.ICoreOrderConfigService;
import com.niu.mall.service.core.order.ICoreOrderCreateService;
import com.niu.mall.service.core.order.helper.OrderCreateHelper;
import com.niu.mall.service.core.order.ICoreOrderPayService;
import com.niu.mall.service.core.order.param.*;
import com.niu.mall.service.core.order.vo.*;
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
    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;
    @Resource
    ICoreOrderConfigService coreOrderConfigService;
    @Resource
    private ICorePointDeductService corePointDeductService;


    @Override
    public MallOrderCreateDataVo calculate(MallOrderCalculateParam param) {
        this.param = param;
        //清除错误提示
        mallOrderCreateDataVo.setError(new ArrayList<>());
        mallOrderCreateDataVo.setIsPlatCoupon(1);
        mallOrderCreateDataVo.setIsSupply(null);
        mallOrderCreateDataVo.setGroupId(0);
        mallOrderCreateDataVo.setBuyerId(param.getMemberId());
        OrderHelperParam.Position position = new OrderHelperParam.Position();
        if (ObjectUtil.isNotEmpty(param.getPosition()) && StringUtils.isNotEmpty(param.getPosition().toString())) {
            position = BeanUtil.toBean(param.getPosition(), OrderHelperParam.Position.class);
        }

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

        //设置配置
        getConfig("delivery");
        mallOrderCreateDataVo.setOrderHandleList(new HashMap<>());
        for (Map.Entry<Integer, MallOrderCalculateParam.Body> entry : param.getBody().entrySet()) {
            OrderCalculateParam orderCalculateParam = new OrderCalculateParam();
            orderCalculateParam.setSiteId(entry.getKey());
            orderCalculateParam.setOrderKey(entry.getValue().getOrderKey());
            orderCalculateParam.setCartIds(entry.getValue().getCartIds());
            orderCalculateParam.setSkuData(entry.getValue().getSkuData());
            orderCalculateParam.setDiscount(entry.getValue().getDiscount());
            orderCalculateParam.setDelivery(entry.getValue().getDelivery());
            orderCalculateParam.setPosition(position);
            orderCalculateParam.setMemberId(param.getMemberId());
            orderCalculateParam.setOrderFrom(param.getOrderFrom());
            orderCalculateParam.setOrderType(entry.getValue().getOrderType());
            orderCalculateParam.setExtendData(entry.getValue().getExtendData());
            orderCalculateParam.setMemberRemark(entry.getValue().getMemberRemark());
            mallOrderCreateDataVo.getOrderHandleList().put(
                    entry.getKey().toString(),
                    coreOrderCreateService.calculate(orderCalculateParam, mallOrderCreateDataVo)
            );
        }
        //核验是否需要地址
        checkHasAddress();
        //统筹多站点订单
        calculateOfMall();

        mallOrderCreateDataVo.setOrderList(new HashMap<>());
        Set<String> tempHasGoodsTypes = new HashSet<>();
        Integer tempVirtualVerifyMobile = 0;
        for (Map.Entry<String, OrderCreateDataVo> entryMap : mallOrderCreateDataVo.getOrderHandleList().entrySet()) {
            mallOrderCreateDataVo.getOrderList().put(entryMap.getValue().getSiteId(), entryMap.getValue());
            tempHasGoodsTypes.addAll(entryMap.getValue().getBasic().getHasGoodsTypes());
            if (ObjectUtil.isNotEmpty(entryMap.getValue().getBasic().getVirtualVerifyMobile()) && entryMap.getValue().getBasic().getVirtualVerifyMobile() > 0) {
                tempVirtualVerifyMobile = entryMap.getValue().getBasic().getVirtualVerifyMobile();
            }
        }
        mallOrderCreateDataVo.getBasic().setHasGoodsTypes(tempHasGoodsTypes);
        mallOrderCreateDataVo.getBasic().setVirtualVerifyMobile(tempVirtualVerifyMobile);

//        mallOrderCreateDataVo.setOrderHandleList(new HashMap<>());  //todo 是否需要
        cacheOrderVo();
        this.param = null;
        return mallOrderCreateDataVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallOrderCreateResultVo create(MallOrderCreateParam param) {
        mallOrderCreateDataVo = loadOrderVo(param.getOrderKey(), MallOrderCreateDataVo.class);
        coerceMallAggregateDiscountFromCache(mallOrderCreateDataVo.getDiscount());
        // 判断有没有错误信息
        if (mallOrderCreateDataVo.getError().size() > 0)
            throw new CommonException(mallOrderCreateDataVo.getError().get(0));

        Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                .select("nickname, point")
                .eq("member_id", param.getMemberId()));
        if (member == null) throw new CommonException("找不到买家");
        if (StatusEnum.OFF.getStatus().equals(member.getStatus())) throw new CommonException("账号被锁定");
        //检测供应商插件
        Addon supplyAddon = coreAddonService.getInfoByKey("supply");
        mallOrderCreateDataVo.setIsSupply(ObjectUtil.isNotEmpty(supplyAddon));
        if (!param.getDelivery().getTakerMobile().equals("")) {
            mallOrderCreateDataVo.getDelivery().setTakerMobile(param.getDelivery().getTakerMobile());
        }
        if (!param.getDelivery().getTakerName().equals("")) {
            mallOrderCreateDataVo.getDelivery().setTakerName(param.getDelivery().getTakerName());
        }

        for (OrderCreateDataVo v : mallOrderCreateDataVo.getOrderList().values()) {
            coreOrderCreateService.recheckFullActiveBeforeCreate(v);
        }
        refreshMallBasicFromOrderList();

        //平台计算
//            $this->calculateOfMall();
        // 与  CoreMallOrderCreateService::create 一致：非 supply 时先在各站点子单上 recalculate 再进入 split/落单
        if (!mallOrderCreateDataVo.getIsSupply()) {
            for (OrderCreateDataVo v : mallOrderCreateDataVo.getOrderList().values()) {
                v.getDiscount().put("point_deduct", recalculateOrderDiscount("point_deduct", new ArrayList<>(), true, v));
                v.getDiscount().put("full_reduction", recalculateOrderDiscount("full_reduction", new ArrayList<>(), true, v));
                // PHP CoreMallOrderCreateService::create 376-377：$v['discount']['point_deduct'] 被汇总结果整体替换，不保留与行项并存的重复键；Java 行项在 Map 的其它 key 上，合并后删除以免 useDiscount 重复落库
                removeMergedDiscountLineKeys(v, "point_deduct");
                removeMergedDiscountLineKeys(v, "full_reduction");
                syncPointDeductBasicFromDiscount(v);
            }
        }
        splitOrder();
        for (OrderCreateDataVo v : mallOrderCreateDataVo.getOrderHandleList().values()) {
            syncPointDeductBasicFromDiscount(v);
        }
        refreshMallAggregateFromOrderHandles();
        createOrderGroup();
        JSONObject formData = param.getFormData();


        for (Map.Entry<String, OrderCreateDataVo> entry : mallOrderCreateDataVo.getOrderHandleList().entrySet()) {
            entry.getValue().setGroupId(mallOrderCreateDataVo.getGroupId());
            if (ObjectUtil.isNotEmpty(formData)) {
                JSONObject siteFormData = formData.getJSONObject(entry.getValue().getSiteId().toString());
                if (ObjectUtil.isNotEmpty(siteFormData)) {

                    entry.getValue().setFormData(siteFormData);
                }
            }
            //赋值发票信息
            OrderHelperParam.InvoiceCreateParam invoice = param.getBody().get(entry.getValue().getSiteId()).getInvoice();
            InvoiceCreateParam invoiceCreateParam = new InvoiceCreateParam();
            if (ObjectUtil.isNotEmpty(invoice)) {
                BeanUtils.copyProperties(invoice, invoiceCreateParam);
            }
            entry.getValue().setInvoice(invoiceCreateParam);
            entry.getValue().setOrderFrom(param.getOrderFrom());
            if (ObjectUtil.isNotEmpty(param.getDelivery().getTakerName())) {
                entry.getValue().getDelivery().setTakerName(param.getDelivery().getTakerName());
            }
            if (ObjectUtil.isNotEmpty(param.getDelivery().getTakerMobile())) {
                entry.getValue().getDelivery().setTakerMobile(param.getDelivery().getTakerMobile());
            }
            entry.getValue().setMemberRemark(param.getBody().get(entry.getValue().getSiteId()).getMemberRemark());
            entry.getValue().setWeappVersion(param.getWeappVersion());
            OrderHelperParam.Delivery siteDelivery = param.getBody().get(entry.getValue().getSiteId()).getDelivery();
            entry.getValue().setBuyerAskDeliveryTime(ObjectUtil.isNotEmpty(siteDelivery) ? siteDelivery.getBuyerAskDeliveryTime() : "");
            entry.getValue().setLocalDeliveryType(ObjectUtil.isNotEmpty(siteDelivery) ? siteDelivery.getLocalDeliveryType() : "");
            entry.getValue().setOrderType(param.getBody().get(entry.getValue().getSiteId()).getOrderType());
            entry.getValue().setExtendData(param.getBody().get(entry.getValue().getSiteId()).getExtendData());
            coreOrderCreateService.create(entry.getValue());
        }

        //26-04-02 LQ添加 积分抵扣（与 PHP joinPointDeduct($buyer_id, $num, $group_id) 一致，不依赖 siteId）
        MallOrderCreateDataVo.MallDiscountVo pointDeductVo = mallOrderCreateDataVo.getDiscount().getOrDefault("point_deduct", new MallOrderCreateDataVo.MallDiscountVo());
        Integer pointDeductNum = pointDeductVo.getNum() != null ? pointDeductVo.getNum() : 0;
        corePointDeductService.joinPointDeduct(null,
                mallOrderCreateDataVo.getBuyerId(),
                pointDeductNum,
                String.valueOf(mallOrderCreateDataVo.getGroupId()));

        //平台优惠
        useMallDiscount();

        if (mallOrderCreateDataVo.getBasic().getOrderMoney().compareTo(BigDecimal.ZERO) == 0) {
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
        removeOrderVo(param.getOrderKey());
        return mallOrderCreateResultVo;
    }

    /**
     * 查询店铺优惠券
     *
     * @return
     */
    @Override
    public Map<Integer, List<OrderCouponListVo>> getCoupon(Map<Integer, Map<String, Object>> body) {

        Map<Integer, List<OrderCouponListVo>> listMap = new HashMap<>();
        if (!body.isEmpty()) {

            for (Map.Entry<Integer, Map<String, Object>> entry : body.entrySet()) {
                Map<String, Object> map = entry.getValue();
                String orderKey = map.containsKey("order_key") ? map.get("order_key").toString() : "";
                if (!orderKey.equals("")) {
                    listMap.put(entry.getKey(), coreOrderCreateService.getCoupon(orderKey));
                }
            }
        }
        return listMap;

    }

    /**
     * 查询平台优惠券（先 calculate 再按 ALL/MALL_CATEGORY/BRAND 过滤，仅返回可用券）
     */
    @Override
    public List<MallMemberCouponListVo> getPlatformCoupon(MallOrderCalculateParam param) {
        calculate(param);

        List<MallMemberCouponListVo> memberCouponList = coreCouponMemberService.getUsePlatformCouponListByMemberId(param.getMemberId());
        List<MallMemberCouponListVo> couponList = new ArrayList<>();
        Set<String> orderHandleKeys = mallOrderCreateDataVo.getOrderHandleList().keySet();

        for (MallMemberCouponListVo couponVo : memberCouponList) {
            if (couponVo.getCoupon() == null || !CouponStatusEnum.NORMAL.getStatus().equals(couponVo.getCoupon().getStatus())) {
                continue;
            }

            Integer isAllSiteJoin = couponVo.getCoupon().getIsAllSiteJoin();
            JSONArray joinSiteIds = parseJoinSiteIds(couponVo.getCoupon().getJoinSiteIds());
            Map<String, OrderCreateDataVo> orderList = getOrderListBySiteIds(
                    CouponIsAllSiteEnum.ALL_SITE_JOIN.getType().equals(isAllSiteJoin) ? null : joinSiteIds
            );

            BigDecimal matchGoodsMoney = computePlatformCouponMatchMoney(couponVo, orderList);
            if (matchGoodsMoney.compareTo(couponVo.getMinConditionMoney()) < 0) {
                continue;
            }
            if (matchGoodsMoney.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            if (CouponIsAllSiteEnum.NOT_ALL_SITE_JOIN.getType().equals(isAllSiteJoin)
                    && joinSiteIds != null && !joinSiteIds.isEmpty()
                    && !isJoinSiteIdsCoveredByOrder(joinSiteIds, orderHandleKeys)) {
                continue;
            }

            couponList.add(couponVo);
        }
        return couponList;
    }

    private JSONArray parseJoinSiteIds(Object joinSiteIds) {
        if (ObjectUtil.isEmpty(joinSiteIds)) {
            return new JSONArray();
        }
        if (joinSiteIds instanceof JSONArray arr) {
            return arr;
        }
        return JSONUtil.parseArray(String.valueOf(joinSiteIds));
    }

    private boolean isJoinSiteIdsCoveredByOrder(JSONArray joinSiteIds, Set<String> orderHandleKeys) {
        for (int i = 0; i < joinSiteIds.size(); i++) {
            Object siteId = joinSiteIds.get(i);
            if (siteId != null && !orderHandleKeys.contains(String.valueOf(siteId))) {
                return false;
            }
        }
        return true;
    }

    private BigDecimal computePlatformCouponMatchMoney(MallMemberCouponListVo couponVo, Map<String, OrderCreateDataVo> orderList) {
        CouponTypeEnum typeEnum = CouponTypeEnum.getEnumByType(Integer.valueOf(couponVo.getType()));
        if (typeEnum == null) {
            return BigDecimal.ZERO;
        }

        List<MallCouponGoods> goodsData = couponVo.getGoods() != null ? couponVo.getGoods() : Collections.emptyList();
        BigDecimal matchGoodsMoney = BigDecimal.ZERO;

        switch (typeEnum) {
            case ALL:
                for (OrderCreateDataVo order : orderList.values()) {
                    matchGoodsMoney = matchGoodsMoney.add(
                            order.getBasic().getGoodsMoney().subtract(order.getBasic().getDiscountMoney())
                    );
                }
                break;
            case MALL_CATEGORY:
                List<Integer> categoryIds = goodsData.stream()
                        .map(MallCouponGoods::getMallCategoryId)
                        .filter(Objects::nonNull)
                        .toList();
                for (OrderCreateDataVo order : orderList.values()) {
                    for (OrderCreateDataVo.Sku sku : order.getGoodsData().values()) {
                        Integer goodsMallCategory = sku.getGoods().getGoodsMallCategory();
                        if (goodsMallCategory != null && categoryIds.contains(goodsMallCategory)) {
                            matchGoodsMoney = matchGoodsMoney.add(
                                    sku.getGoodsMoney().subtract(sku.getDiscountMoney())
                            );
                        }
                    }
                }
                break;
            case BRAND:
                List<Integer> brandIds = goodsData.stream()
                        .map(MallCouponGoods::getBrandId)
                        .filter(Objects::nonNull)
                        .toList();
                for (OrderCreateDataVo order : orderList.values()) {
                    for (OrderCreateDataVo.Sku sku : order.getGoodsData().values()) {
                        Integer brandId = sku.getGoods().getBrandId();
                        if (brandId != null && brandIds.contains(brandId)) {
                            matchGoodsMoney = matchGoodsMoney.add(
                                    sku.getGoodsMoney().subtract(sku.getDiscountMoney())
                            );
                        }
                    }
                }
                break;
            default:
                return BigDecimal.ZERO;
        }
        return matchGoodsMoney;
    }

    /**
     * 拆分订单
     */
    private void splitOrder() {
        if (mallOrderCreateDataVo.getOrderHandleList() == null) {
            mallOrderCreateDataVo.setOrderHandleList(new HashMap<>());
        } else {
            mallOrderCreateDataVo.getOrderHandleList().clear();
        }
        for (Map.Entry<Integer, OrderCreateDataVo> entry : mallOrderCreateDataVo.getOrderList().entrySet()) {

            if (!mallOrderCreateDataVo.getIsSupply()) {
                OrderCreateDataVo orderVo = entry.getValue();
                syncPointDeductBasicFromDiscount(orderVo);
                mallOrderCreateDataVo.getOrderHandleList().put("shop" + entry.getKey().toString(), orderVo);
                continue;
            }
            OrderCreateDataVo siteOrder = entry.getValue();
            OrderCreateDataVo commonOrderCreateDataVo = new OrderCreateDataVo();
            commonOrderCreateDataVo.setMemberId(siteOrder.getMemberId());
            commonOrderCreateDataVo.setSiteId(siteOrder.getSiteId());
            commonOrderCreateDataVo.setSiteInfo(siteOrder.getSiteInfo());
            commonOrderCreateDataVo.setBuyer(siteOrder.getBuyer());
            commonOrderCreateDataVo.setOrderKey(siteOrder.getOrderKey());
            commonOrderCreateDataVo.setInvoice(siteOrder.getInvoice());
            commonOrderCreateDataVo.setError(siteOrder.getError());
            commonOrderCreateDataVo.setOrderId(siteOrder.getOrderId());
            commonOrderCreateDataVo.setBasic(new OrderCreateDataVo.Basic());
            commonOrderCreateDataVo.setDiscount(siteOrder.getDiscount());
            commonOrderCreateDataVo.setGoodsDiscount(siteOrder.getGoodsDiscount());
            commonOrderCreateDataVo.setDelivery(siteOrder.getDelivery());
            //重新组装订单项
            Map<Integer, List<OrderCreateDataVo.Sku>> orderGoods = new HashMap<>();

            siteOrder.getGoodsData().forEach((key, value) -> {

                if (orderGoods.containsKey(value.getSupplyId())) {
                    orderGoods.get(value.getSupplyId()).add(value);
                } else {
                    List<OrderCreateDataVo.Sku> list = new ArrayList<>();
                    list.add(value);
                    orderGoods.put(value.getSupplyId(), list);
                }
            });

            boolean isFirst = true;
            boolean noSupplySiteBatch = orderGoods.containsKey(0);
            boolean fullGiftCopiedToSupplyBatch = false;
            List<String> tmpDiscountParams = new ArrayList<>();
            for (Map.Entry<Integer, List<OrderCreateDataVo.Sku>> supplyEntry : orderGoods.entrySet()) {
                tmpDiscountParams.clear();
                OrderCreateDataVo supplyOrderCreateDataVo = new OrderCreateDataVo();
                BeanUtil.copyProperties(commonOrderCreateDataVo, supplyOrderCreateDataVo);
                supplyOrderCreateDataVo.setSupplyId(supplyEntry.getKey());
                supplyOrderCreateDataVo.setDiscount(copyDiscountMap(siteOrder.getDiscount()));

                if (ObjectUtil.isNotEmpty(siteOrder.getDiscount())) {
                    if (ObjectUtil.isEmpty(supplyOrderCreateDataVo.getDiscount().get("coupon"))) {
                        supplyOrderCreateDataVo.getDiscount().put("coupon", new OrderCreateDataVo.Discount());
                    }
                    if (isFirst) {
                        isFirst = false;
                        supplyOrderCreateDataVo.getDiscount().get("coupon").setIsCan(true);
                    } else {
                        supplyOrderCreateDataVo.getDiscount().get("coupon").setIsCan(false);
                    }
                }
                Map<Integer, OrderCreateDataVo.Sku> goodsData = new HashMap<>();
                supplyOrderCreateDataVo.setBasic(new OrderCreateDataVo.Basic());
                supplyOrderCreateDataVo.setGoodsDiscount(filterGoodsDiscountBySkus(siteOrder.getGoodsDiscount(), supplyEntry.getValue()));
                // 混合单：赠品挂 supply_id=0 子单；纯供货单：挂第一个供货子单
                if (!fullGiftCopiedToSupplyBatch) {
                    boolean attachGiftToThisBatch = noSupplySiteBatch
                            ? Integer.valueOf(0).equals(supplyEntry.getKey())
                            : true;
                    if (attachGiftToThisBatch) {
                        OrderCreateDataVo.Basic siteBasic = siteOrder.getBasic();
                        if (siteBasic != null) {
                            if (siteBasic.getFullGiftSkuList() != null) {
                                supplyOrderCreateDataVo.getBasic().setFullGiftSkuList(new ArrayList<>(siteBasic.getFullGiftSkuList()));
                            }
                            if (siteBasic.getFullGiftList() != null) {
                                supplyOrderCreateDataVo.getBasic().setFullGiftList(new ArrayList<>(siteBasic.getFullGiftList()));
                            }
                        }
                        fullGiftCopiedToSupplyBatch = true;
                    }
                }
                for (OrderCreateDataVo.Sku sku : supplyEntry.getValue()) {
                    String skuDiscountKey = sku.getSiteId() + "_" + sku.getGoodsId() + "_" + sku.getSkuId();
                    tmpDiscountParams.add(skuDiscountKey + ":point_deduct");
                    tmpDiscountParams.add(skuDiscountKey + ":full_reduction");

                    supplyOrderCreateDataVo.getBasic().setGoodsMoney(supplyOrderCreateDataVo.getBasic().getGoodsMoney().add(sku.getGoodsMoney()));
                    supplyOrderCreateDataVo.getBasic().setCostGoodsMoney(supplyOrderCreateDataVo.getBasic().getCostGoodsMoney().add(sku.getCostGoodsMoney()));
                    supplyOrderCreateDataVo.getBasic().setDiscountMoney(supplyOrderCreateDataVo.getBasic().getDiscountMoney().add(sku.getDiscountMoney()));
                    // CoreMallOrderCreateService::splitOrder 1111-1114：行项 mall_discount_money / use_point / point_deduct_money 累加到子单 basic；优先 SKU 字段，与 PHP 一致
                    BigDecimal lineMallDiscount = ObjectUtil.defaultIfNull(sku.getMallDiscountMoney(), BigDecimal.ZERO);
                    OrderCreateDataVo.Goods g = sku.getGoods();
                    if (g != null && ObjectUtil.isNotEmpty(g.getPointDeductMoney()) && g.getPointDeductMoney().compareTo(BigDecimal.ZERO) > 0
                            && lineMallDiscount.compareTo(g.getPointDeductMoney()) < 0) {
                        lineMallDiscount = lineMallDiscount.add(g.getPointDeductMoney());
                    }
                    supplyOrderCreateDataVo.getBasic().setMallDiscountMoney(supplyOrderCreateDataVo.getBasic().getMallDiscountMoney().add(lineMallDiscount));
                    int lineUsePoint = sku.getUsePoint() != null && sku.getUsePoint() > 0
                            ? sku.getUsePoint()
                            : (g != null && g.getUsePoint() != null ? g.getUsePoint() : 0);
                    BigDecimal linePointDeduct = sku.getPointDeductMoney() != null && sku.getPointDeductMoney().compareTo(BigDecimal.ZERO) > 0
                            ? sku.getPointDeductMoney()
                            : (g != null && g.getPointDeductMoney() != null ? g.getPointDeductMoney() : BigDecimal.ZERO);
                    if (lineUsePoint > 0) {
                        supplyOrderCreateDataVo.getBasic().setUsePoint(
                                (supplyOrderCreateDataVo.getBasic().getUsePoint() != null ? supplyOrderCreateDataVo.getBasic().getUsePoint() : 0) + lineUsePoint);
                    }
                    if (linePointDeduct.compareTo(BigDecimal.ZERO) > 0) {
                        supplyOrderCreateDataVo.getBasic().setPointDeductMoney(
                                supplyOrderCreateDataVo.getBasic().getPointDeductMoney().add(linePointDeduct));
                    }
                    supplyOrderCreateDataVo.getBasic().setFullReductionMoney(
                            supplyOrderCreateDataVo.getBasic().getFullReductionMoney().add(ObjectUtil.defaultIfNull(sku.getFullReductionMoney(), BigDecimal.ZERO)));

                    supplyOrderCreateDataVo.getBasic().getHasGoodsTypes().add(sku.getGoods().getGoodsType());
                    if (sku.getGoods().getVirtualVerifyMobile() != null && sku.getGoods().getVirtualVerifyMobile() > 0) {
                        supplyOrderCreateDataVo.getBasic().setVirtualVerifyMobile(sku.getGoods().getVirtualVerifyMobile());
                    }

                    supplyOrderCreateDataVo.getBasic().setMallCouponMoney(supplyOrderCreateDataVo.getBasic().getMallCouponMoney().add(sku.getMallCouponMoney()));

                    if (siteOrder.getDelivery().getDeliveryType().equals(DeliveryTypeEnum.EXPRESS.getType())) {
                        supplyOrderCreateDataVo.getBasic().setDeliveryMoney(supplyOrderCreateDataVo.getBasic().getDeliveryMoney().add(sku.getDeliveryMoney()));

                    }
                    supplyOrderCreateDataVo.getBasic().setTotalNum(supplyOrderCreateDataVo.getBasic().getTotalNum() + sku.getNum());
                    supplyOrderCreateDataVo.getBasic().setBody(supplyOrderCreateDataVo.getBasic().getBody() + sku.getSkuName() + sku.getGoods().getGoodsName() + " ");
                    goodsData.put(sku.getSkuId(), sku);

                    supplyOrderCreateDataVo.getCartIds().add(ObjectUtil.isNotEmpty(sku.getCardId()) ? sku.getCardId() : 0L);

                }
                supplyOrderCreateDataVo.setGoodsData(goodsData);
                if (supplyOrderCreateDataVo.getDiscount() == null) {
                    supplyOrderCreateDataVo.setDiscount(new HashMap<>());
                }
                supplyOrderCreateDataVo.getDiscount().put("point_deduct",
                        recalculateOrderDiscount("point_deduct", tmpDiscountParams, false, siteOrder));
                supplyOrderCreateDataVo.getDiscount().put("full_reduction",
                        recalculateOrderDiscount("full_reduction", tmpDiscountParams, false, siteOrder));
                removeMergedDiscountLineKeys(supplyOrderCreateDataVo, "point_deduct");
                removeMergedDiscountLineKeys(supplyOrderCreateDataVo, "full_reduction");
                syncPointDeductBasicFromDiscount(supplyOrderCreateDataVo);
                if (siteOrder.getDelivery().getDeliveryType().equals(DeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                    supplyOrderCreateDataVo.getBasic().setDeliveryMoney(siteOrder.getBasic().getDeliveryMoney().add(supplyOrderCreateDataVo.getBasic().getDeliveryMoney()));

                }
                supplyOrderCreateDataVo.getBasic().setOrderMoney(supplyOrderCreateDataVo.getBasic().getGoodsMoney().subtract(supplyOrderCreateDataVo.getBasic().getDiscountMoney()).subtract(supplyOrderCreateDataVo.getBasic().getMallDiscountMoney()).add(supplyOrderCreateDataVo.getBasic().getDeliveryMoney()));
                supplyOrderCreateDataVo.getBasic().setHasGoodsTypes(new HashSet<>(supplyOrderCreateDataVo.getBasic().getHasGoodsTypes()));
                if (StringUtils.isNotEmpty(mallOrderCreateDataVo.getDelivery().getTakerName())) {
                    entry.getValue().getDelivery().setTakerName(supplyOrderCreateDataVo.getDelivery().getTakerName());
                }
                if (StringUtils.isNotEmpty(mallOrderCreateDataVo.getDelivery().getTakerMobile())) {
                    entry.getValue().getDelivery().setTakerMobile(supplyOrderCreateDataVo.getDelivery().getTakerMobile());
                }
                if (supplyOrderCreateDataVo.getBasic().getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
                    supplyOrderCreateDataVo.setDelivery(siteOrder.getDelivery());
                } else {
                    supplyOrderCreateDataVo.getDelivery().setDeliveryType(OrderDeliveryTypeEnum.VIRTUAL.getType());
                    supplyOrderCreateDataVo.getDelivery().setTakerMobile(supplyOrderCreateDataVo.getDelivery().getTakerMobile());
                }
                if (supplyOrderCreateDataVo.getSupplyId() != 0) {
                    mallOrderCreateDataVo.getOrderHandleList().put("supply_" + supplyOrderCreateDataVo.getSupplyId().toString(), supplyOrderCreateDataVo);
                } else {
                    mallOrderCreateDataVo.getOrderHandleList().put("shop_" + supplyOrderCreateDataVo.getSiteId().toString(), supplyOrderCreateDataVo);
                }
            }

        }

    }

    /**
     * 聚合层 discount（积分抵扣等）在 JSON 缓存中可能非 {@link MallOrderCreateDataVo.MallDiscountVo}；PHP 侧为原生数组无此问题。
     */
    private static void coerceMallAggregateDiscountFromCache(Map<String, MallOrderCreateDataVo.MallDiscountVo> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        Map<String, MallOrderCreateDataVo.MallDiscountVo> out = new LinkedHashMap<>();
        for (String key : new ArrayList<>(map.keySet())) {
            Object raw = map.get(key);
            if (raw == null) {
                continue;
            }
            if (raw instanceof MallOrderCreateDataVo.MallDiscountVo m) {
                out.put(key, m);
            } else {
                out.put(key, JSONUtil.toBean(JSONUtil.parseObj(raw), MallOrderCreateDataVo.MallDiscountVo.class));
            }
        }
        map.clear();
        map.putAll(out);
    }

    /**
     * create 从缓存反序列化后，子单 discount Map 的 value 可能为 JSONObject；本方法仅在汇总路径使用，等价于 PHP 中已强类型的 discount 项。
     */
    private static OrderCreateDataVo.Discount discountFromCacheValue(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof OrderCreateDataVo.Discount d) {
            return d;
        }
        return JSONUtil.toBean(JSONUtil.parseObj(raw), OrderCreateDataVo.Discount.class);
    }

    /**
     * PHP 将 {@code $v['discount']['point_deduct']} 等槽位整体替换为汇总结果；Java 行项原在其它 Map key 上，汇总写入同名槽位后删除行项，避免 useDiscount 重复插入 mall_order_discount。
     */
    private void removeMergedDiscountLineKeys(OrderCreateDataVo v, String aggregateKey) {
        if (v == null || v.getDiscount() == null || !v.getDiscount().containsKey(aggregateKey)) {
            return;
        }
        Iterator<Map.Entry<String, OrderCreateDataVo.Discount>> it = v.getDiscount().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, OrderCreateDataVo.Discount> en = it.next();
            if (aggregateKey.equals(en.getKey())) {
                continue;
            }
            OrderCreateDataVo.Discount d = discountFromCacheValue(en.getValue());
            if (d != null && aggregateKey.equals(d.getDiscountType())) {
                it.remove();
            }
        }
    }

    /**
     * 重新计算订单优惠信息（积分抵扣/满减）
     *
     * @param discountType 优惠类型：point_deduct-积分抵扣, full_reduction-满减
     * @param params       订单项唯一键列表，格式：siteId_goodsId_skuId
     * @param isAll        是否汇总当前 scopeOrder 下全部匹配项
     * @param scopeOrder   仅汇总该子订单上的优惠
     * @return 重新计算后的优惠信息
     */
    private OrderCreateDataVo.Discount recalculateOrderDiscount(String discountType, List<String> params, boolean isAll, OrderCreateDataVo scopeOrder) {
        OrderCreateDataVo.Discount tmpDiscountInfo = new OrderCreateDataVo.Discount();
        tmpDiscountInfo.setType("discount");
        tmpDiscountInfo.setDiscountType(discountType);
        tmpDiscountInfo.setNum(0);
        tmpDiscountInfo.setMoney(BigDecimal.ZERO);

        if (discountType.equals("point_deduct")) {
            tmpDiscountInfo.setTitle("参与积分抵扣");
        } else if (discountType.equals("full_reduction")) {
            tmpDiscountInfo.setTitle("参与满减优惠活动");
        }

        Map<String, OrderCreateDataVo.Discount> discountData = new HashMap<>();
        if (scopeOrder != null && scopeOrder.getDiscount() != null) {
            for (String key : new ArrayList<>(scopeOrder.getDiscount().keySet())) {
                OrderCreateDataVo.Discount v = discountFromCacheValue(scopeOrder.getDiscount().get(key));
                if (v == null || v.getDiscountType() == null) {
                    continue;
                }
                if (discountType.equals("point_deduct") && v.getDiscountType().equals("point_deduct")) {
                    discountData.put(key, v);
                } else if (discountType.equals("full_reduction") && v.getDiscountType().equals("full_reduction")) {
                    discountData.put(key, v);
                }
            }
        }

        if (!isAll) {
            for (String uniqueKey : params) {
                if (discountData.containsKey(uniqueKey)) {
                    OrderCreateDataVo.Discount discountInfo = discountData.get(uniqueKey);
                    if (discountType.equals("point_deduct")) {
                        String title = tmpDiscountInfo.getTitle();
                        tmpDiscountInfo.setTitle(title + (ObjectUtil.isNotEmpty(discountInfo.getTitle()) ? discountInfo.getTitle() + " " : ""));
                    }
                    tmpDiscountInfo.setNum(tmpDiscountInfo.getNum() + (discountInfo.getNum() != null ? discountInfo.getNum() : 0));
                    tmpDiscountInfo.setMoney(tmpDiscountInfo.getMoney().add(discountInfo.getMoney() != null ? discountInfo.getMoney() : BigDecimal.ZERO));
                }
            }
        } else {
            if (scopeOrder == null || scopeOrder.getDiscount() == null) {
                return tmpDiscountInfo.getMoney().compareTo(BigDecimal.ZERO) > 0 ? tmpDiscountInfo : new OrderCreateDataVo.Discount();
            }
            if (discountType.equals("point_deduct")) {
                StringBuilder titleSb = new StringBuilder();
                for (String key : new ArrayList<>(scopeOrder.getDiscount().keySet())) {
                    OrderCreateDataVo.Discount discountInfo = discountFromCacheValue(scopeOrder.getDiscount().get(key));
                    if (discountInfo == null || discountInfo.getDiscountType() == null || !discountInfo.getDiscountType().equals("point_deduct")) {
                        continue;
                    }
                    if (ObjectUtil.isNotEmpty(discountInfo.getTitle())) {
                        titleSb.append(discountInfo.getTitle()).append(" ");
                    }
                    tmpDiscountInfo.setNum(tmpDiscountInfo.getNum() + (discountInfo.getNum() != null ? discountInfo.getNum() : 0));
                    tmpDiscountInfo.setMoney(tmpDiscountInfo.getMoney().add(discountInfo.getMoney() != null ? discountInfo.getMoney() : BigDecimal.ZERO));
                }
                tmpDiscountInfo.setTitle(tmpDiscountInfo.getTitle() + titleSb);
            } else if (discountType.equals("full_reduction")) {
                for (String key : new ArrayList<>(scopeOrder.getDiscount().keySet())) {
                    OrderCreateDataVo.Discount discountInfo = discountFromCacheValue(scopeOrder.getDiscount().get(key));
                    if (discountInfo == null || discountInfo.getDiscountType() == null || !discountInfo.getDiscountType().equals("full_reduction")) {
                        continue;
                    }
                    tmpDiscountInfo.setNum(tmpDiscountInfo.getNum() + (discountInfo.getNum() != null ? discountInfo.getNum() : 0));
                    tmpDiscountInfo.setMoney(tmpDiscountInfo.getMoney().add(discountInfo.getMoney() != null ? discountInfo.getMoney() : BigDecimal.ZERO));
                }
            }
        }

        if (tmpDiscountInfo.getMoney().compareTo(BigDecimal.ZERO) > 0) {
            return tmpDiscountInfo;
        }
        return new OrderCreateDataVo.Discount();
    }

    /**
     * 订单分组
     */
    private void createOrderGroup() {
        MallOrderGroup orderGroup = new MallOrderGroup();
        orderGroup.setMemberId(mallOrderCreateDataVo.getBuyerId());
        orderGroup.setBody(mallOrderCreateDataVo.getBasic().getBody());
        orderGroup.setGoodsMoney(mallOrderCreateDataVo.getBasic().getGoodsMoney());
        orderGroup.setDeliveryMoney(mallOrderCreateDataVo.getBasic().getDeliveryMoney() != null
                ? mallOrderCreateDataVo.getBasic().getDeliveryMoney() : BigDecimal.ZERO);
        orderGroup.setShopDiscountMoney(mallOrderCreateDataVo.getBasic().getShopDiscountMoney());
        orderGroup.setMallDiscountMoney(mallOrderCreateDataVo.getBasic().getMallDiscountMoney());
        orderGroup.setUsePoint(mallOrderCreateDataVo.getBasic().getUsePoint() != null ? mallOrderCreateDataVo.getBasic().getUsePoint() : 0);
        orderGroup.setPointDeductMoney(mallOrderCreateDataVo.getBasic().getPointDeductMoney() != null
                ? mallOrderCreateDataVo.getBasic().getPointDeductMoney() : BigDecimal.ZERO);
        orderGroup.setFullReductionMoney(mallOrderCreateDataVo.getBasic().getFullReductionMoney() != null
                ? mallOrderCreateDataVo.getBasic().getFullReductionMoney() : BigDecimal.ZERO);
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

                // -----------------开始----------------- 2026-05-11 对齐 PHP useMallDiscount：平台券核销；其余类型仅落库分组优惠
                switch (key) {
                    case "platform_coupon":
                        coreCouponMemberService.use(value.getDiscountTypeId(), mallOrderCreateDataVo.getGroupId());
                        break;
                    case "point_deduct":
                    case "full_reduction":
                        break;
                    case "full_gift":
                        break;
                    default:
                        break;
                }
                // -----------------结束-------------
                MallOrderGroupDiscount model = new MallOrderGroupDiscount();
                model.setGroupId(mallOrderCreateDataVo.getGroupId());
                model.setType(value.getType());
                model.setMoney(value.getMoney() != null ? value.getMoney() : BigDecimal.ZERO);
                model.setNum(value.getNum() != null ? value.getNum() : 0);
                model.setDiscountType(value.getDiscountType());
                model.setDiscountTypeId(value.getDiscountTypeId() != null ? value.getDiscountTypeId() : 0);
                model.setContent(value.getContent());
                // -----------------开始----------------- 2026-05-11 分组优惠记录创建时间
                model.setCreateTime(System.currentTimeMillis() / 1000);
                // -----------------结束-------------
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
        for (Integer siteId : param.getBody().keySet()) {
            String itemDeliveryType = StringUtils.isNotEmpty(param.getBody().get(siteId).getDelivery().getDeliveryType()) ? param.getBody().get(siteId).getDelivery().getDeliveryType() : DeliveryTypeEnum.EXPRESS.getType();
            hasSelectDeliveryTypes.add(itemDeliveryType);
        }
        mallOrderCreateDataVo.getBasic().setHasSelectDeliveryTypes(hasSelectDeliveryTypes);

        //定义收货地址
        //查询默认收货地址
        MemberAddress address = null;
        if (ObjectUtil.isNotEmpty(param.getDelivery()) && ObjectUtil.isNotEmpty(param.getDelivery().getTakeAddressId()) && param.getDelivery().getTakeAddressId() > 0) {
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
            //获取会员存在经纬度的地址
            if (hasSelectDeliveryTypes.contains(DeliveryTypeEnum.LOCAL_DELIVERY.getType()) && ObjectUtil.isNotEmpty(address) && StringUtils.isEmpty(address.getLng())) {
                address = memberAddressMapper.selectOne(new QueryWrapper<MemberAddress>()
                        .eq("member_id", param.getMemberId())
                        .and(item -> item.isNotNull("lng").ne("lng", ""))
                        .and(item -> item.isNotNull("lat").ne("lat", ""))
                        .orderByAsc("is_default", "id")
                        .last("LIMIT 1"));
            }
        }

        if (address == null) {
            mallOrderCreateDataVo.getDelivery().setTakeAddress(new JSONObject());
        } else {
            mallOrderCreateDataVo.getDelivery().setTakeAddress(address);
            mallOrderCreateDataVo.getDelivery().setTakerName(address.getName());
            mallOrderCreateDataVo.getDelivery().setTakerMobile(address.getMobile());
        }

        //填充默认收货地址的姓名和手机号给收货人姓名和收货人手机号
        if (param.getOrderFrom().equals(ChannelEnum.PC.getCode())) return;
        if (hasSelectDeliveryTypes.contains(DeliveryTypeEnum.LOCAL_DELIVERY.getType()) || hasSelectDeliveryTypes.contains(DeliveryTypeEnum.EXPRESS.getType())) {
            mallOrderCreateDataVo.getBasic().setIsNeedTakeAddress(1);
            if (ObjectUtil.isEmpty(mallOrderCreateDataVo.getDelivery().getTakeAddress())) {
                mallOrderCreateDataVo.getError().add("需要先选择收货地址");
            } else {
                if (hasSelectDeliveryTypes.contains(DeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                    JSONObject takeAddress = JSONUtil.parseObj(mallOrderCreateDataVo.getDelivery().getTakeAddress());
                    if (ObjectUtil.isEmpty(takeAddress.get("lng"))) {
                        mallOrderCreateDataVo.getError().add("所选收货地址没有使用地图精确定位");
                    }
                }
            }
        } else {
            //如果没有店铺选择物流配送和门店自提,则不需要收货地址
            mallOrderCreateDataVo.getBasic().setIsNeedTakeAddress(0);
            mallOrderCreateDataVo.getDelivery().setTakeAddress(new JSONObject());
        }
    }

    /**
     * 检测是否需要地址
     *
     * @return
     */
    private Boolean checkHasAddress() {
        Boolean er = false;
        for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {
            if (order.getBasic().getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
                er = true;
            }
        }
        if (!er) {
            mallOrderCreateDataVo.getDelivery().setTakeAddress(new JSONObject());
        }

        for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {
            order.getDelivery().setTakerMobile(mallOrderCreateDataVo.getDelivery().getTakerMobile());
            order.getDelivery().setTakerName(mallOrderCreateDataVo.getDelivery().getTakerName());
        }
        return er;
    }

    // -----------------开始----------------- 2026-05-11 合并子单 mall_data.discount 至聚合 discount / basic.full_reduction_money
    /**
     * 将单店订单 mall_data.discount 合并到聚合 mall 订单
     */
    private void mergeMallDataDiscountFromOrder(OrderCreateDataVo order) {
        if (order.getMallData() == null || !order.getMallData().containsKey("discount")) {
            return;
        }
        Object discountObj = order.getMallData().get("discount");
        if (!(discountObj instanceof Map<?, ?> rawMap)) {
            return;
        }
        for (Map.Entry<?, ?> en : rawMap.entrySet()) {
            String key = String.valueOf(en.getKey());
            Object val = en.getValue();
            if (val instanceof OrderCreateDataVo.Discount d) {
                mergeOneAggregateDiscount(key, d);
            } else if ("full_gift".equals(key) && val instanceof List<?> list) {
                int i = 0;
                for (Object o : list) {
                    if (o instanceof OrderCreateDataVo.Discount d) {
                        mergeOneAggregateDiscount(key + "_" + (i++), d);
                    }
                }
            }
        }
    }

    private void mergeOneAggregateDiscount(String key, OrderCreateDataVo.Discount d) {
        MallOrderCreateDataVo.MallDiscountVo nv = discountToMallDiscountVo(d);
        MallOrderCreateDataVo.MallDiscountVo existing = mallOrderCreateDataVo.getDiscount().get(key);
        if (existing != null && existing.getMoney() != null && nv.getMoney() != null) {
            nv.setMoney(existing.getMoney().add(nv.getMoney()));
            nv.setNum((existing.getNum() != null ? existing.getNum() : 0) + (nv.getNum() != null ? nv.getNum() : 0));
            String t1 = existing.getTitle() != null ? existing.getTitle() : "";
            String t2 = nv.getTitle() != null ? nv.getTitle() : "";
            nv.setTitle((t1 + " " + t2).trim());
        }
        mallOrderCreateDataVo.getDiscount().put(key, nv);
        if (CommonActiveEnum.FULL_REDUCTION.getType().equals(key) && d.getMoney() != null) {
            mallOrderCreateDataVo.getBasic().setFullReductionMoney(
                    mallOrderCreateDataVo.getBasic().getFullReductionMoney().add(d.getMoney()));
        }
    }

    private static MallOrderCreateDataVo.MallDiscountVo discountToMallDiscountVo(OrderCreateDataVo.Discount d) {
        MallOrderCreateDataVo.MallDiscountVo m = new MallOrderCreateDataVo.MallDiscountVo();
        m.setType(d.getType());
        m.setNum(d.getNum() != null ? d.getNum() : 0);
        m.setMoney(d.getMoney() != null ? d.getMoney() : BigDecimal.ZERO);
        m.setDiscountType(d.getDiscountType());
        m.setDiscountTypeId(d.getDiscountTypeId() != null ? d.getDiscountTypeId() : 0);
        m.setContent(d.getContent());
        m.setTitle(d.getTitle());
        return m;
    }
    // -----------------结束-------------

    /**
     * create 前按子单重算结果刷新平台汇总金额（满减/满赠重算后 order_group 落库金额与子单一致）。
     */
    private void refreshMallBasicFromOrderList() {
        BigDecimal goodsMoney = BigDecimal.ZERO;
        BigDecimal deliveryMoney = BigDecimal.ZERO;
        BigDecimal shopDiscountMoney = BigDecimal.ZERO;
        BigDecimal orderMoney = BigDecimal.ZERO;
        BigDecimal fullReductionMoney = BigDecimal.ZERO;
        BigDecimal mallDiscountMoney = BigDecimal.ZERO;
        BigDecimal pointDeductMoney = BigDecimal.ZERO;
        int usePoint = 0;
        for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderList().values()) {
            if (order.getBasic() == null) {
                continue;
            }
            goodsMoney = goodsMoney.add(order.getBasic().getGoodsMoney() != null ? order.getBasic().getGoodsMoney() : BigDecimal.ZERO);
            deliveryMoney = deliveryMoney.add(order.getBasic().getDeliveryMoney() != null ? order.getBasic().getDeliveryMoney() : BigDecimal.ZERO);
            shopDiscountMoney = shopDiscountMoney.add(order.getBasic().getDiscountMoney() != null ? order.getBasic().getDiscountMoney() : BigDecimal.ZERO);
            orderMoney = orderMoney.add(order.getBasic().getOrderMoney() != null ? order.getBasic().getOrderMoney() : BigDecimal.ZERO);
            fullReductionMoney = fullReductionMoney.add(order.getBasic().getFullReductionMoney() != null ? order.getBasic().getFullReductionMoney() : BigDecimal.ZERO);
            mallDiscountMoney = mallDiscountMoney.add(order.getBasic().getMallDiscountMoney() != null ? order.getBasic().getMallDiscountMoney() : BigDecimal.ZERO);
            pointDeductMoney = pointDeductMoney.add(order.getBasic().getPointDeductMoney() != null ? order.getBasic().getPointDeductMoney() : BigDecimal.ZERO);
            usePoint += order.getBasic().getUsePoint() != null ? order.getBasic().getUsePoint() : 0;
        }
        mallOrderCreateDataVo.getBasic().setGoodsMoney(goodsMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setDeliveryMoney(deliveryMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setShopDiscountMoney(shopDiscountMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setOrderMoney(orderMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setFullReductionMoney(fullReductionMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setMallDiscountMoney(mallDiscountMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setPointDeductMoney(pointDeductMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setUsePoint(usePoint);
    }

    /**
     * split 后按 orderHandleList 刷新分组级积分与平台优惠汇总。
     */
    private void refreshMallAggregateFromOrderHandles() {
        if (mallOrderCreateDataVo.getOrderHandleList() == null || mallOrderCreateDataVo.getOrderHandleList().isEmpty()) {
            return;
        }
        BigDecimal goodsMoney = BigDecimal.ZERO;
        BigDecimal deliveryMoney = BigDecimal.ZERO;
        BigDecimal shopDiscountMoney = BigDecimal.ZERO;
        BigDecimal orderMoney = BigDecimal.ZERO;
        BigDecimal fullReductionMoney = BigDecimal.ZERO;
        BigDecimal mallDiscountMoney = BigDecimal.ZERO;
        BigDecimal pointDeductMoney = BigDecimal.ZERO;
        int usePoint = 0;
        for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {
            if (order.getBasic() == null) {
                continue;
            }
            goodsMoney = goodsMoney.add(order.getBasic().getGoodsMoney() != null ? order.getBasic().getGoodsMoney() : BigDecimal.ZERO);
            deliveryMoney = deliveryMoney.add(order.getBasic().getDeliveryMoney() != null ? order.getBasic().getDeliveryMoney() : BigDecimal.ZERO);
            shopDiscountMoney = shopDiscountMoney.add(order.getBasic().getDiscountMoney() != null ? order.getBasic().getDiscountMoney() : BigDecimal.ZERO);
            orderMoney = orderMoney.add(order.getBasic().getOrderMoney() != null ? order.getBasic().getOrderMoney() : BigDecimal.ZERO);
            fullReductionMoney = fullReductionMoney.add(order.getBasic().getFullReductionMoney() != null ? order.getBasic().getFullReductionMoney() : BigDecimal.ZERO);
            mallDiscountMoney = mallDiscountMoney.add(order.getBasic().getMallDiscountMoney() != null ? order.getBasic().getMallDiscountMoney() : BigDecimal.ZERO);
            pointDeductMoney = pointDeductMoney.add(order.getBasic().getPointDeductMoney() != null ? order.getBasic().getPointDeductMoney() : BigDecimal.ZERO);
            usePoint += order.getBasic().getUsePoint() != null ? order.getBasic().getUsePoint() : 0;
        }
        mallOrderCreateDataVo.getBasic().setGoodsMoney(goodsMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setDeliveryMoney(deliveryMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setShopDiscountMoney(shopDiscountMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setOrderMoney(orderMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setFullReductionMoney(fullReductionMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setMallDiscountMoney(mallDiscountMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setPointDeductMoney(pointDeductMoney.setScale(2, RoundingMode.DOWN));
        mallOrderCreateDataVo.getBasic().setUsePoint(usePoint);
    }

    /**
     * 将 discount 汇总结果或行项积分回写到 basic，供 mall_order 主表落库。
     */
    private void syncPointDeductBasicFromDiscount(OrderCreateDataVo vo) {
        if (vo == null || vo.getBasic() == null) {
            return;
        }
        OrderCreateDataVo.Basic basic = vo.getBasic();
        BigDecimal pointMoney = BigDecimal.ZERO;
        int pointNum = 0;
        if (vo.getDiscount() != null && vo.getDiscount().containsKey("point_deduct")) {
            OrderCreateDataVo.Discount pointDiscount = discountFromCacheValue(vo.getDiscount().get("point_deduct"));
            if (pointDiscount != null && pointDiscount.getMoney() != null && pointDiscount.getMoney().compareTo(BigDecimal.ZERO) > 0) {
                pointMoney = pointDiscount.getMoney();
                pointNum = pointDiscount.getNum() != null ? pointDiscount.getNum() : 0;
            }
        }
        if (pointMoney.compareTo(BigDecimal.ZERO) <= 0 && vo.getGoodsData() != null) {
            for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                OrderCreateDataVo.Goods goods = sku.getGoods();
                BigDecimal skuPointMoney = sku.getPointDeductMoney() != null && sku.getPointDeductMoney().compareTo(BigDecimal.ZERO) > 0
                        ? sku.getPointDeductMoney()
                        : (goods != null && goods.getPointDeductMoney() != null ? goods.getPointDeductMoney() : BigDecimal.ZERO);
                pointMoney = pointMoney.add(skuPointMoney);
                int skuUsePoint = sku.getUsePoint() != null && sku.getUsePoint() > 0
                        ? sku.getUsePoint()
                        : (goods != null && goods.getUsePoint() != null ? goods.getUsePoint() : 0);
                pointNum += skuUsePoint;
            }
        }
        if (pointMoney.compareTo(BigDecimal.ZERO) > 0) {
            basic.setPointDeductMoney(pointMoney);
            basic.setUsePoint(pointNum);
            BigDecimal mallDiscount = ObjectUtil.defaultIfNull(basic.getMallDiscountMoney(), BigDecimal.ZERO);
            BigDecimal mallCoupon = ObjectUtil.defaultIfNull(basic.getMallCouponMoney(), BigDecimal.ZERO);
            BigDecimal nonCouponMallDiscount = mallDiscount.subtract(mallCoupon).max(BigDecimal.ZERO);
            if (nonCouponMallDiscount.compareTo(pointMoney) < 0) {
                basic.setMallDiscountMoney(mallDiscount.add(pointMoney.subtract(nonCouponMallDiscount)));
            }
            OrderCreateHelper.refreshBasicOrderMoneyStatic(vo);
        }
    }

    /**
     * 复制 discount Map，避免供货拆单各批次互相污染。
     */
    private Map<String, OrderCreateDataVo.Discount> copyDiscountMap(Map<String, OrderCreateDataVo.Discount> source) {
        Map<String, OrderCreateDataVo.Discount> copy = new HashMap<>();
        if (source == null || source.isEmpty()) {
            return copy;
        }
        for (Map.Entry<String, OrderCreateDataVo.Discount> entry : source.entrySet()) {
            OrderCreateDataVo.Discount d = discountFromCacheValue(entry.getValue());
            if (d != null) {
                copy.put(entry.getKey(), d);
            }
        }
        return copy;
    }

    /**
     * 按供货批次 SKU 过滤行项优惠明细（对应 PHP order_goods_discount）。
     */
    private Map<String, OrderCreateDataVo.Discount> filterGoodsDiscountBySkus(
            Map<String, OrderCreateDataVo.Discount> goodsDiscount, List<OrderCreateDataVo.Sku> skus) {
        if (goodsDiscount == null || goodsDiscount.isEmpty() || skus == null || skus.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, OrderCreateDataVo.Discount> filtered = new HashMap<>();
        for (OrderCreateDataVo.Sku sku : skus) {
            String prefix = sku.getSiteId() + "_" + sku.getGoodsId() + "_" + sku.getSkuId();
            for (Map.Entry<String, OrderCreateDataVo.Discount> entry : goodsDiscount.entrySet()) {
                if (entry.getKey().startsWith(prefix)) {
                    filtered.put(entry.getKey(), discountFromCacheValue(entry.getValue()));
                }
            }
        }
        return filtered;
    }

    /**
     * 统筹计算订单（多站点/多店铺汇总）
     */
    private void calculateOfMall() {

        // 1. 初始化整体金额
        mallOrderCreateDataVo.getBasic().setShopDiscountMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setDeliveryMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setGoodsMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setOrderMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setBody("");
        mallOrderCreateDataVo.getBasic().setUsePoint(0);
        mallOrderCreateDataVo.getBasic().setPreUsePoint(0);
        mallOrderCreateDataVo.getBasic().setPrePointDeductMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setMallDiscountMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setPointDeductMoney(BigDecimal.ZERO);
        // -----------------开始----------------- 2026-05-11 对齐 本轮统筹前清空聚合优惠与满减/平台券金额，避免单例复用脏数据
        mallOrderCreateDataVo.getBasic().setFullReductionMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setMallCouponMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getDiscount().clear();
        // -----------------结束-------------

        // 2. 遍历所有子订单，累加基础数据
        for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {
            // 累加店铺优惠金额（ discount_money）
            mallOrderCreateDataVo.getBasic().setShopDiscountMoney(mallOrderCreateDataVo.getBasic().getShopDiscountMoney()
                    .add(order.getBasic().getDiscountMoney()).setScale(2, RoundingMode.DOWN));
            // 累加物流费
            mallOrderCreateDataVo.getBasic().setDeliveryMoney(mallOrderCreateDataVo.getBasic().getDeliveryMoney()
                    .add(order.getBasic().getDeliveryMoney()).setScale(2, RoundingMode.DOWN));
            // 累加商品总额
            mallOrderCreateDataVo.getBasic().setGoodsMoney(mallOrderCreateDataVo.getBasic().getGoodsMoney()
                    .add(order.getBasic().getGoodsMoney()).setScale(2, RoundingMode.DOWN));
            // 累加订单最终应付金额
            mallOrderCreateDataVo.getBasic().setOrderMoney(mallOrderCreateDataVo.getBasic().getOrderMoney()
                    .add(order.getBasic().getOrderMoney()).setScale(2, RoundingMode.DOWN));
            // 拼接订单描述
            mallOrderCreateDataVo.getBasic().setBody(mallOrderCreateDataVo.getBasic().getBody() + order.getBasic().getBody());
            // 累加使用的积分（ 中 bcadd 处理）
            mallOrderCreateDataVo.getBasic().setUsePoint(mallOrderCreateDataVo.getBasic().getUsePoint() + order.getBasic().getUsePoint());
            // 累加平台优惠金额（平台券、积分抵扣等）
            mallOrderCreateDataVo.getBasic().setMallDiscountMoney(mallOrderCreateDataVo.getBasic().getMallDiscountMoney()
                    .add(order.getBasic().getMallDiscountMoney()).setScale(2, RoundingMode.DOWN));

            // 3. 合并子订单中的平台级优惠数据（ 中的 array_merge）
            // -----------------开始----------------- 2026-05-11
            mergeMallDataDiscountFromOrder(order);
            // -----------------结束-------------

            // 4. 控制平台优惠券开关：只要有一个子订单显式禁用，整体禁用（对齐 PHP：!$order_instance->is_plat_coupon）
            if (order.getIsPlatCoupon() != null && order.getIsPlatCoupon() == 0) {
                mallOrderCreateDataVo.setIsPlatCoupon(0);
            }
        }
        // 5. 调用平台优惠计算（平台券、积分抵扣等）
        calculateDiscount();
        // 6. 触发扩展点（的 event）
        // 可通过 Spring 的 ApplicationEventPublisher 发布事件，供其他模块监听
        // applicationEventPublisher.publishEvent(new MallOrderDiscountCalculateEvent(this));
    }

    /**
     * 计算折扣
     */
    private void calculateDiscount() {
        calculateCoupon();

        //26-04-02 LQ添加 积分抵扣计算
        calculatePointDeduct();
    }

    /**
     * 通过站点id数组查询订单列表
     *
     * @param siteIds
     * @return
     */
    // -----------------开始----------------- 2026-05-11 对齐 getOrderListBySiteIds：仅保留适用站点内的子订单；join_site_ids 与 siteId 类型统一比较
    private Map<String, OrderCreateDataVo> getOrderListBySiteIds(JSONArray siteIds) {
        if (ObjectUtil.isEmpty(siteIds)) {
            return new LinkedHashMap<>(mallOrderCreateDataVo.getOrderHandleList());
        }
        Map<String, OrderCreateDataVo> filteredList = new LinkedHashMap<>();
        for (Map.Entry<String, OrderCreateDataVo> e : mallOrderCreateDataVo.getOrderHandleList().entrySet()) {
            Integer sid = e.getValue().getSiteId();
            if (sid != null && jsonArrayContainsSiteId(siteIds, sid)) {
                filteredList.put(e.getKey(), e.getValue());
            }
        }
        return filteredList;
    }

    private static boolean jsonArrayContainsSiteId(JSONArray siteIds, Integer siteId) {
        if (siteIds == null || siteId == null) {
            return false;
        }
        String s = String.valueOf(siteId);
        for (int i = 0; i < siteIds.size(); i++) {
            Object o = siteIds.get(i);
            if (o != null && String.valueOf(o).equals(s)) {
                return true;
            }
        }
        return false;
    }
    // -----------------结束-------------

    /**
     * 计算平台优惠金额
     *
     * @throws CommonException 如果优惠券不存在或不满足使用条件
     */
    private void calculateCoupon() throws CommonException {
        // -----------------开始----------------- 2026-05-11 不在此处清空 discount（已在 calculateOfMall 合并 mall_data）；重置本轮平台券相关聚合金额
        mallOrderCreateDataVo.getBasic().setMallDiscountMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setMallCouponMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getDiscount().remove("platform_coupon");
        MallOrderCalculateParam.MallDiscount mallDiscount = param.getMallDiscount();
        Integer platformCouponId = mallDiscount != null ? mallDiscount.getPlatformCouponId() : null;
        // -----------------结束-------------
        if (platformCouponId != null && platformCouponId > 0 && mallOrderCreateDataVo.getIsPlatCoupon() > 0) {
            // 查询优惠券信息
            MallMemberCouponInfoVo couponInfo = coreCouponMemberService.getUsePlatformCouponInfoById(platformCouponId);

            if (couponInfo != null) {
                String couponType = couponInfo.getType();
                List<MallCouponGoods> goodsData = couponInfo.getGoods();
                BigDecimal mallDiscountMoney = couponInfo.getPrice();
                BigDecimal minConditionMoney = couponInfo.getMinConditionMoney();
                Integer isAllSiteJoin = couponInfo.getCoupon().getIsAllSiteJoin();
                JSONArray joinSiteIds = new JSONArray();
                if (ObjectUtil.isNotEmpty(couponInfo.getCoupon().getJoinSiteIds())) {
                    joinSiteIds = JSONUtil.parseArray(couponInfo.getCoupon().getJoinSiteIds());
                }


                Map<String, OrderCreateDataVo> orderList = getOrderListBySiteIds(
                        CouponIsAllSiteEnum.ALL_SITE_JOIN.getType().equals(isAllSiteJoin) ? null : joinSiteIds
                );

                BigDecimal matchGoodsMoney = BigDecimal.ZERO;
                List<Integer> discountSiteIds = new ArrayList<>();
                boolean usable = false;
                CouponTypeEnum typeEnum = CouponTypeEnum.getEnumByType(Integer.valueOf(couponType));
                switch (typeEnum) {
                    case ALL:
                        // 通用优惠券
                        usable = true;
                        for (OrderCreateDataVo order : orderList.values()) {
                            discountSiteIds.add(order.getSiteId());
                            matchGoodsMoney = matchGoodsMoney.add(
                                    order.getBasic().getGoodsMoney().subtract(order.getBasic().getDiscountMoney())
                            );
                        }
                        break;
                    case MALL_CATEGORY:
                        // 品类优惠券
                        List<Integer> categoryIds = goodsData.stream()
                                .map(MallCouponGoods::getMallCategoryId).toList();

                        for (OrderCreateDataVo order : orderList.values()) {
                            for (OrderCreateDataVo.Sku sku : order.getGoodsData().values()) {
                                Integer goodsMallCategory = sku.getGoods().getGoodsMallCategory();
                                if (categoryIds.contains(goodsMallCategory)) {
                                    usable = true;
                                    discountSiteIds.add(order.getSiteId());
                                    matchGoodsMoney = matchGoodsMoney.add(
                                            sku.getGoodsMoney().subtract(sku.getDiscountMoney())
                                    );
                                }
                            }
                        }
                        break;
                    case BRAND:
                        // 品牌优惠券
                        List<Integer> brandIds = goodsData.stream()
                                .map(MallCouponGoods::getBrandId).toList();

                        for (OrderCreateDataVo order : orderList.values()) {
                            for (OrderCreateDataVo.Sku sku : order.getGoodsData().values()) {
                                Integer brandId = sku.getGoods().getBrandId();
                                if (brandIds.contains(brandId)) {
                                    usable = true;
                                    discountSiteIds.add(order.getSiteId());
                                    matchGoodsMoney = matchGoodsMoney.add(
                                            sku.getGoodsMoney().subtract(sku.getDiscountMoney())
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
                                order.getBasic().setMallCouponMoney(tempRealGoodsMoney);
                            }

                            order.getBasic().setOrderMoney(
                                    order.getBasic().getOrderMoney().subtract(order.getBasic().getMallDiscountMoney())
                                            .setScale(2, RoundingMode.DOWN)
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
                            discount.setNum(1);
                            order.getDiscount().put("platform_coupon", discount);
                        }
                    }

                    mallOrderCreateDataVo.getBasic().setMallDiscountMoney(realMallDiscountGoodsMoney);
                    // -----------------开始----------------- 2026-05-11  basic.mall_coupon_money
                    mallOrderCreateDataVo.getBasic().setMallCouponMoney(realMallDiscountGoodsMoney);
                    // -----------------结束-------------

                    if (mallOrderCreateDataVo.getBasic().getMallDiscountMoney()
                            .compareTo(mallOrderCreateDataVo.getBasic().getOrderMoney()) > 0) {
                        BigDecimal cap = mallOrderCreateDataVo.getBasic().getOrderMoney();
                        mallOrderCreateDataVo.getBasic().setMallDiscountMoney(cap);
                        // -----------------开始----------------- 2026-05-11 与 封顶时 mall_coupon_money 同步
                        mallOrderCreateDataVo.getBasic().setMallCouponMoney(cap);
                        // -----------------结束-------------
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
     * 解析商品实际积分抵扣比例；不可抵扣时返回 null，site 类型写回站点比例到商品对象
     */
    private BigDecimal resolvePointDeductRate(OrderCreateDataVo.Goods goods, PointDeduct sitePointDeductConfig) {
        String pointDeductType = goods.getPointDeductType() != null ? goods.getPointDeductType() : GoodsDictEnum.SITE.getValue();
        BigDecimal pointDeductRate = goods.getPointDeductRate() != null ? goods.getPointDeductRate() : BigDecimal.ZERO;
        if (pointDeductType.equals(GoodsDictEnum.CUSTOM.getValue()) && pointDeductRate.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        if (pointDeductType.equals(GoodsDictEnum.SITE.getValue())) {
            pointDeductRate = sitePointDeductConfig.getDeductRate() != null ? sitePointDeductConfig.getDeductRate() : BigDecimal.ZERO;
            goods.setPointDeductRate(pointDeductRate);
        }
        return pointDeductRate;
    }

    /**
     * 26-04-02 LQ添加 积分抵扣计算
     */
    private void calculatePointDeduct() {
        PointDeduct pointDeductConfig = corePointDeductService.getPointDeductConfig();
        Integer isOpen = pointDeductConfig.getIsOpen() != null ? pointDeductConfig.getIsOpen() : 0;
        mallOrderCreateDataVo.getBasic().setIsCanPointDeduct(isOpen);

        // -----------------开始----------------- 2026-05-11 未传 is_use_point_deduct 时按 0 处理
        Integer isUsePoint = param.getIsUsePointDeduct() != null ? param.getIsUsePointDeduct() : 0;
        // -----------------结束-------------

        // 获取会员总积分
        Integer orderMemberPoint = mallOrderCreateDataVo.getBuyer() != null ?
                mallOrderCreateDataVo.getBuyer().getPoint() != null ?
                        mallOrderCreateDataVo.getBuyer().getPoint() : 0
                : 0;

        // 2. 遍历订单处理列表
        // 先预计算每个站点订单的预计抵扣金额
        for (var orderHandle : mallOrderCreateDataVo.getOrderHandleList().values()) {
            if (orderHandle.getIsCanUsePoint() == 0) {
                continue;
            }
            var sitePointDeductConfig = corePointDeductService.getSitePointDeductConfig(orderHandle.getSiteId());

            var basic = orderHandle.getBasic() != null ? orderHandle.getBasic() : new OrderCreateDataVo.Basic();
            basic.setPointDeductMoney(BigDecimal.ZERO);
            basic.setUsePoint(0);

            BigDecimal allDeductGoodsMoney = BigDecimal.ZERO;
            for (Map.Entry<Integer, ? extends OrderCreateDataVo.Sku> entry : orderHandle.getGoodsData().entrySet()) {
                var item = entry.getValue();
                var goods = item.getGoods();

                BigDecimal pointDeductRate = resolvePointDeductRate(goods, sitePointDeductConfig);
                if (pointDeductRate == null) {
                    continue;
                }

                // 标记整体订单允许积分抵扣
                mallOrderCreateDataVo.getBasic().setIsCanPointDeduct(1);

                // 计算商品实际金额 = 商品金额 - 店铺优惠 - 平台优惠
                BigDecimal realGoodsMoney = item.getGoodsMoney()
                        .subtract(item.getDiscountMoney() != null ? item.getDiscountMoney() : BigDecimal.ZERO)
                        .subtract(item.getMallDiscountMoney() != null ? item.getMallDiscountMoney() : BigDecimal.ZERO);

                // 计算可抵扣金额 = 商品实际金额 * (抵扣比例 / 100)
                BigDecimal rateDiv100 = pointDeductRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal price = realGoodsMoney.multiply(rateDiv100).setScale(4, RoundingMode.HALF_UP);

                // 累加可抵扣金额（保留2位小数）
                allDeductGoodsMoney = allDeductGoodsMoney.add(price).setScale(2, RoundingMode.HALF_UP);
            }

            // 调用积分抵扣服务计算可抵扣信息
            var deductInfo = corePointDeductService.calculateDeductMoney(
                    orderMemberPoint, allDeductGoodsMoney, pointDeductConfig, null, null);
            // 累加预计抵扣金额（保留两位小数）
            mallOrderCreateDataVo.getBasic().setPrePointDeductMoney
                    (mallOrderCreateDataVo.getBasic().getPrePointDeductMoney().add(deductInfo.getDeductMoney()).setScale(2, RoundingMode.HALF_UP));
            // 累加预计使用积分（积分是整数，直接加）
            mallOrderCreateDataVo.getBasic().setPreUsePoint(mallOrderCreateDataVo.getBasic().getPreUsePoint() + deductInfo.getUsePoint());
            // 扣减会员可用积分
            orderMemberPoint -= deductInfo.getUsePoint();


            // 判断用户是否使用积分抵扣
            if (isUsePoint == 1) {
                BigDecimal deductMoney = deductInfo.getDeductMoney();
                Integer usePoint = deductInfo.getUsePoint();

                // 更新子订单的基础金额（仅维护优惠字段，order_money 由 refreshBasicOrderMoney 统一重算）
                basic.setMallDiscountMoney(basic.getMallDiscountMoney().add(deductMoney));
                basic.setPointDeductMoney(deductMoney);
                basic.setUsePoint(usePoint);

                // 更新整体订单基础数据
                mallOrderCreateDataVo.getBasic().setUsePoint(mallOrderCreateDataVo.getBasic().getUsePoint() + usePoint);
                mallOrderCreateDataVo.getBasic().setPointDeductMoney(mallOrderCreateDataVo.getBasic().getPointDeductMoney().add(deductMoney));
                mallOrderCreateDataVo.getBasic().setMallDiscountMoney(mallOrderCreateDataVo.getBasic().getMallDiscountMoney().add(deductMoney));

                // 商品积分分摊
                TreeMap<Integer, OrderCreateDataVo.Sku> goodsData = new TreeMap<>(orderHandle.getGoodsData());

                // 统计可抵扣商品数量（含 site 类型继承站点比例的商品）
                long validGoodsCount = goodsData.entrySet().stream()
                        .filter(item -> {
                            BigDecimal rate = resolvePointDeductRate(item.getValue().getGoods(), sitePointDeductConfig);
                            return rate != null && rate.compareTo(BigDecimal.ZERO) > 0;
                        }).count();

                Integer maxUsePoint = usePoint;          // 剩余可用积分（按商品分摊）
                Integer orderGoodsMemberPoint = orderMemberPoint; // 当前剩余会员积分

                for (var item : goodsData.entrySet()) {
                    var goods = item.getValue().getGoods();
                    BigDecimal pointDeductRate = resolvePointDeductRate(goods, sitePointDeductConfig);
                    if (pointDeductRate == null) {
                        continue;
                    }

                    // 初始化商品的抵扣字段
                    goods.setUsePoint(0);
                    goods.setPointDeductMoney(BigDecimal.ZERO);
                    goods.setMallDiscountMoney(goods.getMallDiscountMoney() != null ? goods.getMallDiscountMoney() : BigDecimal.ZERO);

                    // 计算商品实际金额 = 商品金额 - 店铺优惠 - 平台优惠
                    BigDecimal goodsMoney = item.getValue().getGoodsMoney();
                    BigDecimal discountMoney = item.getValue().getDiscountMoney() != null ?
                            item.getValue().getDiscountMoney() : BigDecimal.ZERO;
                    BigDecimal mallDiscountMoney = item.getValue().getMallDiscountMoney();
                    BigDecimal datumGoodsMoney = goodsMoney.subtract(discountMoney).subtract(mallDiscountMoney);

                    // 商品最大可抵扣金额 = 商品实际金额 * (抵扣率 / 100)
                    BigDecimal rateDiv100 = pointDeductRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.DOWN);
                    BigDecimal goodsDeductableMoney = datumGoodsMoney.multiply(rateDiv100).setScale(2, RoundingMode.DOWN);

                    // 是否为最后一个可抵扣商品
                    validGoodsCount--;
                    boolean isLastValid = validGoodsCount == 0;

                    DeductMoneyVo tmpDeduct = corePointDeductService.calculateDeductMoney(
                            orderGoodsMemberPoint, goodsDeductableMoney, pointDeductConfig, true, maxUsePoint);
                    // 更新商品抵扣数据
                    goods.setUsePoint(tmpDeduct.getUsePoint());
                    orderGoodsMemberPoint -= tmpDeduct.getUsePoint();
                    goods.setPointDeductMoney(tmpDeduct.getDeductMoney());
                    maxUsePoint -= tmpDeduct.getUsePoint();

                    // 更新商品平台优惠金额
                    goods.setMallDiscountMoney(goods.getMallDiscountMoney().add(tmpDeduct.getDeductMoney()));

                    // 与 PHP $datum 一致：积分抵扣同步写入 SKU，供落单与退款读取
                    OrderCreateDataVo.Sku sku = item.getValue();
                    sku.setUsePoint(tmpDeduct.getUsePoint());
                    sku.setPointDeductMoney(tmpDeduct.getDeductMoney());
                    sku.setMallDiscountMoney(
                            ObjectUtil.defaultIfNull(sku.getMallDiscountMoney(), BigDecimal.ZERO)
                                    .add(tmpDeduct.getDeductMoney())
                    );

                    // 记录订单商品折扣明细
                    if (goods.getUsePoint() > 0) {
                        String uniqueKey = goods.getSiteId() + "_" + goods.getGoodsId() + "_" + item.getValue().getSkuId() + ":" + "point_deduct";

                        // 1. 记录订单商品折扣明细（order_goods_discount）
                        OrderCreateDataVo.Discount orderGoodsDiscount = new OrderCreateDataVo.Discount();
                        orderGoodsDiscount.setMatchGoodsIds(new Integer[]{item.getValue().getSkuId()});
                        orderGoodsDiscount.setType("discount");
                        orderGoodsDiscount.setDiscountType("point_deduct");
                        orderGoodsDiscount.setNum(goods.getUsePoint());
                        orderGoodsDiscount.setMoney(tmpDeduct.getDeductMoney());
                        orderGoodsDiscount.setOrderDiscountId(0);
                        orderGoodsDiscount.setContent("参与积分抵扣");
                        orderGoodsDiscount.setTitle("积分抵扣");
                        orderHandle.getGoodsDiscount().put(uniqueKey, orderGoodsDiscount);

                        // 2. 记录订单整体折扣明细（discount）
                        OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                        discount.setMatchGoodsIds(new Integer[]{item.getValue().getSkuId()});
                        discount.setType("discount");
                        discount.setNum(tmpDeduct.getUsePoint());
                        discount.setMoney(tmpDeduct.getDeductMoney());
                        discount.setDiscountType("point_deduct");
                        discount.setDiscountTypeId(0);
                        discount.setContent("");
                        discount.setTitle("积分抵扣");
                        orderHandle.getDiscount().put(uniqueKey, discount);
                    }
                }
                OrderCreateHelper.refreshBasicOrderMoneyStatic(orderHandle);
            }
        }

        refreshMallAggregateFromOrderHandles();

        if (mallOrderCreateDataVo.getBasic().getPrePointDeductMoney().compareTo(BigDecimal.ZERO) >= 0) {
            MallOrderCreateDataVo.MallDiscountVo mallDiscount = new MallOrderCreateDataVo.MallDiscountVo();
            mallDiscount.setMatchGoodsIds(List.of(new Integer[]{}));
            mallDiscount.setType("discount");
            mallDiscount.setNum(mallOrderCreateDataVo.getBasic().getUsePoint());
            mallDiscount.setMoney(mallOrderCreateDataVo.getBasic().getPointDeductMoney());
            mallDiscount.setDiscountType("point_deduct");
            mallDiscount.setDiscountTypeId(0);
            mallDiscount.setContent("");
            mallDiscount.setTitle("积分抵扣");
            mallOrderCreateDataVo.getDiscount().put("point_deduct", mallDiscount);

            mallOrderCreateDataVo.getBasic().setIsCanPointDeduct(1);
        }
        if (mallOrderCreateDataVo.getBasic().getPrePointDeductMoney().compareTo(BigDecimal.ZERO) <= 0) {
            mallOrderCreateDataVo.getBasic().setIsCanPointDeduct(0);
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

        T t = JSONUtil.toBean(JSONUtil.parseObj(data), clazz);
        if (t.getOrderHandleList() == null) {
            t.setOrderHandleList(new HashMap<>());
        }
        return t;
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
     * 移除订单数据缓存（含各子单 order_key 与 _basic 快照）
     *
     * @param orderKey
     */
    protected void removeOrderVo(String orderKey) {
        Cached cache = CacheFactory.getCacheOperator();
        String memberPrefix = RequestUtils.memberId() + "_member_order_";
        if (mallOrderCreateDataVo != null) {
            Set<String> siteOrderKeys = new LinkedHashSet<>();
            if (mallOrderCreateDataVo.getOrderList() != null) {
                for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderList().values()) {
                    if (order != null && StringUtils.isNotEmpty(order.getOrderKey())) {
                        siteOrderKeys.add(order.getOrderKey());
                    }
                }
            }
            if (mallOrderCreateDataVo.getOrderHandleList() != null) {
                for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {
                    if (order != null && StringUtils.isNotEmpty(order.getOrderKey())) {
                        siteOrderKeys.add(order.getOrderKey());
                    }
                }
            }
            for (String siteOrderKey : siteOrderKeys) {
                cache.remove(memberPrefix + siteOrderKey);
                cache.remove(memberPrefix + siteOrderKey + "_basic");
            }
        }
        cache.remove(memberPrefix + orderKey);
    }

    /**
     * 缓存订单数据
     */
    protected void cacheOrderVo() {
        if (ObjectUtil.isEmpty(mallOrderCreateDataVo.getOrderKey())) mallOrderCreateDataVo.setOrderKey(createOrderNo());
        Cached cache = CacheFactory.getCacheOperator();
        JSONObject jo = JSONUtil.parseObj(mallOrderCreateDataVo);
        jo.remove("orderHandleList");
        cache.put(RequestUtils.memberId() + "_member_order_" + mallOrderCreateDataVo.getOrderKey(), jo.toString());
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
     * 配置设置或查询
     *
     * @param key
     */
    public void getConfig(String key) {
        //查询购物配置
        switch (key) {
            case "order":
                OrderConfigVo orderConfig = coreOrderConfigService.getConfig();
                MallOrderCreateDataVo.CloseOrder closeOrder = new MallOrderCreateDataVo.CloseOrder();
                closeOrder.setIsClose(orderConfig.getCloseOrderInfo().getIsClose());
                closeOrder.setCloseLength(orderConfig.getCloseOrderInfo().getCloseLength());
                mallOrderCreateDataVo.getConfig().setOrder(closeOrder);
                break;
            case "delivery":
                AdminDeliveryConfigVo deliveryObj = coreDeliveryConfigService.getAdminDeliveryConfig();
                MallOrderCreateDataVo.Delivery delivery = new MallOrderCreateDataVo.Delivery();
                BeanUtils.copyProperties(deliveryObj, delivery);
                mallOrderCreateDataVo.getConfig().setDelivery(delivery);
                break;
        }
    }
}
