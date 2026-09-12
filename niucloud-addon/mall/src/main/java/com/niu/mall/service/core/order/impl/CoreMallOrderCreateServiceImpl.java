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
import com.niu.core.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGroup;
import com.niu.mall.entity.order.MallOrderGroupDiscount;
import com.niu.core.enums.coupon.CouponIsAllSiteEnum;
import com.niu.core.enums.coupon.CouponStatusEnum;
import com.niu.core.enums.coupon.CouponTypeEnum;
import com.niu.mall.enums.goods.GoodsDictEnum;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderGroupEnum;
import com.niu.mall.mapper.order.MallOrderGroupDiscountMapper;
import com.niu.mall.mapper.order.MallOrderGroupMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.core.service.core.marketing.ICoreCouponMemberService;
import com.niu.core.service.core.marketing.vo.MallMemberCouponInfoVo;
import com.niu.core.service.core.marketing.vo.MallMemberCouponListVo;
import com.niu.mall.service.core.order.ICoreMallOrderCreateService;
import com.niu.mall.service.core.order.ICoreOrderConfigService;
import com.niu.mall.service.core.order.ICoreOrderCreateService;
import com.niu.mall.service.core.order.ICoreOrderEventService;
import com.niu.mall.service.core.order.helper.OrderCreateHelper;
import com.niu.mall.service.core.order.ICoreOrderPayService;
import com.niu.mall.event.order.OrderCreateAfterEvent;
import com.niu.mall.service.core.order.param.*;
import com.niu.mall.service.core.order.vo.*;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class CoreMallOrderCreateServiceImpl implements ICoreMallOrderCreateService {

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private MallOrderGroupMapper mallOrderGroupMapper;

    @Resource
    private MallOrderMapper mallOrderMapper;

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
    ICoreDeliveryConfigService coreDeliveryConfigService;
    @Resource
    ICoreOrderConfigService coreOrderConfigService;
    @Resource
    private ICorePointDeductService corePointDeductService;

    @Resource
    private ICoreOrderEventService coreOrderEventService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * 多站点订单计算：逐站点调单站点 calculate → 统筹平台优惠 → 汇总 orderList → 写缓存。
     */
    @Override
    public MallOrderCreateDataVo calculate(MallOrderCalculateParam param) {
        MallOrderCreateDataVo mallOrderCreateDataVo = new MallOrderCreateDataVo();
        mallOrderCreateDataVo.setError(new ArrayList<>());
        mallOrderCreateDataVo.setIsPlatCoupon(1);
        mallOrderCreateDataVo.setIsSupply(null);
        mallOrderCreateDataVo.setGroupId(0);
        mallOrderCreateDataVo.setBuyerId(param.getMemberId());
        OrderHelperParam.Position position = new OrderHelperParam.Position();
        if (ObjectUtil.isNotEmpty(param.getPosition()) && StringUtils.isNotEmpty(param.getPosition().toString())) {
            position = BeanUtil.toBean(param.getPosition(), OrderHelperParam.Position.class);
        }

        MallOrderCreateDataVo.Buyer buyer = new MallOrderCreateDataVo.Buyer();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                .select("nickname, point")
                .eq("member_id", param.getMemberId()));
        if (member == null) throw new CommonException("找不到买家");

        BeanUtil.copyProperties(member, buyer);
        buyer.setMemberLevel(memberLevelService.findMemberLevel(param.getMemberId()));
        mallOrderCreateDataVo.setBuyer(buyer);

        selectTakeAddress(param, mallOrderCreateDataVo);
        getConfig("delivery", mallOrderCreateDataVo);
        mallOrderCreateDataVo.setOrderHandleList(new HashMap<>());
        // 【循环】param.body：key=siteId，value=该站点的下单参数 → 调单站点 calculate，结果写入 orderHandleList
        JSONObject extendData = new JSONObject();
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
            extendData = entry.getValue().getExtendData();
        }
        checkHasAddress(mallOrderCreateDataVo);
        calculateOfMall(param, mallOrderCreateDataVo);

        // orderList：按 siteId 索引各站点子单，供 create 阶段 splitOrder 读取
        mallOrderCreateDataVo.setOrderList(new HashMap<>());
        Set<String> tempHasGoodsTypes = new HashSet<>();
        int tempVirtualVerifyMobile = 0;
        // 【循环】orderHandleList 各站点子单 → 写入 orderList 并汇总商品类型/虚拟核销手机号
        for (Map.Entry<String, OrderCreateDataVo> entryMap : mallOrderCreateDataVo.getOrderHandleList().entrySet()) {
            mallOrderCreateDataVo.getOrderList().put(entryMap.getValue().getSiteId(), entryMap.getValue());
            tempHasGoodsTypes.addAll(entryMap.getValue().getBasic().getHasGoodsTypes());
            if (ObjectUtil.isNotEmpty(entryMap.getValue().getBasic().getVirtualVerifyMobile()) && entryMap.getValue().getBasic().getVirtualVerifyMobile() > 0) {
                tempVirtualVerifyMobile = entryMap.getValue().getBasic().getVirtualVerifyMobile();
            }
        }
        mallOrderCreateDataVo.getBasic().setHasGoodsTypes(tempHasGoodsTypes);
        mallOrderCreateDataVo.getBasic().setVirtualVerifyMobile(tempVirtualVerifyMobile);
        mallOrderCreateDataVo.getBasic().setExtendData(extendData);

        cacheOrderVo(mallOrderCreateDataVo);
        return mallOrderCreateDataVo;
    }

    /**
     * 多站点订单创建：读 calculate 缓存 → 拆单 → 建订单组 → 逐子单落库 → 扣积分/平台优惠 → 后置事件。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallOrderCreateResultVo create(MallOrderCreateParam param) {
        MallOrderCreateDataVo mallOrderCreateDataVo = loadOrderVo(param.getOrderKey(), MallOrderCreateDataVo.class);
        if (mallOrderCreateDataVo.getOrderList() == null || mallOrderCreateDataVo.getOrderList().isEmpty()) {
            throw new CommonException("订单数据已失效，请重新结算");
        }
        coerceMallAggregateDiscountFromCache(mallOrderCreateDataVo.getDiscount());
        if (!mallOrderCreateDataVo.getError().isEmpty()){
            throw new CommonException(mallOrderCreateDataVo.getError().get(0));
        }

        Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                .select("nickname, point")
                .eq("member_id", param.getMemberId()));
        if (member == null) throw new CommonException("找不到买家");
        if (StatusEnum.OFF.getStatus().equals(member.getStatus())) throw new CommonException("账号被锁定");
        Addon supplyAddon = coreAddonService.getInfoByKey("supply");
        mallOrderCreateDataVo.setIsSupply(ObjectUtil.isNotEmpty(supplyAddon));
        if (!param.getDelivery().getTakerMobile().isEmpty()) {
            mallOrderCreateDataVo.getDelivery().setTakerMobile(param.getDelivery().getTakerMobile());
        }
        if (!param.getDelivery().getTakerName().isEmpty()) {
            mallOrderCreateDataVo.getDelivery().setTakerName(param.getDelivery().getTakerName());
        }
        if (mallOrderCreateDataVo.getBuyer() != null) {
            mallOrderCreateDataVo.getBuyer().setPoint(member.getPoint());
        }

        // 对齐 PHP：create 信任 calculate 缓存金额，不在此阶段重算满减/平台券/积分
        MallGroupMoneySnapshot groupSnapshot = MallGroupMoneySnapshot.capture(mallOrderCreateDataVo);
        Map<Integer, SubOrderMoneySnapshot> subSnapshots = captureSubOrderMoneySnapshots(mallOrderCreateDataVo);

        if (!mallOrderCreateDataVo.getIsSupply()) {
            // 无供货：各站点 discount 行项汇总为单条 point_deduct/full_reduction，防 useDiscount 重复落库
            for (OrderCreateDataVo v : mallOrderCreateDataVo.getOrderList().values()) {
                v.getDiscount().put("point_deduct", recalculateOrderDiscount("point_deduct", new ArrayList<>(), true, v));
                v.getDiscount().put("full_reduction", recalculateOrderDiscount("full_reduction", new ArrayList<>(), true, v));
                removeMergedDiscountLineKeys(v, "point_deduct");
                removeMergedDiscountLineKeys(v, "full_reduction");
            }
        }
        splitOrder(mallOrderCreateDataVo);
        if (mallOrderCreateDataVo.getOrderHandleList().isEmpty()) {
            throw new CommonException("订单数据已失效，请重新结算");
        }
        if (Boolean.TRUE.equals(mallOrderCreateDataVo.getIsSupply())) {
            for (OrderCreateDataVo handle : mallOrderCreateDataVo.getOrderHandleList().values()) {
                OrderCreateHelper.refreshBasicOrderMoneyStatic(handle);
            }
            refreshMallAggregateFromOrderHandles(mallOrderCreateDataVo);
        } else {
            groupSnapshot.applyTo(mallOrderCreateDataVo);
            for (OrderCreateDataVo handle : mallOrderCreateDataVo.getOrderHandleList().values()) {
                SubOrderMoneySnapshot snapshot = subSnapshots.get(handle.getSiteId());
                if (snapshot != null) {
                    snapshot.applyTo(handle);
                }
            }
        }
        createOrderGroup(mallOrderCreateDataVo);
        JSONObject formData = param.getFormData();

        // 【循环】orderHandleList：每条待落库子单 → 补 groupId/表单/发票/配送 → 调单站点 create
        for (Map.Entry<String, OrderCreateDataVo> entry : mallOrderCreateDataVo.getOrderHandleList().entrySet()) {
            entry.getValue().setGroupId(mallOrderCreateDataVo.getGroupId());
            if (ObjectUtil.isNotEmpty(formData)) {
                JSONObject siteFormData = formData.getJSONObject(entry.getValue().getSiteId().toString());
                if (ObjectUtil.isNotEmpty(siteFormData)) {

                    entry.getValue().setFormData(siteFormData);
                }
            }
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
            if (!Boolean.TRUE.equals(mallOrderCreateDataVo.getIsSupply())) {
                SubOrderMoneySnapshot snapshot = subSnapshots.get(entry.getValue().getSiteId());
                if (snapshot != null) {
                    snapshot.applyTo(entry.getValue());
                }
            }
            coreOrderCreateService.create(entry.getValue());
        }

        MallOrderCreateDataVo.MallDiscountVo pointDeductVo = mallOrderCreateDataVo.getDiscount().getOrDefault("point_deduct", new MallOrderCreateDataVo.MallDiscountVo());
        Integer pointDeductNum = pointDeductVo.getNum() != null ? pointDeductVo.getNum() : 0;
        corePointDeductService.joinPointDeduct(null,
                mallOrderCreateDataVo.getBuyerId(),
                pointDeductNum,
                String.valueOf(mallOrderCreateDataVo.getGroupId()));

        useMallDiscount(mallOrderCreateDataVo);

        if (mallOrderCreateDataVo.getBasic().getOrderMoney().compareTo(BigDecimal.ZERO) == 0) {
            OrderPayParam orderPayParam = new OrderPayParam();
            orderPayParam.setTradeId(mallOrderCreateDataVo.getGroupId());
            orderPayParam.setMainType(mallOrderCreateDataVo.getMainType());
            orderPayParam.setMainId(mallOrderCreateDataVo.getMainId());
            coreOrderPayService.payOfGroup(orderPayParam);
        }

        // 【循环】已落库子单：触发创建后置事件（对齐 PHP MallOrderCreate：秒杀/拼团/助力在此写 extend_field）
        for (OrderCreateDataVo createdOrder : mallOrderCreateDataVo.getOrderHandleList().values()) {
            coreOrderEventService.orderCreateAfter(createdOrder);
            OrderCreateAfterEvent afterEvent = new OrderCreateAfterEvent();
            afterEvent.setSiteId(createdOrder.getSiteId());
            afterEvent.setOrderData(createdOrder);
            if (createdOrder.getOrderId() != null && createdOrder.getOrderId() > 0) {
                MallOrder order = mallOrderMapper.selectById(createdOrder.getOrderId());
                afterEvent.setOrder(order);
            }
            applicationEventPublisher.publishEvent(afterEvent);
        }

        MallOrderCreateResultVo mallOrderCreateResultVo = new MallOrderCreateResultVo();
        mallOrderCreateResultVo.setTradeType("mall");
        mallOrderCreateResultVo.setTradeId(mallOrderCreateDataVo.getGroupId());
        removeOrderVo(param.getOrderKey(), mallOrderCreateDataVo);
        return mallOrderCreateResultVo;
    }

    /**
     * 查询店铺优惠券
     *
     * @return 优惠券列表
     */
    @Override
    public Map<Integer, List<OrderCouponListVo>> getCoupon(Map<Integer, Map<String, Object>> body) {
        // 按站点批量查询可用店铺优惠券，返回 siteId -> 券列表
        Map<Integer, List<OrderCouponListVo>> listMap = new HashMap<>();
        if (!body.isEmpty()) {
            // 【循环】body 各站点 → 有 order_key 则调单站点 getCoupon
            for (Map.Entry<Integer, Map<String, Object>> entry : body.entrySet()) {
                Map<String, Object> map = entry.getValue();
                String orderKey = map.containsKey("order_key") ? map.get("order_key").toString() : "";
                if (!orderKey.isEmpty()) {
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
        // 先 calculate 再按券类型/门槛/适用站点过滤，仅返回可用平台券
        MallOrderCreateDataVo mallOrderCreateDataVo = calculate(param);

        List<MallMemberCouponListVo> memberCouponList = coreCouponMemberService.getUsePlatformCouponListByMemberId(param.getMemberId(), "mall");
        List<MallMemberCouponListVo> couponList = new ArrayList<>();
        Set<String> orderHandleKeys = mallOrderCreateDataVo.getOrderHandleList().keySet();

        // 【循环】会员可用平台券 → 校验状态/门槛/适用站点后加入结果
        for (MallMemberCouponListVo couponVo : memberCouponList) {
            if (couponVo.getCoupon() == null || !CouponStatusEnum.NORMAL.getStatus().equals(couponVo.getCoupon().getStatus())) {
                continue; // 券不存在或非正常状态
            }

            Integer isAllSiteJoin = couponVo.getCoupon().getIsAllSiteJoin();
            JSONArray joinSiteIds = parseJoinSiteIds(couponVo.getCoupon().getJoinSiteIds());
            Map<String, OrderCreateDataVo> orderList = getOrderListBySiteIds(
                    CouponIsAllSiteEnum.ALL_SITE_JOIN.getType().equals(isAllSiteJoin) ? null : joinSiteIds,
                    mallOrderCreateDataVo
            );

            BigDecimal matchGoodsMoney = computePlatformCouponMatchMoney(couponVo, orderList);
            if (matchGoodsMoney.compareTo(couponVo.getMinConditionMoney()) < 0) {
                continue; // 未达最低消费门槛
            }
            if (matchGoodsMoney.compareTo(BigDecimal.ZERO) == 0) {
                continue; // 匹配商品金额为 0
            }
            if (CouponIsAllSiteEnum.NOT_ALL_SITE_JOIN.getType().equals(isAllSiteJoin)
                    && joinSiteIds != null && !joinSiteIds.isEmpty()
                    && !isJoinSiteIdsCoveredByOrder(joinSiteIds, orderHandleKeys)) {
                continue; // 指定站点未全部包含在当前订单
            }

            couponList.add(couponVo);
        }
        return couponList;
    }

    private JSONArray parseJoinSiteIds(Object joinSiteIds) {
        // 平台券适用站点 joinSiteIds 统一转为 JSONArray
        if (ObjectUtil.isEmpty(joinSiteIds)) {
            return new JSONArray(); // 空则返回空数组
        }
        if (joinSiteIds instanceof JSONArray arr) {
            return arr; // 已是 JSONArray 直接返回
        }
        return JSONUtil.parseArray(String.valueOf(joinSiteIds)); // 字符串 JSON 解析
    }

    private boolean isJoinSiteIdsCoveredByOrder(JSONArray joinSiteIds, Set<String> orderHandleKeys) {
        // 校验平台券指定站点是否都在当前订单 orderHandleList 内
        // 【循环】joinSiteIds 每个指定站点 → 任一不在 orderHandleKeys 则不可用
        for (Object siteId : joinSiteIds) {
            if (siteId != null && !orderHandleKeys.contains(String.valueOf(siteId))) {
                return false;
            }
        }
        return true;
    }

    private BigDecimal computePlatformCouponMatchMoney(MallMemberCouponListVo couponVo, Map<String, OrderCreateDataVo> orderList) {
        // 按券类型（全品/品类/品牌）计算平台券可匹配商品金额
        CouponTypeEnum typeEnum = CouponTypeEnum.getEnumByType(Integer.valueOf(couponVo.getType()));
        if (typeEnum == null) {
            return BigDecimal.ZERO; // 未知券类型
        }

        List<MallCouponGoods> goodsData = couponVo.getGoods() != null ? couponVo.getGoods() : Collections.emptyList();
        BigDecimal matchGoodsMoney = BigDecimal.ZERO;

        switch (typeEnum) {
            case ALL:
                // 【循环】orderList 各子单 → 累加商品金额减店铺优惠
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
                // 【外层循环】orderList 各子单 → 遍历 SKU 匹配商城品类
                for (OrderCreateDataVo order : orderList.values()) {
                    // 【内层循环】该子单 goodsData 每个 SKU → 品类命中则累加可匹配金额
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
                // 【外层循环】orderList 各子单 → 遍历 SKU 匹配品牌
                for (OrderCreateDataVo order : orderList.values()) {
                    // 【内层循环】该子单 goodsData 每个 SKU → 品牌命中则累加可匹配金额
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
     * 拆分订单：把 calculate 阶段的「按站点汇总单」转成 create 实际落库的「待写库子单列表」。
     */
    private void splitOrder(MallOrderCreateDataVo mallOrderCreateDataVo) {
        // orderHandleList = create 阶段逐条调用 coreOrderCreateService.create 的列表
        if (mallOrderCreateDataVo.getOrderHandleList() == null) {
            mallOrderCreateDataVo.setOrderHandleList(new HashMap<>());
        } else {
            mallOrderCreateDataVo.getOrderHandleList().clear();
        }
        // 【外层循环】orderList：key=siteId，value=该站点 calculate 后的整单
        // 作用：每个站点产出 1~N 条子单写入 orderHandleList（无供货 N=1，有供货按 supplyId 拆 N 条）
        for (Map.Entry<Integer, OrderCreateDataVo> entry : mallOrderCreateDataVo.getOrderList().entrySet()) {

            if (!mallOrderCreateDataVo.getIsSupply()) {
                // 未装供货插件：站点整单不拆，同步积分后原样放入 handleList
                OrderCreateDataVo orderVo = entry.getValue();
                syncPointDeductBasicFromDiscount(orderVo);
                mallOrderCreateDataVo.getOrderHandleList().put("shop" + entry.getKey().toString(), orderVo);
                continue;
            }

            // ---------- 以下：有供货插件，当前站点需按 supplyId 拆成多条子单 ----------
            OrderCreateDataVo siteOrder = entry.getValue();
            // 站点级公共字段模板，各供货批次 copy 后只改 supplyId/goodsData/basic 等差异部分
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
            // 把站点内所有 SKU 按 supplyId 分组：key=supplyId（0=店铺自营），value=该供货商下的 SKU 列表
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

            boolean isFirst = true; // 标记第一个供货批次，用于店铺券只核销一次
            boolean noSupplySiteBatch = orderGoods.containsKey(0); // 是否含店铺自营商品（supplyId=0）
            boolean fullGiftCopiedToSupplyBatch = false; // 满赠列表只挂到一个子单，避免重复落赠品
            List<String> tmpDiscountParams = new ArrayList<>(); // 收集本批 SKU 在 discount 中的行项 key
            // 【中层循环】orderGoods：key=supplyId，value=该供货商批次下的 SKU 列表
            // 作用：每个 supplyId 生成一条 supplyOrderCreateDataVo，作为一条独立 mall_order 落库
            for (Map.Entry<Integer, List<OrderCreateDataVo.Sku>> supplyEntry : orderGoods.entrySet()) {
                tmpDiscountParams.clear();
                // 基于站点模板复制，得到本供货批次的待落库子单
                OrderCreateDataVo supplyOrderCreateDataVo = new OrderCreateDataVo();
                BeanUtil.copyProperties(commonOrderCreateDataVo, supplyOrderCreateDataVo);
                supplyOrderCreateDataVo.setSupplyId(supplyEntry.getKey());
                supplyOrderCreateDataVo.setDiscount(copyDiscountMap(siteOrder.getDiscount()));

                // 店铺券全站只用一张：仅第一个供货批次 isCan=true 会触发核销，其余批次只落优惠记录
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
                // 从站点 goodsDiscount 中筛出本批 SKU 对应的行项优惠（满减/积分等明细）
                supplyOrderCreateDataVo.setGoodsDiscount(filterGoodsDiscountBySkus(siteOrder.getGoodsDiscount(), supplyEntry.getValue()));
                // 满赠 SKU/活动列表只复制到一个子单：混合单挂自营批(supplyId=0)，纯供货单挂第一个批次
                if (!fullGiftCopiedToSupplyBatch) {
                    boolean attachGiftToThisBatch = !noSupplySiteBatch || Integer.valueOf(0).equals(supplyEntry.getKey());
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
                // 【内层循环】本供货批次下的每个 SKU
                // 作用：累加子单 basic 金额字段，收集 discount 行项 key，组装 goodsData
                for (OrderCreateDataVo.Sku sku : supplyEntry.getValue()) {
                    String skuDiscountKey = sku.getSiteId() + "_" + sku.getGoodsId() + "_" + sku.getSkuId();
                    tmpDiscountParams.add(skuDiscountKey + ":point_deduct");
                    tmpDiscountParams.add(skuDiscountKey + ":full_reduction");

                    supplyOrderCreateDataVo.getBasic().setGoodsMoney(supplyOrderCreateDataVo.getBasic().getGoodsMoney().add(sku.getGoodsMoney()));
                    supplyOrderCreateDataVo.getBasic().setCostGoodsMoney(supplyOrderCreateDataVo.getBasic().getCostGoodsMoney().add(sku.getCostGoodsMoney()));
                    supplyOrderCreateDataVo.getBasic().setDiscountMoney(supplyOrderCreateDataVo.getBasic().getDiscountMoney().add(sku.getDiscountMoney()));
                    // mall_discount：优先 SKU.mallDiscountMoney；若未含积分抵扣则补上 Goods.pointDeductMoney
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
                // --- 本供货批次 SKU 累加完毕，汇总 discount / 运费 / 应付金额 ---
                supplyOrderCreateDataVo.setGoodsData(goodsData);
                if (supplyOrderCreateDataVo.getDiscount() == null) {
                    supplyOrderCreateDataVo.setDiscount(new HashMap<>());
                }
                // 从站点原 discount 中，按 tmpDiscountParams 指定的行项 key 汇总为本批单条 point_deduct / full_reduction
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
                // 本批子单写入 handleList：供货商单 key=supply_{id}，自营单 key=shop_{siteId}
                if (supplyOrderCreateDataVo.getSupplyId() != 0) {
                    mallOrderCreateDataVo.getOrderHandleList().put("supply_" + supplyOrderCreateDataVo.getSupplyId(), supplyOrderCreateDataVo);
                } else {
                    mallOrderCreateDataVo.getOrderHandleList().put("shop_" + supplyOrderCreateDataVo.getSiteId().toString(), supplyOrderCreateDataVo);
                }
            }

        }

    }

    /**
     * 聚合层 discount 从 JSON 缓存反序列化后可能为 Map/JSONObject，统一转为 MallDiscountVo。
     */
    private static void coerceMallAggregateDiscountFromCache(Map<String, MallOrderCreateDataVo.MallDiscountVo> map) {
        // 缓存反序列化后统一聚合 discount 为 MallDiscountVo，避免 Map/JSONObject 类型不一致
        if (map == null || map.isEmpty()) {
            return;
        }
        Map<String, MallOrderCreateDataVo.MallDiscountVo> out = new LinkedHashMap<>();
        // 【循环】discount map 各 key → 非 MallDiscountVo 则 JSON 转 Bean 后回写
        for (String key : new ArrayList<>(map.keySet())) {
            MallOrderCreateDataVo.MallDiscountVo raw = map.get(key);
            if (raw == null) {
                continue;
            }
            out.put(key, raw); // 已是 MallDiscountVo 直接保留
        }
        map.clear();
        map.putAll(out);
    }

    /**
     * 子单 discount Map 的 value 可能为 JSONObject，汇总路径需转为 Discount。
     */
    private static OrderCreateDataVo.Discount discountFromCacheValue(Object raw) {
        // 子单 discount Map 的 value 可能为 JSONObject，汇总路径需转为 Discount
        if (raw == null) {
            return null;
        }
        if (raw instanceof OrderCreateDataVo.Discount d) {
            return d; // 已是 Discount 直接返回
        }
        return JSONUtil.toBean(JSONUtil.parseObj(raw), OrderCreateDataVo.Discount.class);
    }

    /**
     * 汇总写入 discount[aggregateKey] 后，删除 Map 中其它 key 上同 discountType 的行项，避免重复落库。
     */
    private void removeMergedDiscountLineKeys(OrderCreateDataVo v, String aggregateKey) {
        // 汇总写入 discount[aggregateKey] 后，删除其它 key 上同 discountType 行项，避免 useDiscount 重复落库
        if (v == null || v.getDiscount() == null || !v.getDiscount().containsKey(aggregateKey)) {
            return;
        }
        Iterator<Map.Entry<String, OrderCreateDataVo.Discount>> it = v.getDiscount().entrySet().iterator();
        // 【循环】discount 除 aggregateKey 外各 entry → 同 discountType 行项删除
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
        // 按 params 指定行项或 isAll 全量，汇总 scopeOrder 上 point_deduct/full_reduction 的 num 与 money
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
            // 【循环】scopeOrder.discount 各 key → 收集同 discountType 行项
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
            // 【循环】params 指定行项 key → 累加 num/money
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
                // 【循环】scopeOrder 全部 point_deduct 行项 → 累加 num/money 与 title
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
                // 【循环】scopeOrder 全部 full_reduction 行项 → 累加 num/money
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
        return new OrderCreateDataVo.Discount(); // 无有效优惠金额返回空 Discount
    }

    /**
     * 订单分组
     */
    private void createOrderGroup(MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 写入 mall_order_group 主表并回填 groupId，供后续子单与平台优惠关联
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
    private void useMallDiscount(MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 平台优惠落库：平台券核销 + 写入 mall_order_group_discount
        if (!mallOrderCreateDataVo.getDiscount().isEmpty()) {
            // 【循环】平台聚合 discount 各类型 → 按类型处理并插入分组优惠记录
            for (Map.Entry<String, MallOrderCreateDataVo.MallDiscountVo> entry : mallOrderCreateDataVo.getDiscount().entrySet()) {
                String key = entry.getKey();
                MallOrderCreateDataVo.MallDiscountVo value = entry.getValue();

                // platform_coupon 核销会员券；point_deduct/full_reduction/full_gift 仅落分组记录
                switch (key) {
                    case "platform_coupon":
                        coreCouponMemberService.use(value.getDiscountTypeId(), mallOrderCreateDataVo.getGroupId());
                        break;
                    case "point_deduct":
                    case "full_reduction":
                    case "full_gift":
                    default:
                        break;
                }
                MallOrderGroupDiscount model = new MallOrderGroupDiscount();
                model.setGroupId(mallOrderCreateDataVo.getGroupId());
                model.setType(value.getType());
                model.setMoney(value.getMoney() != null ? value.getMoney() : BigDecimal.ZERO);
                model.setNum(value.getNum() != null ? value.getNum() : 0);
                model.setDiscountType(value.getDiscountType());
                model.setDiscountTypeId(value.getDiscountTypeId() != null ? value.getDiscountTypeId() : 0);
                model.setContent(value.getContent());
                model.setCreateTime(System.currentTimeMillis() / 1000);
                mallOrderGroupDiscountMapper.insert(model);

            }

            // TODO: 批量保存优惠数据
        }
    }

    /**
     * 选中收货地址
     */
    protected void selectTakeAddress(MallOrderCalculateParam param, MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 汇总各子单配送方式，加载默认或指定收货地址，并校验是否需要/有效地址
        Set<String> hasSelectDeliveryTypes = new HashSet<>();
        // 【循环】param.body 各站点 → 收集配送方式到 hasSelectDeliveryTypes
        for (Integer siteId : param.getBody().keySet()) {
            String itemDeliveryType = StringUtils.isNotEmpty(param.getBody().get(siteId).getDelivery().getDeliveryType()) ? param.getBody().get(siteId).getDelivery().getDeliveryType() : DeliveryTypeEnum.EXPRESS.getType();
            hasSelectDeliveryTypes.add(itemDeliveryType);
        }
        mallOrderCreateDataVo.getBasic().setHasSelectDeliveryTypes(hasSelectDeliveryTypes);

        //定义收货地址
        //查询默认收货地址
        MemberAddress address;
        if (ObjectUtil.isNotEmpty(param.getDelivery()) && ObjectUtil.isNotEmpty(param.getDelivery().getTakeAddressId()) && param.getDelivery().getTakeAddressId() > 0) {
            // 指定 takeAddressId：查该会员地址
            address = memberAddressMapper.selectOne(new QueryWrapper<MemberAddress>()
                    .eq("id", param.getDelivery().getTakeAddressId())
                    .eq("member_id", param.getMemberId()));
        } else {
            // 未指定：先查默认地址，再查首个地址
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
                // 同城配送需经纬度：优先取有 lng/lat 的地址
                address = memberAddressMapper.selectOne(new QueryWrapper<MemberAddress>()
                        .eq("member_id", param.getMemberId())
                        .and(item -> item.isNotNull("lng").ne("lng", ""))
                        .and(item -> item.isNotNull("lat").ne("lat", ""))
                        .orderByAsc("is_default", "id")
                        .last("LIMIT 1"));
            }
        }

        if (address == null) {
            mallOrderCreateDataVo.getDelivery().setTakeAddress(new JSONObject()); // 无可用地址置空
        } else {
            mallOrderCreateDataVo.getDelivery().setTakeAddress(address);
            mallOrderCreateDataVo.getDelivery().setTakerName(address.getName());
            mallOrderCreateDataVo.getDelivery().setTakerMobile(address.getMobile());
        }

        //填充默认收货地址的姓名和手机号给收货人姓名和收货人手机号
        if (param.getOrderFrom().equals(ChannelEnum.PC.getCode())) return; // PC 端不校验地址
        if (hasSelectDeliveryTypes.contains(DeliveryTypeEnum.LOCAL_DELIVERY.getType()) || hasSelectDeliveryTypes.contains(DeliveryTypeEnum.EXPRESS.getType())) {
            mallOrderCreateDataVo.getBasic().setIsNeedTakeAddress(1);
            if (ObjectUtil.isEmpty(mallOrderCreateDataVo.getDelivery().getTakeAddress())) {
                mallOrderCreateDataVo.getError().add("需要先选择收货地址");
            } else {
                if (hasSelectDeliveryTypes.contains(DeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                    JSONObject takeAddress = JSONUtil.parseObj(mallOrderCreateDataVo.getDelivery().getTakeAddress());
                    if (ObjectUtil.isEmpty(takeAddress.get("lng"))) {
                        mallOrderCreateDataVo.getError().add("所选收货地址没有使用地图精确定位"); // 同城配送需地图定位
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
     * @return 是否需要地址
     */
    private Boolean checkHasAddress(MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 判断是否含实物需地址，无实物清空地址，并同步收货人信息到各子单
        boolean er = false;
        // 【循环】orderHandleList 各子单 → 是否含实物商品
        for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {
            if (order.getBasic().getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
                er = true;
                break;
            }
        }
        if (!er) {
            mallOrderCreateDataVo.getDelivery().setTakeAddress(new JSONObject()); // 无实物不需要收货地址
        }

        // 【循环】orderHandleList 各子单 → 同步平台收货人姓名/手机
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
    private void mergeMallDataDiscountFromOrder(OrderCreateDataVo order, MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 子单 mall_data.discount 合并到平台聚合 discount
        if (order.getMallData() == null || !order.getMallData().containsKey("discount")) {
            return;
        }
        Object discountObj = order.getMallData().get("discount");
        if (!(discountObj instanceof Map<?, ?> rawMap)) {
            return;
        }
        // 【循环】子单 mall_data.discount 各 entry → 合并到平台聚合 discount
        for (Map.Entry<?, ?> en : rawMap.entrySet()) {
            String key = String.valueOf(en.getKey());
            Object val = en.getValue();
            if (val instanceof OrderCreateDataVo.Discount d) {
                mergeOneAggregateDiscount(key, d, mallOrderCreateDataVo);
            } else if ("full_gift".equals(key) && val instanceof List<?> list) {
                int i = 0;
                // 【循环】full_gift 列表 → 每条满赠单独合并（key 带序号）
                for (Object o : list) {
                    if (o instanceof OrderCreateDataVo.Discount d) {
                        mergeOneAggregateDiscount(key + "_" + (i++), d, mallOrderCreateDataVo);
                    }
                }
            }
        }
    }

    private void mergeOneAggregateDiscount(String key, OrderCreateDataVo.Discount d, MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 同 key 聚合优惠：已存在则 money/num/title 累加后覆盖
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
    }

    private static MallOrderCreateDataVo.MallDiscountVo discountToMallDiscountVo(OrderCreateDataVo.Discount d) {
        // 子单 Discount 字段映射为平台 MallDiscountVo，供聚合 discount 合并
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
    private void refreshMallBasicFromOrderList(MallOrderCreateDataVo mallOrderCreateDataVo) {
        // create 前按 orderList 汇总刷新平台 basic 金额，与 order_group 落库一致
        BigDecimal goodsMoney = BigDecimal.ZERO;
        BigDecimal deliveryMoney = BigDecimal.ZERO;
        BigDecimal shopDiscountMoney = BigDecimal.ZERO;
        BigDecimal orderMoney = BigDecimal.ZERO;
        BigDecimal fullReductionMoney = BigDecimal.ZERO;
        BigDecimal mallDiscountMoney = BigDecimal.ZERO;
        BigDecimal pointDeductMoney = BigDecimal.ZERO;
        int usePoint = 0;
        // 【循环】orderList 各站点子单 → 累加平台 basic 金额字段
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
    private void refreshMallAggregateFromOrderHandles(MallOrderCreateDataVo mallOrderCreateDataVo) {
        // split 后按 orderHandleList 刷新平台汇总金额（积分/平台券分摊后与子单一致）
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
        // 【循环】orderHandleList 各待落库子单 → 累加平台 basic 金额字段
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
        int skuPointNum = sumSkuUsePoint(vo);

        BigDecimal pointMoney = sumSkuPointDeductMoney(vo);
        int pointNum = skuPointNum;

        if (vo.getDiscount() != null && vo.getDiscount().containsKey("point_deduct")) {
            OrderCreateDataVo.Discount pointDiscount = discountFromCacheValue(vo.getDiscount().get("point_deduct"));
            if (pointDiscount != null && pointDiscount.getMoney() != null
                    && pointDiscount.getMoney().compareTo(BigDecimal.ZERO) > 0) {
                int aggPointNum = pointDiscount.getNum() != null ? pointDiscount.getNum() : 0;
                BigDecimal aggPointMoney = pointDiscount.getMoney();
                if (pointNum <= 0 && aggPointNum > 0) {
                    pointNum = aggPointNum;
                    pointMoney = aggPointMoney;
                    distributePointToSkus(vo, pointNum, pointMoney);
                } else if (aggPointNum > pointNum) {
                    distributePointToSkus(vo, aggPointNum, aggPointMoney);
                    pointNum = aggPointNum;
                    pointMoney = aggPointMoney;
                }
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

    private int sumSkuUsePoint(OrderCreateDataVo vo) {
        if (vo.getGoodsData() == null) {
            return 0;
        }
        int total = 0;
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            OrderCreateDataVo.Goods goods = sku.getGoods();
            int skuUsePoint = sku.getUsePoint() != null && sku.getUsePoint() > 0
                    ? sku.getUsePoint()
                    : (goods != null && goods.getUsePoint() != null ? goods.getUsePoint() : 0);
            total += skuUsePoint;
        }
        return total;
    }

    private BigDecimal sumSkuPointDeductMoney(OrderCreateDataVo vo) {
        if (vo.getGoodsData() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            OrderCreateDataVo.Goods goods = sku.getGoods();
            BigDecimal skuPointMoney = sku.getPointDeductMoney() != null && sku.getPointDeductMoney().compareTo(BigDecimal.ZERO) > 0
                    ? sku.getPointDeductMoney()
                    : (goods != null && goods.getPointDeductMoney() != null ? goods.getPointDeductMoney() : BigDecimal.ZERO);
            total = total.add(skuPointMoney);
        }
        return total;
    }

    /**
     * 多站点 create 阶段：discount 汇总有积分但 SKU 未分摊时，按行金额比例写入 SKU.use_point。
     */
    private void distributePointToSkus(OrderCreateDataVo vo, int targetPoint, BigDecimal targetMoney) {
        if (vo.getGoodsData() == null || vo.getGoodsData().isEmpty() || targetPoint <= 0) {
            return;
        }
        int currentPoint = sumSkuUsePoint(vo);
        if (currentPoint >= targetPoint) {
            return;
        }
        int deficit = targetPoint - currentPoint;
        BigDecimal currentMoney = sumSkuPointDeductMoney(vo);
        BigDecimal moneyDeficit = targetMoney.subtract(currentMoney).max(BigDecimal.ZERO);

        List<OrderCreateDataVo.Sku> allocationTargets = new ArrayList<>();
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            int skuUsePoint = sku.getUsePoint() != null ? sku.getUsePoint() : 0;
            if (skuUsePoint > 0) {
                continue;
            }
            BigDecimal weight = sku.getGoodsMoney()
                    .subtract(ObjectUtil.defaultIfNull(sku.getDiscountMoney(), BigDecimal.ZERO))
                    .subtract(ObjectUtil.defaultIfNull(sku.getMallCouponMoney(), BigDecimal.ZERO))
                    .max(BigDecimal.ZERO);
            allocationTargets.add(sku);
            totalWeight = totalWeight.add(weight);
        }
        if (allocationTargets.isEmpty()) {
            OrderCreateDataVo.Sku lastSku = null;
            for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                lastSku = sku;
            }
            if (lastSku != null) {
                applyPointShareToSku(lastSku, deficit, moneyDeficit);
            }
            return;
        }

        int allocatedPoint = 0;
        BigDecimal allocatedMoney = BigDecimal.ZERO;
        for (int i = 0; i < allocationTargets.size(); i++) {
            OrderCreateDataVo.Sku sku = allocationTargets.get(i);
            int pointShare;
            BigDecimal moneyShare;
            if (i == allocationTargets.size() - 1) {
                pointShare = deficit - allocatedPoint;
                moneyShare = moneyDeficit.subtract(allocatedMoney).max(BigDecimal.ZERO);
            } else {
                BigDecimal weight = sku.getGoodsMoney()
                        .subtract(ObjectUtil.defaultIfNull(sku.getDiscountMoney(), BigDecimal.ZERO))
                        .subtract(ObjectUtil.defaultIfNull(sku.getMallCouponMoney(), BigDecimal.ZERO))
                        .max(BigDecimal.ZERO);
                if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal ratio = weight.divide(totalWeight, 4, RoundingMode.HALF_UP);
                    pointShare = BigDecimal.valueOf(deficit).multiply(ratio).setScale(0, RoundingMode.DOWN).intValue();
                    moneyShare = moneyDeficit.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                } else {
                    pointShare = deficit / allocationTargets.size();
                    moneyShare = moneyDeficit.divide(BigDecimal.valueOf(allocationTargets.size()), 2, RoundingMode.HALF_UP);
                }
            }
            applyPointShareToSku(sku, pointShare, moneyShare);
            allocatedPoint += pointShare;
            allocatedMoney = allocatedMoney.add(moneyShare);
        }
    }

    private void applyPointShareToSku(OrderCreateDataVo.Sku sku, int pointShare, BigDecimal moneyShare) {
        if (sku == null || pointShare <= 0) {
            return;
        }
        int usePoint = (sku.getUsePoint() != null ? sku.getUsePoint() : 0) + pointShare;
        BigDecimal pointMoney = ObjectUtil.defaultIfNull(sku.getPointDeductMoney(), BigDecimal.ZERO).add(moneyShare);
        sku.setUsePoint(usePoint);
        sku.setPointDeductMoney(pointMoney);
        sku.setMallDiscountMoney(ObjectUtil.defaultIfNull(sku.getMallDiscountMoney(), BigDecimal.ZERO).add(moneyShare));
        if (sku.getGoods() != null) {
            sku.getGoods().setUsePoint(usePoint);
            sku.getGoods().setPointDeductMoney(pointMoney);
            sku.getGoods().setMallDiscountMoney(sku.getMallDiscountMoney());
        }
    }

    /**
     * 复制 discount Map，避免供货拆单各批次互相污染。
     */
    private Map<String, OrderCreateDataVo.Discount> copyDiscountMap(Map<String, OrderCreateDataVo.Discount> source) {
        // 深拷贝 discount Map，供货拆单各批次互不影响
        Map<String, OrderCreateDataVo.Discount> copy = new HashMap<>();
        if (source == null || source.isEmpty()) {
            return copy;
        }
        // 【循环】source 各 discount entry → discountFromCacheValue 后写入副本
        for (Map.Entry<String, OrderCreateDataVo.Discount> entry : source.entrySet()) {
            OrderCreateDataVo.Discount d = discountFromCacheValue(entry.getValue());
            if (d != null) {
                copy.put(entry.getKey(), d);
            }
        }
        return copy;
    }

    /**
     * 按供货批次 SKU 前缀过滤 goodsDiscount 行项优惠。
     */
    private Map<String, OrderCreateDataVo.Discount> filterGoodsDiscountBySkus(
            Map<String, OrderCreateDataVo.Discount> goodsDiscount, List<OrderCreateDataVo.Sku> skus) {
        // 按供货批次 SKU 前缀（siteId_goodsId_skuId）过滤 goodsDiscount 行项
        if (goodsDiscount == null || goodsDiscount.isEmpty() || skus == null || skus.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, OrderCreateDataVo.Discount> filtered = new HashMap<>();
        // 【外层循环】本批 SKU 列表 → 构造前缀匹配 goodsDiscount
        for (OrderCreateDataVo.Sku sku : skus) {
            String prefix = sku.getSiteId() + "_" + sku.getGoodsId() + "_" + sku.getSkuId();
            // 【内层循环】goodsDiscount 各 entry → key 前缀命中则保留
            for (Map.Entry<String, OrderCreateDataVo.Discount> entry : goodsDiscount.entrySet()) {
                if (entry.getKey().startsWith(prefix)) {
                    filtered.put(entry.getKey(), discountFromCacheValue(entry.getValue()));
                }
            }
        }
        return filtered;
    }

    /**
     * 统筹多站点：累加各子单金额 → 合并 mallData.discount → 算平台券与积分抵扣。
     */
    private void calculateOfMall(MallOrderCalculateParam param, MallOrderCreateDataVo mallOrderCreateDataVo) {
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
        // 清空上轮统筹残留的聚合优惠金额，避免 Bean 复用脏数据
        mallOrderCreateDataVo.getBasic().setFullReductionMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setMallCouponMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getDiscount().clear();

        // 【循环】orderHandleList 各站点子单 → 累加到 mall 聚合 basic，合并子单 mallData.discount
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
            // 累加子单满减金额到聚合 basic
            mallOrderCreateDataVo.getBasic().setFullReductionMoney(mallOrderCreateDataVo.getBasic().getFullReductionMoney()
                    .add(order.getBasic().getFullReductionMoney() != null ? order.getBasic().getFullReductionMoney() : BigDecimal.ZERO)
                    .setScale(2, RoundingMode.DOWN));

            mergeMallDataDiscountFromOrder(order, mallOrderCreateDataVo);

            // 任一子单禁用平台券则整单禁用
            if (order.getIsPlatCoupon() != null && order.getIsPlatCoupon() == 0) {
                mallOrderCreateDataVo.setIsPlatCoupon(0);
            }
        }
        calculateDiscount(param, mallOrderCreateDataVo);
    }

    /**
     * 计算折扣
     */
    private void calculateDiscount(MallOrderCalculateParam param, MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 平台级优惠计算入口：先平台券再积分抵扣
        calculateCoupon(param, mallOrderCreateDataVo);
        calculatePointDeduct(param, mallOrderCreateDataVo);
    }

    /**
     * 按平台券适用站点 ID 过滤子订单（joinSiteIds 与 siteId 统一字符串比较）。
     */
    private Map<String, OrderCreateDataVo> getOrderListBySiteIds(JSONArray siteIds, MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 按平台券适用站点 joinSiteIds 过滤 orderHandleList 子单
        if (ObjectUtil.isEmpty(siteIds)) {
            return new LinkedHashMap<>(mallOrderCreateDataVo.getOrderHandleList()); // siteIds 为空表示全站点适用
        }
        Map<String, OrderCreateDataVo> filteredList = new LinkedHashMap<>();
        // 【循环】orderHandleList 各子单 → 站点在 joinSiteIds 内则纳入
        for (Map.Entry<String, OrderCreateDataVo> e : mallOrderCreateDataVo.getOrderHandleList().entrySet()) {
            Integer sid = e.getValue().getSiteId();
            if (sid != null && jsonArrayContainsSiteId(siteIds, sid)) {
                filteredList.put(e.getKey(), e.getValue());
            }
        }
        return filteredList;
    }

    private static boolean jsonArrayContainsSiteId(JSONArray siteIds, Integer siteId) {
        // 判断 JSONArray 内是否包含指定 siteId（字符串比较）
        if (siteIds == null || siteId == null) {
            return false;
        }
        String s = String.valueOf(siteId);
        // 【循环】siteIds 逐个元素 → 与 siteId 字符串比较
        for (Object o : siteIds) {
            if (o != null && String.valueOf(o).equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算平台优惠金额
     *
     * @throws CommonException 如果优惠券不存在或不满足使用条件
     */
    private void calculateCoupon(MallOrderCalculateParam param, MallOrderCreateDataVo mallOrderCreateDataVo) throws CommonException {
        // 平台券计算：匹配商品金额、按子单/SKU 比例分摊、更新聚合与子单 basic/discount
        mallOrderCreateDataVo.getBasic().setMallDiscountMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getBasic().setMallCouponMoney(BigDecimal.ZERO);
        mallOrderCreateDataVo.getDiscount().remove("platform_coupon");
        MallOrderCalculateParam.MallDiscount mallDiscount = param.getMallDiscount();
        Integer platformCouponId = mallDiscount != null ? mallDiscount.getPlatformCouponId() : null;
        // -----------------结束-------------
        if (platformCouponId != null && platformCouponId > 0 && mallOrderCreateDataVo.getIsPlatCoupon() > 0) {
            // 查询会员平台券详情
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
                        CouponIsAllSiteEnum.ALL_SITE_JOIN.getType().equals(isAllSiteJoin) ? null : joinSiteIds,
                        mallOrderCreateDataVo
                );

                BigDecimal matchGoodsMoney = BigDecimal.ZERO;
                List<Integer> discountSiteIds = new ArrayList<>();
                boolean usable = false;
                CouponTypeEnum typeEnum = CouponTypeEnum.getEnumByType(Integer.valueOf(couponType));
                switch (typeEnum) {
                    case ALL:
                        // 全品类券：【循环】orderList 各子单累加可匹配商品金额
                        usable = true;
                        for (OrderCreateDataVo order : orderList.values()) {
                            discountSiteIds.add(order.getSiteId());
                            matchGoodsMoney = matchGoodsMoney.add(
                                    order.getBasic().getGoodsMoney().subtract(order.getBasic().getDiscountMoney())
                            ).setScale(2, RoundingMode.DOWN);
                        }
                        break;
                    case MALL_CATEGORY:
                        // 品类券：【外层循环】orderList → 【内层循环】SKU 匹配 mall_category
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
                                    ).setScale(2, RoundingMode.DOWN);
                                }
                            }
                        }
                        break;
                    case BRAND:
                        // 品牌券：【外层循环】orderList → 【内层循环】SKU 匹配 brand_id
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
                                    ).setScale(2, RoundingMode.DOWN);
                                }
                            }
                        }
                        break;
                }

                if (matchGoodsMoney.compareTo(BigDecimal.ZERO) <= 0) {
                    param.setMallDiscount(null);
                    if (usable) {
                        throw new CommonException("商品金额已减免至0元，不能继续使用平台优惠券"); // 有匹配但金额为 0
                    } else {
                        throw new CommonException("订单未满足当前优惠券的最低使用条件"); // 无可匹配商品
                    }
                }

                // 满足平台优惠券的使用门槛
                if (matchGoodsMoney.compareTo(minConditionMoney) >= 0 && usable) {
                    // 写入平台聚合 discount.platform_coupon
                    MallOrderCreateDataVo.MallDiscountVo mallDiscountVo = new MallOrderCreateDataVo.MallDiscountVo();
                    mallDiscountVo.setMatchGoodsIds(new ArrayList<>());
                    mallDiscountVo.setDiscountType("platform_coupon");
                    mallDiscountVo.setNum(1);
                    mallDiscountVo.setMoney(mallDiscountMoney);
                    mallDiscountVo.setType("discount");
                    mallDiscountVo.setDiscountTypeId(platformCouponId);
                    mallDiscountVo.setTitle(couponInfo.getTitle());
                    mallOrderCreateDataVo.getDiscount().put("platform_coupon", mallDiscountVo);

                    // 按子单商品金额比例分摊平台券
                    BigDecimal realMallDiscountGoodsMoney = BigDecimal.ZERO;
                    BigDecimal tempMallDiscountMoney = mallDiscountMoney;

                    // 【外层循环】orderHandleList 各子单 → 参与优惠的站点按比例分摊平台券
                    for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {
                        Integer siteId = order.getSiteId();
                        if (discountSiteIds.contains(siteId)) {
                            BigDecimal tempRealGoodsMoney = order.getBasic().getGoodsMoney()
                                    .subtract(order.getBasic().getDiscountMoney());
                            BigDecimal tempRate = tempRealGoodsMoney.divide(matchGoodsMoney, 4, RoundingMode.HALF_UP);
                            BigDecimal itemMallDiscountMoney = tempRate.multiply(mallDiscountMoney)
                                    .setScale(2, RoundingMode.HALF_UP);

                            if (itemMallDiscountMoney.compareTo(tempMallDiscountMoney) > 0) {
                                itemMallDiscountMoney = tempMallDiscountMoney; // 不超过剩余券额
                            }

                            if (itemMallDiscountMoney.compareTo(tempRealGoodsMoney) > 0) {
                                itemMallDiscountMoney = tempRealGoodsMoney; // 不超过子单可优惠商品金额
                            }

                            realMallDiscountGoodsMoney = realMallDiscountGoodsMoney.add(itemMallDiscountMoney)
                                    .setScale(2, RoundingMode.DOWN);
                            tempMallDiscountMoney = tempMallDiscountMoney.subtract(itemMallDiscountMoney)
                                    .setScale(2, RoundingMode.DOWN);

                            // 累加子单平台优惠并扣减 orderMoney
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

                            BigDecimal tempOrderGoodsMoney = order.getBasic().getGoodsMoney()
                                    .subtract(order.getBasic().getDiscountMoney());
                            BigDecimal tempSuprItemMallDiscountMoney = itemMallDiscountMoney;

                            // 【内层循环】该子单 goodsData 各 SKU → 按商品金额比例分摊平台券
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

                                    tempSuprItemMallDiscountMoney = tempSuprItemMallDiscountMoney.subtract(tempItemMallDiscountMoney)
                                            .setScale(2, RoundingMode.DOWN);
                                }

                                sku.setMallDiscountMoney(tempItemMallDiscountMoney);
                                sku.setMallCouponMoney(tempItemMallDiscountMoney);

                                // -----------------开始-----------------  order_goods_discount[platform_coupon]
                                String uniqueKey = sku.getSiteId() + "_" + sku.getGoodsId() + "_" + sku.getSkuId() + ":platform_coupon";
                                OrderCreateDataVo.Discount orderGoodsDiscount = new OrderCreateDataVo.Discount();
                                orderGoodsDiscount.setMatchGoodsIds(new Integer[]{sku.getSkuId()});
                                orderGoodsDiscount.setType("discount");
                                orderGoodsDiscount.setDiscountType("platform_coupon");
                                orderGoodsDiscount.setNum(0);
                                orderGoodsDiscount.setMoney(tempItemMallDiscountMoney);
                                orderGoodsDiscount.setOrderDiscountId(platformCouponId);
                                orderGoodsDiscount.setContent("平台优惠券抵扣");
                                orderGoodsDiscount.setTitle(couponInfo.getTitle());
                                order.getGoodsDiscount().put(uniqueKey, orderGoodsDiscount);
                                // -----------------结束-------------
                            }

                            // 写入子单 discount.platform_coupon 汇总项
                            OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                            discount.setType("discount");
                            discount.setDiscountType("platform_coupon");
                            discount.setMoney(itemMallDiscountMoney);
                            discount.setDiscountTypeId(platformCouponId);
                            discount.setContent("平台优惠券抵扣");
                            discount.setTitle(couponInfo.getTitle());
                            discount.setNum(1);
                            order.getDiscount().put("platform_coupon", discount);
                        }
                    }

                    mallOrderCreateDataVo.getBasic().setMallDiscountMoney(realMallDiscountGoodsMoney);
                    mallOrderCreateDataVo.getBasic().setMallCouponMoney(realMallDiscountGoodsMoney);

                    if (mallOrderCreateDataVo.getBasic().getMallDiscountMoney()
                            .compareTo(mallOrderCreateDataVo.getBasic().getOrderMoney()) > 0) {
                        // 平台优惠不超过订单应付金额
                        BigDecimal cap = mallOrderCreateDataVo.getBasic().getOrderMoney();
                        mallOrderCreateDataVo.getBasic().setMallDiscountMoney(cap);
                        mallOrderCreateDataVo.getBasic().setMallCouponMoney(cap);
                    }

                    mallOrderCreateDataVo.getBasic().setOrderMoney(
                            mallOrderCreateDataVo.getBasic().getOrderMoney()
                                    .subtract(mallOrderCreateDataVo.getBasic().getMallDiscountMoney())
                                    .setScale(2, RoundingMode.DOWN)
                    );
                    return;
                }
                // 券存在但不满足门槛或无可匹配商品
                throw new CommonException("优惠券不存在");
            } else {
                throw new CommonException("优惠券不存在"); // 券 ID 无效
            }
        }
    }

    /**
     * 解析商品实际积分抵扣比例；不可抵扣时返回 null，site 类型写回站点比例到商品对象
     */
    private BigDecimal resolvePointDeductRate(OrderCreateDataVo.Goods goods, PointDeduct sitePointDeductConfig) {
        // 解析商品积分抵扣比例：自定义或继承站点；不可抵扣时返回 null
        String pointDeductType = goods.getPointDeductType() != null ? goods.getPointDeductType() : GoodsDictEnum.SITE.getValue();
        BigDecimal pointDeductRate = goods.getPointDeductRate() != null ? goods.getPointDeductRate() : BigDecimal.ZERO;
        if (pointDeductType.equals(GoodsDictEnum.CUSTOM.getValue()) && pointDeductRate.compareTo(BigDecimal.ZERO) == 0) {
            return null; // 自定义比例且为 0 表示不参与抵扣
        }
        if (pointDeductType.equals(GoodsDictEnum.SITE.getValue())) {
            pointDeductRate = sitePointDeductConfig.getDeductRate() != null ? sitePointDeductConfig.getDeductRate() : BigDecimal.ZERO;
            goods.setPointDeductRate(pointDeductRate); // 继承站点比例写回商品
        }
        return pointDeductRate;
    }

    /**
     * 平台积分抵扣：对齐 PHP，预计算各站点可抵扣额 → 分摊到 SKU → 增量扣减 order_money。
     */
    private void calculatePointDeduct(MallOrderCalculateParam param, MallOrderCreateDataVo mallOrderCreateDataVo) {
        PointDeduct pointDeductConfig = corePointDeductService.getPointDeductConfig();
        Integer isOpen = pointDeductConfig.getIsOpen() != null ? pointDeductConfig.getIsOpen() : 0;
        mallOrderCreateDataVo.getBasic().setIsCanPointDeduct(isOpen);

        int isUsePoint = param.getIsUsePointDeduct() != null ? param.getIsUsePointDeduct() : 0;

        Integer orderMemberPoint = mallOrderCreateDataVo.getBuyer() != null
                && mallOrderCreateDataVo.getBuyer().getPoint() != null
                ? mallOrderCreateDataVo.getBuyer().getPoint() : 0;
        // 对齐 PHP：SKU 分摊时使用的会员积分余额在整单循环外初始化，跨站点递减
        int orderGoodsMemberPoint = orderMemberPoint;

        for (var orderHandle : mallOrderCreateDataVo.getOrderHandleList().values()) {
            if (orderHandle.getIsCanUsePoint() == 0) {
                continue;
            }
            var sitePointDeductConfig = corePointDeductService.getSitePointDeductConfig(orderHandle.getSiteId());

            OrderCreateDataVo.Basic basic = orderHandle.getBasic() != null ? orderHandle.getBasic() : new OrderCreateDataVo.Basic();
            orderHandle.setBasic(basic);

            BigDecimal allDeductGoodsMoney = BigDecimal.ZERO;
            for (Map.Entry<Integer, ? extends OrderCreateDataVo.Sku> entry : orderHandle.getGoodsData().entrySet()) {
                OrderCreateDataVo.Sku item = entry.getValue();
                OrderCreateDataVo.Goods goods = item.getGoods();

                BigDecimal pointDeductRate = resolvePointDeductRate(goods, sitePointDeductConfig);
                if (pointDeductRate == null) {
                    continue;
                }

                mallOrderCreateDataVo.getBasic().setIsCanPointDeduct(1);

                BigDecimal price = getPrice(item, pointDeductRate);
                allDeductGoodsMoney = allDeductGoodsMoney.add(price);
            }

            // 调用积分抵扣服务计算可抵扣信息
            DeductMoneyVo deductInfo = corePointDeductService.calculateDeductMoney(
                    orderMemberPoint, allDeductGoodsMoney, pointDeductConfig, null, null);
            // 累加预计抵扣金额（保留两位小数）
            mallOrderCreateDataVo.getBasic().setPrePointDeductMoney(
                    mallOrderCreateDataVo.getBasic().getPrePointDeductMoney()
                            .add(deductInfo.getDeductMoney()).setScale(2, RoundingMode.HALF_UP));
            // 累加预计使用积分（积分是整数，直接加）
            mallOrderCreateDataVo.getBasic().setPreUsePoint(
                    mallOrderCreateDataVo.getBasic().getPreUsePoint() + deductInfo.getUsePoint());
            // 扣减会员可用积分
            orderMemberPoint -= deductInfo.getUsePoint();

            // 判断用户是否使用积分抵扣
            if (isUsePoint == 1 && deductInfo.getDeductMoney().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal deductMoney = deductInfo.getDeductMoney().setScale(2, RoundingMode.HALF_UP);
                Integer usePoint = deductInfo.getUsePoint();

                // 更新子订单的基础金额（仅维护优惠字段，order_money 由 refreshBasicOrderMoney 统一重算）
                basic.setMallDiscountMoney(
                        ObjectUtil.defaultIfNull(basic.getMallDiscountMoney(), BigDecimal.ZERO).add(deductMoney));
                basic.setPointDeductMoney(deductMoney);
                basic.setUsePoint(usePoint);
                basic.setOrderMoney(
                        ObjectUtil.defaultIfNull(basic.getOrderMoney(), BigDecimal.ZERO)
                                .subtract(deductMoney).setScale(2, RoundingMode.HALF_UP).max(BigDecimal.ZERO));

                // 更新整体订单基础数据
                mallOrderCreateDataVo.getBasic().setUsePoint(mallOrderCreateDataVo.getBasic().getUsePoint() + usePoint);
                mallOrderCreateDataVo.getBasic().setPointDeductMoney(
                        mallOrderCreateDataVo.getBasic().getPointDeductMoney().add(deductMoney));
                mallOrderCreateDataVo.getBasic().setMallDiscountMoney(
                        mallOrderCreateDataVo.getBasic().getMallDiscountMoney().add(deductMoney));
                mallOrderCreateDataVo.getBasic().setOrderMoney(
                        mallOrderCreateDataVo.getBasic().getOrderMoney()
                                .subtract(deductMoney).setScale(2, RoundingMode.HALF_UP).max(BigDecimal.ZERO));

                // 商品积分分摊
                TreeMap<Integer, OrderCreateDataVo.Sku> goodsData = new TreeMap<>(orderHandle.getGoodsData());
                int maxUsePoint = usePoint;

                for (var item : goodsData.entrySet()) {
                    OrderCreateDataVo.Sku sku = item.getValue();
                    OrderCreateDataVo.Goods goods = sku.getGoods();
                    BigDecimal pointDeductRate = resolvePointDeductRate(goods, sitePointDeductConfig);
                    if (pointDeductRate == null) {
                        continue;
                    }

                    // 初始化商品的抵扣字段
                    sku.setUsePoint(0);
                    sku.setPointDeductMoney(BigDecimal.ZERO);
                    BigDecimal skuMallDiscount = ObjectUtil.defaultIfNull(sku.getMallDiscountMoney(), BigDecimal.ZERO);
                    goods.setUsePoint(0);
                    goods.setPointDeductMoney(BigDecimal.ZERO);
                    goods.setMallDiscountMoney(skuMallDiscount);

                    // 计算商品实际金额 = 商品金额 - 店铺优惠 - 平台优惠
                    BigDecimal discountMoney = sku.getDiscountMoney() != null ? sku.getDiscountMoney() : BigDecimal.ZERO;
                    BigDecimal datumGoodsMoney = sku.getGoodsMoney().subtract(discountMoney).subtract(skuMallDiscount);
                    BigDecimal goodsDeductableMoney = datumGoodsMoney
                            .multiply(pointDeductRate)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                    DeductMoneyVo tmpDeduct = corePointDeductService.calculateDeductMoney(
                            orderGoodsMemberPoint, goodsDeductableMoney, pointDeductConfig, true, maxUsePoint);

                    // 同步写入 SKU 级积分字段，供落单与关单退还读取
                    sku.setUsePoint(tmpDeduct.getUsePoint());
                    sku.setPointDeductMoney(tmpDeduct.getDeductMoney());
                    sku.setMallDiscountMoney(skuMallDiscount.add(tmpDeduct.getDeductMoney()));

                    // 更新商品抵扣数据
                    goods.setUsePoint(tmpDeduct.getUsePoint());
                    goods.setPointDeductMoney(tmpDeduct.getDeductMoney());
                    // 更新商品平台优惠金额
                    goods.setMallDiscountMoney(sku.getMallDiscountMoney());

                    orderGoodsMemberPoint -= tmpDeduct.getUsePoint();
                    maxUsePoint -= tmpDeduct.getUsePoint();

                    // 记录订单商品折扣明细
                    if (sku.getUsePoint() > 0) {
                        String uniqueKey = sku.getSiteId() + "_" + sku.getGoodsId() + "_" + sku.getSkuId() + ":point_deduct";

                        // 1. 记录订单商品折扣明细（order_goods_discount）
                        OrderCreateDataVo.Discount orderGoodsDiscount = new OrderCreateDataVo.Discount();
                        orderGoodsDiscount.setMatchGoodsIds(new Integer[]{sku.getSkuId()});
                        orderGoodsDiscount.setType("discount");
                        orderGoodsDiscount.setDiscountType("point_deduct");
                        orderGoodsDiscount.setNum(sku.getUsePoint());
                        orderGoodsDiscount.setMoney(tmpDeduct.getDeductMoney());
                        orderGoodsDiscount.setOrderDiscountId(0);
                        orderGoodsDiscount.setContent("参与积分抵扣");
                        orderGoodsDiscount.setTitle("积分抵扣");
                        orderHandle.getGoodsDiscount().put(uniqueKey, orderGoodsDiscount);

                        // 2. 记录订单整体折扣明细（discount）
                        OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                        discount.setMatchGoodsIds(new Integer[]{sku.getSkuId()});
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

                syncSubOrderPointBasicFromSkus(orderHandle, mallOrderCreateDataVo);
            }
        }

        if (mallOrderCreateDataVo.getBasic().getPrePointDeductMoney().compareTo(BigDecimal.ZERO) >= 0) {
            MallOrderCreateDataVo.MallDiscountVo mallDiscount = getMallDiscountVo(mallOrderCreateDataVo);
            mallOrderCreateDataVo.getDiscount().put("point_deduct", mallDiscount);

            mallOrderCreateDataVo.getBasic().setIsCanPointDeduct(1);
        }
        if (mallOrderCreateDataVo.getBasic().getPrePointDeductMoney().compareTo(BigDecimal.ZERO) <= 0) {
            mallOrderCreateDataVo.getBasic().setIsCanPointDeduct(0);
        }
    }

    private static MallOrderCreateDataVo.@NotNull MallDiscountVo getMallDiscountVo(MallOrderCreateDataVo mallOrderCreateDataVo) {
        MallOrderCreateDataVo.MallDiscountVo mallDiscount = new MallOrderCreateDataVo.MallDiscountVo();
        mallDiscount.setMatchGoodsIds(List.of(new Integer[]{}));
        mallDiscount.setType("discount");
        mallDiscount.setNum(mallOrderCreateDataVo.getBasic().getUsePoint());
        mallDiscount.setMoney(mallOrderCreateDataVo.getBasic().getPointDeductMoney());
        mallDiscount.setDiscountType("point_deduct");
        mallDiscount.setDiscountTypeId(0);
        mallDiscount.setContent("");
        mallDiscount.setTitle("积分抵扣");
        return mallDiscount;
    }

    private static @NotNull BigDecimal getPrice(OrderCreateDataVo.Sku item, BigDecimal pointDeductRate) {
        BigDecimal realGoodsMoney = item.getGoodsMoney()
                .subtract(item.getDiscountMoney() != null ? item.getDiscountMoney() : BigDecimal.ZERO)
                .subtract(item.getMallDiscountMoney() != null ? item.getMallDiscountMoney() : BigDecimal.ZERO);

        BigDecimal rateDiv100 = pointDeductRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return realGoodsMoney.multiply(rateDiv100).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * SKU 分摊完成后，将子单 basic 积分字段与 SKU 汇总对齐；若与站点级预扣不一致则同步修正聚合层。
     */
    private void syncSubOrderPointBasicFromSkus(OrderCreateDataVo orderHandle, MallOrderCreateDataVo mallOrderCreateDataVo) {
        if (orderHandle == null || orderHandle.getBasic() == null || orderHandle.getGoodsData() == null) {
            return;
        }
        BigDecimal skuPointMoney = BigDecimal.ZERO;
        int skuUsePoint = 0;
        for (OrderCreateDataVo.Sku sku : orderHandle.getGoodsData().values()) {
            if (sku.getPointDeductMoney() != null) {
                skuPointMoney = skuPointMoney.add(sku.getPointDeductMoney());
            }
            if (sku.getUsePoint() != null) {
                skuUsePoint += sku.getUsePoint();
            }
        }
        if (skuPointMoney.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        OrderCreateDataVo.Basic basic = orderHandle.getBasic();
        BigDecimal mallCoupon = ObjectUtil.defaultIfNull(basic.getMallCouponMoney(), BigDecimal.ZERO);
        BigDecimal oldPointMoney = ObjectUtil.defaultIfNull(basic.getPointDeductMoney(), BigDecimal.ZERO);
        int oldUsePoint = ObjectUtil.defaultIfNull(basic.getUsePoint(), 0);
        BigDecimal pointDelta = skuPointMoney.subtract(oldPointMoney);
        int usePointDelta = skuUsePoint - oldUsePoint;
        if (pointDelta.compareTo(BigDecimal.ZERO) == 0 && usePointDelta == 0) {
            return;
        }

        basic.setPointDeductMoney(skuPointMoney);
        basic.setUsePoint(skuUsePoint);
        basic.setMallDiscountMoney(mallCoupon.add(skuPointMoney));
        BigDecimal goodsMoney = ObjectUtil.defaultIfNull(basic.getGoodsMoney(), BigDecimal.ZERO);
        BigDecimal discountMoney = ObjectUtil.defaultIfNull(basic.getDiscountMoney(), BigDecimal.ZERO);
        BigDecimal deliveryMoney = ObjectUtil.defaultIfNull(basic.getDeliveryMoney(), BigDecimal.ZERO);
        basic.setOrderMoney(goodsMoney.add(deliveryMoney).subtract(discountMoney)
                .subtract(basic.getMallDiscountMoney()).setScale(2, RoundingMode.HALF_UP).max(BigDecimal.ZERO));

        if (mallOrderCreateDataVo != null && mallOrderCreateDataVo.getBasic() != null) {
            MallOrderCreateDataVo.Basic agg = mallOrderCreateDataVo.getBasic();
            agg.setPointDeductMoney(agg.getPointDeductMoney().add(pointDelta));
            agg.setMallDiscountMoney(agg.getMallDiscountMoney().add(pointDelta));
            agg.setOrderMoney(agg.getOrderMoney().subtract(pointDelta).setScale(2, RoundingMode.HALF_UP).max(BigDecimal.ZERO));
            agg.setUsePoint(agg.getUsePoint() + usePointDelta);
        }
    }

    /**
     * @param orderKey 缓存key
     * @return 订单数据
     */
    protected <T extends MallOrderCreateDataVo> T loadOrderVo(String orderKey, Class<T> clazz) {
        // 从会员订单缓存读取多站点聚合订单并反序列化为指定类型
        Cached cache = CacheFactory.getCacheOperator();
        Object data = cache.get(RequestUtils.memberId() + "_member_order_" + orderKey);
        if (data == null) throw new CommonException("订单数据已失效");

        JSONObject raw = JSONUtil.parseObj(data);
        T t = JSONUtil.toBean(raw, clazz);
        restoreMallOrderCacheStructure(t, raw);
        coerceMallAggregateDiscountFromCache(t.getDiscount());
        preserveMallOrderMoneyFromCacheSnapshot(t, raw);
        if (t.getOrderHandleList() == null) {
            t.setOrderHandleList(new HashMap<>()); // 缓存不含 orderHandleList，create 时从 orderList 重建
        }
        return t;
    }

    /**
     * 修复缓存反序列化：orderList / goodsData 的 Integer 键在 JSON 中为字符串，需手动还原。
     */
    private void restoreMallOrderCacheStructure(MallOrderCreateDataVo vo, JSONObject raw) {
        if (vo == null || raw == null) {
            return;
        }
        JSONObject orderListRaw = raw.getJSONObject("orderList");
        if (orderListRaw == null) {
            orderListRaw = raw.getJSONObject("order_list");
        }
        if (orderListRaw == null || orderListRaw.isEmpty()) {
            return;
        }
        Map<Integer, OrderCreateDataVo> orderList = new LinkedHashMap<>();
        for (String siteKey : orderListRaw.keySet()) {
            JSONObject siteJson = orderListRaw.getJSONObject(siteKey);
            if (siteJson == null) {
                continue;
            }
            OrderCreateDataVo siteOrder = JSONUtil.toBean(siteJson, OrderCreateDataVo.class);
            restoreOrderCreateDataVo(siteOrder, siteJson);
            orderList.put(Integer.valueOf(siteKey), siteOrder);
        }
        vo.setOrderList(orderList);
    }

    private void restoreOrderCreateDataVo(OrderCreateDataVo vo, JSONObject raw) {
        if (vo == null || raw == null) {
            return;
        }
        JSONObject goodsRaw = raw.getJSONObject("goodsData");
        if (goodsRaw == null) {
            goodsRaw = raw.getJSONObject("goods_data");
        }
        if (goodsRaw != null && !goodsRaw.isEmpty()) {
            Map<Integer, OrderCreateDataVo.Sku> goodsData = new LinkedHashMap<>();
            for (String skuKey : goodsRaw.keySet()) {
                JSONObject skuJson = goodsRaw.getJSONObject(skuKey);
                if (skuJson != null) {
                    goodsData.put(Integer.valueOf(skuKey), JSONUtil.toBean(skuJson, OrderCreateDataVo.Sku.class));
                }
            }
            vo.setGoodsData(goodsData);
        }
        restoreOrderDiscountMap(vo, raw, "discount", true);
        restoreOrderDiscountMap(vo, raw, "goodsDiscount", false);
    }

    private void restoreOrderDiscountMap(OrderCreateDataVo vo, JSONObject raw, String field, boolean isDiscount) {
        JSONObject discountRaw = raw.getJSONObject(field);
        if (discountRaw == null || discountRaw.isEmpty()) {
            return;
        }
        Map<String, OrderCreateDataVo.Discount> map = new LinkedHashMap<>();
        for (String key : discountRaw.keySet()) {
            OrderCreateDataVo.Discount d = discountFromCacheValue(discountRaw.get(key));
            if (d != null) {
                map.put(key, d);
            }
        }
        if (isDiscount) {
            vo.setDiscount(map);
        } else {
            vo.setGoodsDiscount(map);
        }
    }

    private void preserveMallOrderMoneyFromCacheSnapshot(MallOrderCreateDataVo vo, JSONObject raw) {
        if (vo == null || raw == null) {
            return;
        }
        JSONObject basicRaw = raw.getJSONObject("basic");
        if (basicRaw != null && vo.getBasic() != null) {
            applyCachedMallBasicMoneyFields(basicRaw, vo.getBasic());
        }
        JSONObject orderListRaw = raw.getJSONObject("orderList");
        if (orderListRaw == null) {
            orderListRaw = raw.getJSONObject("order_list");
        }
        if (orderListRaw == null || orderListRaw.isEmpty() || vo.getOrderList() == null) {
            return;
        }
        for (String siteKey : orderListRaw.keySet()) {
            JSONObject siteJson = orderListRaw.getJSONObject(siteKey);
            if (siteJson == null) {
                continue;
            }
            OrderCreateDataVo siteOrder = vo.getOrderList().get(Integer.valueOf(siteKey));
            if (siteOrder == null || siteOrder.getBasic() == null) {
                continue;
            }
            JSONObject siteBasicRaw = siteJson.getJSONObject("basic");
            if (siteBasicRaw != null) {
                applyCachedSubOrderBasicMoneyFields(siteBasicRaw, siteOrder.getBasic());
            }
        }
    }

    private void applyCachedMallBasicMoneyFields(JSONObject from, MallOrderCreateDataVo.Basic to) {
        to.setOrderMoney(getJsonBigDecimal(from, "orderMoney", "order_money", to.getOrderMoney()));
        to.setMallDiscountMoney(getJsonBigDecimal(from, "mallDiscountMoney", "mall_discount_money", to.getMallDiscountMoney()));
        to.setMallCouponMoney(getJsonBigDecimal(from, "mallCouponMoney", "mall_coupon_money", to.getMallCouponMoney()));
        to.setPointDeductMoney(getJsonBigDecimal(from, "pointDeductMoney", "point_deduct_money", to.getPointDeductMoney()));
        to.setGoodsMoney(getJsonBigDecimal(from, "goodsMoney", "goods_money", to.getGoodsMoney()));
        Integer usePoint = getJsonInteger(from, "usePoint", "use_point", to.getUsePoint());
        if (usePoint != null) {
            to.setUsePoint(usePoint);
        }
    }

    private void applyCachedSubOrderBasicMoneyFields(JSONObject from, OrderCreateDataVo.Basic to) {
        to.setOrderMoney(getJsonBigDecimal(from, "orderMoney", "order_money", to.getOrderMoney()));
        to.setMallDiscountMoney(getJsonBigDecimal(from, "mallDiscountMoney", "mall_discount_money", to.getMallDiscountMoney()));
        to.setMallCouponMoney(getJsonBigDecimal(from, "mallCouponMoney", "mall_coupon_money", to.getMallCouponMoney()));
        to.setPointDeductMoney(getJsonBigDecimal(from, "pointDeductMoney", "point_deduct_money", to.getPointDeductMoney()));
        to.setGoodsMoney(getJsonBigDecimal(from, "goodsMoney", "goods_money", to.getGoodsMoney()));
        Integer usePoint = getJsonInteger(from, "usePoint", "use_point", to.getUsePoint());
        if (usePoint != null) {
            to.setUsePoint(usePoint);
        }
    }

    private BigDecimal getJsonBigDecimal(JSONObject from, String camelKey, String snakeKey, BigDecimal fallback) {
        Object val = from.get(camelKey);
        if (val == null) {
            val = from.get(snakeKey);
        }
        if (val == null) {
            return fallback;
        }
        if (val instanceof BigDecimal) {
            return (BigDecimal) val;
        }
        try {
            return new BigDecimal(val.toString());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private Integer getJsonInteger(JSONObject from, String camelKey, String snakeKey, Integer fallback) {
        Object val = from.get(camelKey);
        if (val == null) {
            val = from.get(snakeKey);
        }
        if (val == null) {
            return fallback;
        }
        if (val instanceof Integer) {
            return (Integer) val;
        }
        try {
            return Integer.valueOf(val.toString());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private Map<Integer, SubOrderMoneySnapshot> captureSubOrderMoneySnapshots(MallOrderCreateDataVo vo) {
        Map<Integer, SubOrderMoneySnapshot> snapshots = new LinkedHashMap<>();
        if (vo.getOrderList() == null) {
            return snapshots;
        }
        for (Map.Entry<Integer, OrderCreateDataVo> entry : vo.getOrderList().entrySet()) {
            snapshots.put(entry.getKey(), SubOrderMoneySnapshot.capture(entry.getValue()));
        }
        return snapshots;
    }

    private static final class MallGroupMoneySnapshot {
        private final BigDecimal orderMoney;
        private final BigDecimal mallDiscountMoney;
        private final BigDecimal mallCouponMoney;
        private final BigDecimal pointDeductMoney;
        private final BigDecimal goodsMoney;
        private final Integer usePoint;

        private MallGroupMoneySnapshot(MallOrderCreateDataVo.Basic basic) {
            this.orderMoney = basic.getOrderMoney();
            this.mallDiscountMoney = basic.getMallDiscountMoney();
            this.mallCouponMoney = basic.getMallCouponMoney();
            this.pointDeductMoney = basic.getPointDeductMoney();
            this.goodsMoney = basic.getGoodsMoney();
            this.usePoint = basic.getUsePoint();
        }

        static MallGroupMoneySnapshot capture(MallOrderCreateDataVo vo) {
            return new MallGroupMoneySnapshot(vo.getBasic());
        }

        void applyTo(MallOrderCreateDataVo vo) {
            MallOrderCreateDataVo.Basic basic = vo.getBasic();
            basic.setOrderMoney(orderMoney);
            basic.setMallDiscountMoney(mallDiscountMoney);
            basic.setMallCouponMoney(mallCouponMoney);
            basic.setPointDeductMoney(pointDeductMoney);
            basic.setGoodsMoney(goodsMoney);
            basic.setUsePoint(usePoint);
        }
    }

    private static final class SubOrderMoneySnapshot {
        private final BigDecimal orderMoney;
        private final BigDecimal mallDiscountMoney;
        private final BigDecimal mallCouponMoney;
        private final BigDecimal pointDeductMoney;
        private final Integer usePoint;

        private SubOrderMoneySnapshot(OrderCreateDataVo.Basic basic) {
            this.orderMoney = basic.getOrderMoney();
            this.mallDiscountMoney = basic.getMallDiscountMoney();
            this.mallCouponMoney = basic.getMallCouponMoney();
            this.pointDeductMoney = basic.getPointDeductMoney();
            this.usePoint = basic.getUsePoint();
        }

        static SubOrderMoneySnapshot capture(OrderCreateDataVo vo) {
            return new SubOrderMoneySnapshot(vo.getBasic());
        }

        void applyTo(OrderCreateDataVo vo) {
            OrderCreateDataVo.Basic basic = vo.getBasic();
            basic.setOrderMoney(orderMoney);
            basic.setMallDiscountMoney(mallDiscountMoney);
            basic.setMallCouponMoney(mallCouponMoney);
            basic.setPointDeductMoney(pointDeductMoney);
            basic.setUsePoint(usePoint);
        }
    }

    /**
     * 获取订单数据
     *
     * @param orderKey 订单
     * @return 缓存数据
     */
    protected JSONObject loadOrderData(String orderKey) {
        // 从会员订单缓存读取平台订单 JSON 原始数据
        Cached cache = CacheFactory.getCacheOperator();
        Object data = cache.get(RequestUtils.memberId() + "_member_order_" + orderKey);
        if (data == null) throw new CommonException("订单数据已失效");

        return JSONUtil.parseObj(data);
    }

    /**
     * 移除订单数据缓存（含各子单 order_key 与 _basic 快照）
     *
     * @param orderKey 订单key
     */
    protected void removeOrderVo(String orderKey, MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 清除平台订单缓存及各子单 order_key、_basic 快照
        Cached cache = CacheFactory.getCacheOperator();
        String memberPrefix = RequestUtils.memberId() + "_member_order_";
        if (mallOrderCreateDataVo != null) {
            Set<String> siteOrderKeys = new LinkedHashSet<>();
            // 【循环】orderList 各子单 → 收集 orderKey
            if (mallOrderCreateDataVo.getOrderList() != null) {
                for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderList().values()) {
                    if (order != null && StringUtils.isNotEmpty(order.getOrderKey())) {
                        siteOrderKeys.add(order.getOrderKey());
                    }
                }
            }
            // 【循环】orderHandleList 各子单 → 收集 orderKey
            if (mallOrderCreateDataVo.getOrderHandleList() != null) {
                for (OrderCreateDataVo order : mallOrderCreateDataVo.getOrderHandleList().values()) {
                    if (order != null && StringUtils.isNotEmpty(order.getOrderKey())) {
                        siteOrderKeys.add(order.getOrderKey());
                    }
                }
            }
            // 【循环】各子单 orderKey → 删除子单缓存及 _basic 快照
            for (String siteOrderKey : siteOrderKeys) {
                cache.remove(memberPrefix + siteOrderKey);
                cache.remove(memberPrefix + siteOrderKey + "_basic");
            }
        }
        cache.remove(memberPrefix + orderKey); // 删除平台聚合缓存
    }

    /**
     * 缓存订单数据
     */
    protected void cacheOrderVo(MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 写入平台订单快照到会员缓存（不含 orderHandleList，子单各自缓存）
        if (ObjectUtil.isEmpty(mallOrderCreateDataVo.getOrderKey())) mallOrderCreateDataVo.setOrderKey(createOrderNo());
        Cached cache = CacheFactory.getCacheOperator();
        JSONObject jo = JSONUtil.parseObj(mallOrderCreateDataVo);
        jo.remove("orderHandleList");
        cache.put(RequestUtils.memberId() + "_member_order_" + mallOrderCreateDataVo.getOrderKey(), jo.toString());
    }

    /**
     * 创建交易号
     *
     * @return 交易号
     */
    protected String createOrderNo() {
        // 生成平台订单缓存 key：yyyyMMdd + 雪花 ID
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }


    /**
     * 配置设置或查询
     *
     * @param key 字符串
     */
    public void getConfig(String key, MallOrderCreateDataVo mallOrderCreateDataVo) {
        // 按 key 加载平台级配置写入 mallOrderCreateDataVo.config
        switch (key) {
            case "order":
                // 关单配置
                OrderConfigVo orderConfig = coreOrderConfigService.getConfig();
                MallOrderCreateDataVo.CloseOrder closeOrder = new MallOrderCreateDataVo.CloseOrder();
                closeOrder.setIsClose(orderConfig.getCloseOrderInfo().getIsClose());
                closeOrder.setCloseLength(orderConfig.getCloseOrderInfo().getCloseLength());
                mallOrderCreateDataVo.getConfig().setOrder(closeOrder);
                break;
            case "delivery":
                // 配送配置
                AdminDeliveryConfigVo deliveryObj = coreDeliveryConfigService.getAdminDeliveryConfig();
                MallOrderCreateDataVo.Delivery delivery = new MallOrderCreateDataVo.Delivery();
                BeanUtils.copyProperties(deliveryObj, delivery);
                mallOrderCreateDataVo.getConfig().setDelivery(delivery);
                break;
        }
    }
}
