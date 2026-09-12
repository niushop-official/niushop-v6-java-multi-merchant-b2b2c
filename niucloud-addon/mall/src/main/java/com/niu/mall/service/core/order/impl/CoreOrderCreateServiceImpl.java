package com.niu.mall.service.core.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.enums.shop.ShopStatusEnum;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.mall.constant.MallConstant;
import com.niu.mall.entity.cart.MallCart;
import com.niu.core.entity.coupon.MallCouponGoods;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGiftGoods;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.core.enums.coupon.CouponStatusEnum;
import com.niu.core.enums.coupon.CouponTypeEnum;
import com.niu.core.enums.goods.GoodsAuditStatusEnum;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.enums.trade.SettlementEnum;
import com.niu.mall.event.marketing.GoodsMarketCalculateEventDefiner;
import com.niu.mall.event.order.CheckOrderOfCreateEventDefiner;
import com.niu.mall.mapper.cart.MallCartMapper;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.api.goods.IGoodsService;
import com.niu.mall.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.mall.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.core.service.core.marketing.ICoreCouponMemberService;
import com.niu.core.service.core.marketing.vo.MallMemberCouponListVo;
import com.niu.mall.service.core.order.ICoreOrderCreateService;
import com.niu.mall.service.core.order.ICoreSiteOrderConfigService;
import com.niu.mall.service.core.shop.ICoreShopService;
import com.niu.mall.service.core.shop.vo.ShopInfoOfOrderCreateVo;
import com.niu.mall.service.core.order.helper.OrderCreateHelper;
import com.niu.mall.service.core.order.helper.OrderPointDeductHelper;
import com.niu.mall.service.core.order.param.OrderCalculateParam;
import com.niu.mall.service.core.order.param.OrderHelperParam;
import com.niu.mall.service.core.order.vo.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoreOrderCreateServiceImpl extends OrderCreateHelper implements ICoreOrderCreateService {

    @Resource
    MemberMapper memberMapper;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    MallCartMapper cartMapper;

    @Resource
    IGoodsService goodsService;

    @Resource
    ICoreGoodsLimitBuyService coreGoodsLimitBuyService;

    @Resource
    ICoreDiyFormRecordsService coreDiyFormRecordsService;

    @Resource
    ICoreSiteOrderConfigService coreOrderConfigService;

    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    ICoreShopService coreShopService;

    @Resource
    ICoreCouponMemberService coreCouponMemberService;

    @Resource
    ICoreGoodsActivePriceService coreGoodsActivePriceService;

    /**
     * 缓存加载后补全优惠券开关默认值（JSON 缺字段时为 null，默认开启）
     * 仅对 null 补默认，不覆盖已存在的 0。
     */
    private void ensureCouponFlags(OrderCreateDataVo vo) {
        // 缓存 JSON 反序列化后 isPlatCoupon/isShopCoupon 可能为 null，null 才补 1，已有 0 不覆盖
        if (vo.getIsPlatCoupon() == null) {
            vo.setIsPlatCoupon(1);
        }
        if (vo.getIsShopCoupon() == null) {
            vo.setIsShopCoupon(1);
        }
    }

    /**
     * 缓存反序列化后补全满减/满赠开关默认值（JSON 缺字段时为 null，会导致满减被跳过）
     * 仅对 null 补默认，不覆盖已存在的 0。
     */
    private void ensureFullActiveFlags(OrderCreateDataVo vo) {
        // 缓存 JSON 反序列化后 isJoinFullReduction/isJoinFullGift 可能为 null，null 才补 1，已有 0 不覆盖
        if (vo.getIsJoinFullReduction() == null) {
            vo.setIsJoinFullReduction(1);
        }
        if (vo.getIsJoinFullGift() == null) {
            vo.setIsJoinFullGift(1);
        }
    }

    /**
     * 缓存反序列化后补全积分抵扣开关默认值；仅对 null 补默认，不覆盖已存在的 0。
     */
    private void ensureCanUsePointFlag(OrderCreateDataVo vo) {
        // 缓存 JSON 反序列化后 isCanUsePoint 可能为 null，null 才补 1，已有 0 不覆盖
        if (vo.getIsCanUsePoint() == null) {
            vo.setIsCanUsePoint(1);
        }
    }

    /**
     * 订单类型解析：优先请求参数，其次 vo.orderType，最后 basic.orderType。
     */
    private void resolveOrderType(OrderCreateDataVo vo, String orderTypeFromParam) {
        // 【分支】orderTypeFromParam 非空：请求显式指定，写入 vo.orderType
        if (StringUtils.isNotEmpty(orderTypeFromParam)) {
            vo.setOrderType(orderTypeFromParam);
        // 【分支】vo.orderType 为空且 basic.orderType 有值：从 _basic 快照回填
        } else if (StringUtils.isEmpty(vo.getOrderType())
                && vo.getBasic() != null
                && StringUtils.isNotEmpty(vo.getBasic().getOrderType())) {
            vo.setOrderType(vo.getBasic().getOrderType());
        }
    }

    /**
     * 二次 calculate 从 _basic 缓存加载时，按 orderType 重放营销活动开关（与 GoodsMarketCalculateEvent 一致）。
     */
    private void ensureMarketingFlagsByOrderType(OrderCreateDataVo vo, String orderType) {
        // 【分支】orderType=seckill：活动单关闭平台券/店铺券/积分/满减/满赠
        if ("seckill".equals(orderType)) {
            vo.setIsPlatCoupon(0);
            vo.setIsShopCoupon(0);
            vo.setIsCanUsePoint(0);
            vo.setIsJoinFullReduction(0);
            vo.setIsJoinFullGift(0);
        // 【分支】orderType=friend_help：助力单同秒杀，全部营销关闭
        } else if ("friend_help".equals(orderType)) {
            vo.setIsPlatCoupon(0);
            vo.setIsShopCoupon(0);
            vo.setIsCanUsePoint(0);
            vo.setIsJoinFullReduction(0);
            vo.setIsJoinFullGift(0);
        // 【分支】orderType=pintuan：拼团单仅保留积分抵扣，其余营销关闭
        } else if ("pintuan".equals(orderType)) {
            vo.setIsPlatCoupon(0);
            vo.setIsShopCoupon(0);
            vo.setIsCanUsePoint(1);
            vo.setIsJoinFullReduction(0);
            vo.setIsJoinFullGift(0);
        }
    }

    /**
     * 单站点订单计算：加载/确认订单数据 → 算优惠满赠 → 算运费 → 重算应付金额 → 写缓存。
     */
    public OrderCreateDataVo calculate(OrderCalculateParam param, MallOrderCreateDataVo mallOrderCreateDataVo) {
        OrderCreateDataVo vo;
        // 【分支】按 orderKey 决定数据来源：无 key 首次 confirm；有 key 读 _basic 快照续算
        if (StringUtils.isEmpty(param.getOrderKey())) {
            vo = this.confirm(param);
            vo.setOrderFrom(param.getOrderFrom());
            this.cacheOrderVo(vo);
            this.cacheOrderVoBasic(vo);
        } else {
            try {
                vo = this.loadOrderVo(param.getOrderKey() + "_basic", OrderCreateDataVo.class);
                vo.setOrderFrom(param.getOrderFrom());
                vo.setOrderKey(param.getOrderKey());
                resolveOrderType(vo, param.getOrderType());
                ensureMarketingFlagsByOrderType(vo, vo.getOrderType());
            } catch (CommonException e) {
                if ("订单数据已失效".equals(e.getMessage())) {
                    param.setOrderKey("");
                    vo = this.confirm(param);
                    vo.setOrderFrom(param.getOrderFrom());
                    this.cacheOrderVo(vo);
                    this.cacheOrderVoBasic(vo);
                } else {
                    throw e;
                }
            }
        }
        // 缓存反序列化后补全营销开关默认值（null 才补，不覆盖 0）
        ensureCouponFlags(vo);
        ensureFullActiveFlags(vo);
        ensureCanUsePointFlag(vo);

        calculateDiscount(vo, param);
        calculateFullGift(vo, param);
        this.cacheOrderVo(vo);

        // 正式下单才算运费；购物车预览（orderType 为空）跳过
        if (StringUtils.isNotEmpty(param.getOrderType())) {
            calculateDelivery(vo, param, mallOrderCreateDataVo);
        }

        checkGoodsLimitBuySoft(vo);

        // 子单 calculate 不含平台 mall_discount：order_money = goods - discount + delivery
        refreshShopOrderMoney(vo);
        OrderCreateDataVo.Basic basic = vo.getBasic();
        basic.setPointDeductMoney(moneyFormat(basic.getPointDeductMoney()));
        basic.setDiscountMoney(moneyFormat(basic.getDiscountMoney()));
        basic.setDeliveryMoney(moneyFormat(basic.getDeliveryMoney()));
        basic.setGoodsMoney(moneyFormat(basic.getGoodsMoney()));
        basic.setFullReductionMoney(moneyFormat(basic.getFullReductionMoney()));
        cacheOrderVo(vo);
        vo.setMemberRemark(param.getMemberRemark());
        return vo;
    }

    @Override
    public void recheckFullActiveBeforeCreate(OrderCreateDataVo vo) {
        // 委托父类按当前时间重算 vo.basic 满减/满赠，create 前校验活动仍有效
        super.recheckFullActiveBeforeCreate(vo);
    }

    /**
     * 单站点订单创建：校验 → 组装 MallOrder/MallOrderGoods → 调用 createOrder 落库。
     */
    @Override
    public OrderCreateResultVo create(OrderCreateDataVo vo) {
        if (!vo.getError().isEmpty()) throw new CommonException(vo.getError().get(0));
        checkGoodsLimitBuyHard(vo);
        // 多站点订单组子单：对齐 PHP，使用 calculate 缓存金额，不在 create 再次重算满减/平台优惠
        boolean isMallGroupSubOrder = vo.getGroupId() != null && vo.getGroupId() > 0;
        if (!isMallGroupSubOrder) {
            recheckFullActiveBeforeCreate(vo);
        }
        checkSite(vo);
        // 扩展点：供货价/拼团等创建前校验
        CheckOrderOfCreateEventDefiner.CheckOrderOfCreateEvent event = new CheckOrderOfCreateEventDefiner.CheckOrderOfCreateEvent();
        event.setSiteId(RequestUtils.siteId());
        event.setVo(vo);
        List<CheckOrderOfCreateEventDefiner.CheckOrderOfCreateResult> resultList = EventAndSubscribeOfPublisher.publishAndCallback(event);
        if (!resultList.isEmpty()) {
            for (CheckOrderOfCreateEventDefiner.CheckOrderOfCreateResult item : resultList) {
                if (item == null) continue;
                if (ObjectUtil.isNotEmpty(item.getError())) {
                    throw new CommonException(item.getError().get(0));
                }
            }
        }

        boolean isSeckillOrder = "seckill".equals(vo.getOrderType());
        // 【循环】goodsData 每个 SKU：校验开售状态与库存（秒杀单库存由秒杀模块校验）
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().select("stock,is_sell").eq("sku_id", sku.getSkuId()));
            if (!(ObjectUtil.isNotEmpty(goodsSku.getIsSell()) && goodsSku.getIsSell().equals(1))) {
                throw new CommonException("商品" + sku.getGoods().getGoodsName() + " " + sku.getSkuName() + "暂未开售");
            }
            if (!isSeckillOrder && goodsSku.getStock().compareTo(sku.getNum()) < 0) {
                throw new CommonException("商品" + sku.getGoods().getGoodsName() + " " + sku.getSkuName() + "库存不足");
            }
        }

        // 实物或需核销手机号的虚拟商品，落库时需收货人手机号
        int needTakerMobile = 0;
        if ((ObjectUtil.isNotEmpty(vo.getBasic().getVirtualVerifyMobile()) && vo.getBasic().getVirtualVerifyMobile().equals(1)) ||
                (ObjectUtil.isNotEmpty(vo.getBasic().getHasGoodsTypes()) && vo.getBasic().getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType()))) {
            needTakerMobile = 1;
        }

        if (!isMallGroupSubOrder) {
            refreshBasicOrderMoney(vo);
            assertOrderMoneyConsistency(vo);
        }

        // --- 组装 mall_order 主表 ---
        MallOrder order = new MallOrder();
        order.setOrderNo(createOrderNo());
        order.setGroupId(vo.getGroupId());
        order.setSiteId(vo.getSiteId());
        order.setSiteName(vo.getSiteInfo().getSiteName());
        order.setOrderFrom(vo.getOrderFrom());
        order.setIp(RequestUtils.ip());
        order.setOrderType(vo.getOrderType());
        order.setStatus(OrderStatusEnum.WAIT_PAY.getStatus().toString());
        order.setSettlementStatus(SettlementEnum.WAIT.getStatus());
        order.setBody(vo.getBasic().getBody());
        order.setMemberId(vo.getMemberId());
        order.setGoodsMoney(vo.getBasic().getGoodsMoney());
        order.setCostGoodsMoney(vo.getBasic().getCostGoodsMoney());

        order.setDeliveryMoney(vo.getBasic().getDeliveryMoney());
        order.setDiscountMoney(vo.getBasic().getDiscountMoney());
        order.setMallDiscountMoney(vo.getBasic().getMallDiscountMoney());
        order.setMallCouponMoney(vo.getBasic().getMallCouponMoney());
        order.setOrderMoney(vo.getBasic().getOrderMoney());
        order.setHasGoodsTypes(JSONUtil.parseArray(vo.getBasic().getHasGoodsTypes()).toString());
        order.setDeliveryType(vo.getDelivery().getDeliveryType());
        if (StringUtils.isNotEmpty(vo.getDelivery().getSubDeliveryType())) {
            order.setSubDeliveryType(vo.getDelivery().getSubDeliveryType());
        }
        order.setMemberRemark(vo.getMemberRemark());
        order.setSupplyId(vo.getSupplyId());
        order.setIsSyncSupply(vo.getSupplyId() > 0 ? 2 : 1);
        order.setSupplyDeliveryMoney(vo.getSupplyId() > 0 ? vo.getBasic().getDeliveryMoney() : BigDecimal.ZERO);
        order.setFullReductionMoney(vo.getBasic().getFullReductionMoney());
        order.setUsePoint(BigDecimal.valueOf(vo.getBasic().getUsePoint() != null ? vo.getBasic().getUsePoint() : 0));
        order.setPointDeductMoney(vo.getBasic().getPointDeductMoney() != null
                ? vo.getBasic().getPointDeductMoney() : BigDecimal.ZERO);

        if (ObjectUtil.isNotEmpty(vo.getDelivery().getTakeAddress())) {
            MemberAddress address = JSONUtil.toBean(JSONUtil.parseObj(vo.getDelivery().getTakeAddress()), MemberAddress.class);
            order.setTakerName(address.getName());
            order.setTakerMobile(needTakerMobile > 0 ? address.getMobile() : "");
            order.setTakerProvince(address.getProvinceId());
            if (ObjectUtil.isNotEmpty(vo.getLocalDeliveryType()) && vo.getLocalDeliveryType().equals("now")) {
                order.setBuyerAskDeliveryTime(vo.getBuyerAskDeliveryTime());
            } else {
                order.setBuyerAskDeliveryTime(StringUtils.isNotEmpty(vo.getBuyerAskDeliveryTime()) ? vo.getBuyerAskDeliveryTime() : "");
            }
            order.setTakerCity(address.getCityId());
            order.setTakerDistrict(address.getDistrictId());
            order.setTakerAddress(address.getAddress());
            order.setTakerFullAddress(address.getFullAddress());
            order.setTakerLatitude(address.getLat());
            order.setTakerLongitude(address.getLng());
        }
        if (ObjectUtil.isNotEmpty(vo.getDelivery().getTakeStore()) && !vo.getDelivery().getTakeStore().toString().equals("[]")) {
            OrderCreateDataVo.TakeStore takeStore = JSONUtil.toBean(JSONUtil.parseObj(vo.getDelivery().getTakeStore()), OrderCreateDataVo.TakeStore.class);
            order.setTakeStoreId(takeStore.getStoreId());
        }
        order.setExtendData(JSONUtil.toJsonStr(vo.getExtendData()));
        order.setCreateTime(System.currentTimeMillis() / 1000);
        order.setWeappVersion(vo.getWeappVersion());
        if (vo.getSiteInfo() != null && vo.getSiteInfo().getShop() != null
                && vo.getSiteInfo().getShop().getCircleId() != null) {
            order.setCircleId(vo.getSiteInfo().getShop().getCircleId());
        }
        // --- 组装 mall_order_goods 行项列表 ---
        List<MallOrderGoods> orderGoods = new LinkedList<>();

        // 【循环】goodsData 每个 SKU → 转 MallOrderGoods 实体（含积分/虚拟商品配置快照）
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            MallOrderGoods item = new MallOrderGoods();
            item.setSiteId(vo.getSiteId());
            item.setMemberId(vo.getMemberId());
            item.setSkuId(sku.getSkuId());
            item.setGoodsId(sku.getGoodsId());
            item.setGoodsName(sku.getGoods().getGoodsName());
            item.setGoodsImage(sku.getGoods().getGoodsCover());
            item.setSkuName(sku.getSkuName());
            item.setSkuImage(sku.getSkuImage());
            item.setPrice(sku.getPrice());
            item.setOriginalPrice(sku.getOriginalPrice());
            item.setNum(sku.getNum());
            item.setGoodsMoney(sku.getGoodsMoney());
            item.setGoodsType(sku.getGoods().getGoodsType());
            item.setCostGoodsMoney(sku.getCostGoodsMoney());
            item.setDiscountMoney(sku.getDiscountMoney());
            item.setMallDiscountMoney(OrderCreateHelper.resolveSkuMallDiscountMoney(sku));
            item.setOrderGoodsMoney(OrderCreateHelper.calculateOrderGoodsMoney(sku));
            item.setStatus(1);
            item.setSupplyId(sku.getSupplyId());
            item.setSupplySkuId(sku.getSupplySkuId());
            item.setSupplyGoodsId(sku.getGoods().getSupplyGoodsId());

            // ========== 补充缺失字段 ==========
            item.setDeliveryMoney(sku.getDeliveryMoney());   // 需在 Sku 中添加
            int lineUsePoint = sku.getUsePoint() != null && sku.getUsePoint() > 0
                    ? sku.getUsePoint()
                    : (sku.getGoods() != null && sku.getGoods().getUsePoint() != null ? sku.getGoods().getUsePoint() : 0);
            item.setUsePoint(lineUsePoint);
            BigDecimal linePointDeduct = sku.getPointDeductMoney() != null && sku.getPointDeductMoney().compareTo(BigDecimal.ZERO) > 0
                    ? sku.getPointDeductMoney()
                    : (sku.getGoods() != null && sku.getGoods().getPointDeductMoney() != null
                    ? sku.getGoods().getPointDeductMoney() : BigDecimal.ZERO);
            item.setPointDeductMoney(linePointDeduct);
            item.setFullReductionMoney(sku.getFullReductionMoney());

            // 虚拟商品配置快照（发货/自动收货读此配置）
            if (sku.getGoods() != null && GoodsTypeEnum.VIRTUAL.getType().equals(sku.getGoods().getGoodsType())) {
                Map<String, Object> config = new HashMap<>();
                config.put("virtual_delivery_type", StringUtils.isNotEmpty(sku.getGoods().getVirtualDeliveryType())
                        ? sku.getGoods().getVirtualDeliveryType() : "auto");
                config.put("virtual_delivery_content", ObjectUtil.defaultIfNull(sku.getGoods().getVirtualDeliveryContent(), ""));
                config.put("virtual_receive_type", StringUtils.isNotEmpty(sku.getGoods().getVirtualReceiveType())
                        ? sku.getGoods().getVirtualReceiveType() : "artificial");
                config.put("virtual_verify_type", ObjectUtil.defaultIfNull(sku.getGoods().getVirtualVerifyType(), 0));
                config.put("virtual_indate", ObjectUtil.defaultIfNull(sku.getGoods().getVirtualIndate(), 0));
                config.put("virtual_refund", ObjectUtil.defaultIfNull(sku.getGoods().getVirtualRefund(), 0));
                config.put("virtual_refund_day", ObjectUtil.defaultIfNull(sku.getGoods().getVirtualRefundDay(), 0));
                config.put("virtual_verify_mobile", ObjectUtil.defaultIfNull(sku.getGoods().getVirtualVerifyMobile(), 0));
                item.setGoodsConfig(JSONUtil.toJsonStr(config));
                item.setVirtualRefundType(sku.getGoods().getVirtualRefund());
            }

            orderGoods.add(item);
        }

        order.setUsePoint(BigDecimal.valueOf(OrderPointDeductHelper.resolveOrderUsePoint(order.getUsePoint(), orderGoods)));
        if (order.getPointDeductMoney() == null || order.getPointDeductMoney().compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal pointDeductFromGoods = orderGoods.stream()
                    .map(g -> g.getPointDeductMoney() != null ? g.getPointDeductMoney() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (pointDeductFromGoods.compareTo(BigDecimal.ZERO) > 0) {
                order.setPointDeductMoney(pointDeductFromGoods);
            }
        }

        List<MallOrderGiftGoods> orderGiftGoods = buildOrderGiftGoodsList(vo);
        vo.setGiftGoods(orderGiftGoods);

        return this.createOrder(vo, order, orderGoods, orderGiftGoods);
    }

    /**
     * 首次进入 calculate：初始化 vo，加载站点/买家/商品/配送/发票配置。
     */
    private OrderCreateDataVo confirm(OrderCalculateParam param) {
        OrderCreateDataVo vo = new OrderCreateDataVo();
        vo.setSiteId(param.getSiteId());
        vo.setMemberId(param.getMemberId());
        vo.setOrderType(param.getOrderType());
        vo.setMemberRemark(param.getMemberRemark());
        vo.setOrderFrom(param.getOrderFrom());
        vo.setExtendData(param.getExtendData());
        this.getConfig(vo, "delivery_type", null, param);
        initSite(vo);
        OrderCreateDataVo.Buyer buyer = new OrderCreateDataVo.Buyer();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("nickname, point").eq("member_id", param.getMemberId()));
        if (member == null) throw new CommonException("找不到买家");
        BeanUtil.copyProperties(member, buyer);
        buyer.setMemberLevel(memberLevelService.findMemberLevel(param.getMemberId()));
        vo.setBuyer(buyer);
        SiteOrderConfigVo orderConfig = coreOrderConfigService.getConfig(param.getSiteId());
        if (ObjectUtil.isNotEmpty(orderConfig.getFormId())) {
            DiyForm diyForm = diyFormMapper.selectOne(new QueryWrapper<DiyForm>().select("form_id").eq("form_id", orderConfig.getFormId()).eq("status", 1));
            if (diyForm != null) vo.setFormId(orderConfig.getFormId());
        }
        this.getGoodsData(vo, param);
        this.getDelivery(vo, param);
        this.getConfig(vo, "invoice", null, param);
        return vo;
    }

    private void initSite(OrderCreateDataVo vo) {
        // 按 vo.siteId 查店铺信息，写入 vo.siteInfo（confirm 阶段初始化站点上下文）
        applySiteInfo(vo, coreShopService.getShopInfoOfOrderCreate(vo.getSiteId()));
    }

    /**
     * 校验店铺
     */
    private void checkSite(OrderCreateDataVo vo) {
        // 查店铺营业状态，business_status=打烊则 create 阶段拒绝下单
        ShopInfoOfOrderCreateVo siteInfo = coreShopService.getShopInfoOfOrderCreate(vo.getSiteId());
        // 【分支】shop.businessStatus=CLOSE：抛出「店铺已打烊」
        if (siteInfo.getSiteId() != null && siteInfo.getShop() != null
                && ShopStatusEnum.CLOSE.getCode().equals(siteInfo.getShop().getBusinessStatus())) {
            throw new CommonException("店铺\"" + siteInfo.getSiteName() + "\"已打烊");
        }
    }

    private void applySiteInfo(OrderCreateDataVo vo, ShopInfoOfOrderCreateVo siteInfo) {
        // 【分支】siteInfo.siteId 为空：无有效店铺，跳过写入
        if (siteInfo.getSiteId() == null) {
            return;
        }
        // 写入站点 id/名称；shop 非空时复制店铺扩展字段到 vo.siteInfo.shop
        vo.getSiteInfo().setSiteId(siteInfo.getSiteId());
        vo.getSiteInfo().setSiteName(siteInfo.getSiteName());
        if (siteInfo.getShop() != null) {
            BeanUtil.copyProperties(siteInfo.getShop(), vo.getSiteInfo().getShop());
        }
    }

    /**
     * calculate 阶段限购软校验：异常捕获后不抛出，由 error 列表反馈。
     */
    private void checkGoodsLimitBuySoft(OrderCreateDataVo vo) {
        // calculate 阶段限购软校验：异常捕获后不抛出，错误写入 vo.error 供前端展示
        try {
            String res = coreGoodsLimitBuyService.checkGoodsLimitBuy(vo.getMemberId(), vo);
            // 【分支】res 非空：限购不通过，追加到 error 列表
            if (!res.isEmpty()) {
                vo.getError().add(res);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * create 阶段限购硬校验
     */
    private void checkGoodsLimitBuyHard(OrderCreateDataVo vo) {
        // create 阶段限购硬校验：不通过直接抛 CommonException 阻断落库
        String res = coreGoodsLimitBuyService.checkGoodsLimitBuy(vo.getMemberId(), vo);
        if (!res.isEmpty()) throw new CommonException(res);
    }

    /**
     * 加载商品行：cartIds 或 skuData → 查 SKU/商品 → 算会员价/活动价 → 写入 vo.goodsData 与 basic 汇总。
     */
    private void getGoodsData(OrderCreateDataVo vo, OrderCalculateParam param) {
        Map<Integer, OrderCreateDataVo.Sku> goodsData = new HashMap<>();
        OrderCreateDataVo.Basic basic = vo.getBasic();
        Map<Integer, Long> skuCartData = new HashMap<>();
        if (ObjectUtil.isNotEmpty(param.getCartIds())) {
            List<OrderHelperParam.SkuItem> skuData = CollectUtils.convert(cartMapper.selectList(new QueryWrapper<MallCart>()
                    .in("id", param.getCartIds())
                    .select("id,goods_id, site_id, sku_id, num, market_type, market_type_id")), OrderHelperParam.SkuItem.class);
            if (skuData.isEmpty()) {
                throw new CommonException("购物车数据已过期");
            }
            if (skuData.size() != param.getCartIds().size()) {
                throw new CommonException("购物车数据已过期");
            }
            param.setSkuData(skuData);
            skuCartData = skuData.stream().collect(Collectors.toMap(OrderHelperParam.SkuItem::getSkuId, OrderHelperParam.SkuItem::getId));
        } else {
            if (param.getSkuData().isEmpty()) {
                throw new CommonException("购物车数据已过期");
            }
        }
        List<Integer> skuIds = param.getSkuData().stream().map(OrderHelperParam.SkuItem::getSkuId).toList();
        List<MallGoodsSku> skuSumList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().in("sku_id", skuIds));
        Map<Integer, MallGoodsSku> skuList = skuSumList.stream().collect(Collectors.toMap(MallGoodsSku::getSkuId, v -> v));

        Set<String> hasGoodsTypes = new HashSet<>();
        Set<String> hasDeliveryTypes = new HashSet<>();
        Integer virtualVerifyMobile = 0;
        Integer totalNum = 0;
        BigDecimal orderMoney = BigDecimal.ZERO, goodsMoney = BigDecimal.ZERO, deliveryMoney = BigDecimal.ZERO, costGoodsMoney = BigDecimal.ZERO;
        String body = "";
        // 【循环】param.skuData 每个下单 SKU → 组装 Sku/Goods、算价、累加 basic 汇总字段
        for (OrderHelperParam.SkuItem item : param.getSkuData()) {
            OrderCreateDataVo.Sku skuInfo = new OrderCreateDataVo.Sku();
            Integer skuId = item.getSkuId();
            Integer num = item.getNum();
            totalNum += item.getNum();
            String marketType = ObjectUtil.isNotEmpty(item.getMarketType()) ? item.getMarketType() : "";
            Integer marketTypeId = ObjectUtil.isNotEmpty(item.getMarketTypeId()) ? item.getMarketTypeId() : 0;
            MallGoodsSku sku = skuList.get(skuId);
            if (ObjectUtil.isEmpty(sku)) {
                throw new CommonException("购物车数据已过期");
            }
            BeanUtil.copyProperties(sku, skuInfo);
            Long cardId = ObjectUtil.isNotEmpty(skuCartData.get(skuId)) ? skuCartData.get(skuId) : 0L;
            skuInfo.setCardId(cardId);

            // 查询商品数据
            OrderCreateDataVo.Goods goodsVo = new OrderCreateDataVo.Goods();
            MallGoods goods = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("site_id", param.getSiteId()).eq("goods_id", skuInfo.getGoodsId()).eq("status", 1).eq("audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus()).eq("delete_time", 0));
            Assert.notNull(goods, "购物车数据已过期");
            BeanUtil.copyProperties(goods, goodsVo);

            skuInfo.setGoods(goodsVo);

            //商品校验
            DiyFormRecordsParam diyRecordsParam = new DiyFormRecordsParam();
            diyRecordsParam.setFormId(skuInfo.getGoods().getFormId());
            diyRecordsParam.setMemberId(param.getMemberId());
            List<String> error = coreDiyFormRecordsService.check(diyRecordsParam);
            if (!error.isEmpty()) {
                skuInfo.getGoods().setFormId(0);
            }
            skuInfo.setOriginalPrice(skuInfo.getPrice());
            skuInfo.setShowType(MallConstant.ORIGINAL_PRICE);
            skuInfo.setMemberPrice(skuInfo.getPrice());
            //计算会员价格
            if (ObjectUtil.isNotEmpty(vo.getBuyer().getMemberLevel())) {
                String memberPrice = sku.getMemberPrice();
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(memberPrice) ? JSONUtil.parseObj(memberPrice) : new JSONObject();
                skuInfo.setMemberPrice(goodsService.calculateMemberPrice(vo.getBuyer().getMemberLevel(), goods.getMemberDiscount(), memberPriceConfig, sku.getPrice()));
                skuInfo.setPrice(skuInfo.getMemberPrice());
            }

            int comparison = (skuInfo.getOriginalPrice().subtract(skuInfo.getMemberPrice())).compareTo(BigDecimal.ZERO);
            if (comparison > 0) {
                skuInfo.setShowType(MallConstant.MEMBER_PRICE);
            }

            // 付费会员卡折扣（优先于普通会员价）
            BigDecimal vipCardPrice = coreGoodsActivePriceService.calculateVipCardPrice(
                    param.getMemberId(),
                    goods.getVipCardDiscount(),
                    sku.getSvipDiscount(),
                    sku.getSvipPrice(),
                    sku.getPrice()
            );
            if (vipCardPrice != null) {
                skuInfo.setMemberPrice(vipCardPrice);
                skuInfo.setPrice(vipCardPrice);
                skuInfo.setShowType(MallConstant.SVIP_PRICE);
            }

            //默认金额填充
            skuInfo.setDiscountMoney(BigDecimal.ZERO);
            int itemVirtualVerifyMobile = ObjectUtil.isNotEmpty(skuInfo.getGoods().getVirtualVerifyMobile()) ? skuInfo.getGoods().getVirtualVerifyMobile() : 0;
            hasGoodsTypes.add(goodsVo.getGoodsType());
            if (StringUtils.isNotEmpty(goodsVo.getDeliveryType()) && JSONUtil.isTypeJSON(goodsVo.getDeliveryType())) {
                JSONArray deliveryTypes = JSONUtil.parseArray(goodsVo.getDeliveryType());
                deliveryTypes.forEach(info -> hasDeliveryTypes.add(info.toString()));
            }
            if (itemVirtualVerifyMobile > 0) {
                virtualVerifyMobile = goodsVo.getVirtualVerifyMobile();
            }

            skuInfo.setNum(num);
            skuInfo.setMarketType(marketType);
            skuInfo.setMarketTypeId(marketTypeId);
            skuInfo.setMaxBuy(goodsVo.getMaxBuy());
            skuInfo.setMinBuy(goodsVo.getMinBuy());
            // 计算优惠信息
            GoodsMarketCalculateEventDefiner.GoodsMarketCalculateEvent event = new GoodsMarketCalculateEventDefiner.GoodsMarketCalculateEvent();
            event.addAppSign("mall");
            event.setSiteId(vo.getSiteId());
            event.setVo(vo);
            event.setSkuInfo(skuInfo);
            event.setSkuData(param.getSkuData());
            List<GoodsMarketCalculateEventDefiner.GoodsMarketCalculateResult> marketData = EventAndSubscribeOfPublisher.publishAndCallback(event);
            boolean marketApplied = false;
            if (!marketData.isEmpty()) {
                for (GoodsMarketCalculateEventDefiner.GoodsMarketCalculateResult result : marketData) {
                    if (ObjectUtil.isNotEmpty(result) && ObjectUtil.isNotEmpty(result.getSkuInfo())) {
                        skuInfo = result.getSkuInfo();
                        marketApplied = true;
                        break;
                    }
                }
            }
            if (!marketApplied) {
                BigDecimal price = skuInfo.getPrice();
                skuInfo.setGoodsMoney(price.multiply(BigDecimal.valueOf(item.getNum())));
            }

            // 小计
            skuInfo.setCostGoodsMoney(skuInfo.getCostPrice().multiply(new BigDecimal(item.getNum())));
            goodsMoney = goodsMoney.add(skuInfo.getGoodsMoney());
            costGoodsMoney = costGoodsMoney.add(skuInfo.getCostGoodsMoney());
            skuInfo.setSupplyGoodsId(skuInfo.getGoods().getSupplyGoodsId());
            goodsData.put(skuInfo.getSkuId(), skuInfo);
            body = StringUtils.isNotEmpty(body) ? body + skuInfo.getSkuName() + goodsVo.getGoodsName() : skuInfo.getSkuName() + goodsVo.getGoodsName();
        }
        basic.setHasGoodsTypes(hasGoodsTypes);
        basic.setHasDeliveryTypes(hasDeliveryTypes);
        basic.setVirtualVerifyMobile(virtualVerifyMobile);
        basic.setTotalNum(totalNum);
        vo.setGoodsData(goodsData);
        basic.setGoodsMoney(goodsMoney);
        basic.setBody(body);
        basic.setCostGoodsMoney(costGoodsMoney);
        basic.setOrderType(param.getOrderType());
        basic.setExtendData(param.getExtendData());
    }

    /**
     * 查询订单可用优惠券
     *
     * @param orderKey 订单缓存key
     */
    @Override
    public List<OrderCouponListVo> getCoupon(String orderKey) {
        // orderKey 定位 Redis 订单快照，补全 orderType 与营销开关后再匹配可用券
        OrderCreateDataVo vo = loadOrderVo(orderKey, OrderCreateDataVo.class);
        resolveOrderType(vo, null);
        ensureMarketingFlagsByOrderType(vo, vo.getOrderType());
        ensureCouponFlags(vo);
        ensureFullActiveFlags(vo);
        ensureCanUsePointFlag(vo);

        // memberCouponList：会员在该站点下 status=待使用的优惠券
        List<MallMemberCouponListVo> memberCouponList = coreCouponMemberService.getUseCouponListByMemberId(vo.getMemberId(), vo.getSiteId());
        List<OrderCouponListVo> couponList = new LinkedList<>();

        if (ObjectUtil.isNotEmpty(memberCouponList)) {
            // 【循环】memberCouponList 每张券 item：按 type 匹配 goodsData 并标记 isNormal/error
            for (MallMemberCouponListVo item : memberCouponList) {
                // 【分支】券模板缺失或非 NORMAL 状态：跳过
                if (item.getCoupon() == null || !CouponStatusEnum.NORMAL.getStatus().equals(item.getCoupon().getStatus())) {
                    continue;
                }

                OrderCouponListVo couponVo = new OrderCouponListVo();
                BeanUtil.copyProperties(item, couponVo);

                CouponTypeEnum typeEnum = CouponTypeEnum.getEnumByType(Integer.valueOf(item.getType()));
                boolean matchGoods = false;
                BigDecimal matchGoodsMoney = BigDecimal.ZERO; // 适用商品金额合计，用于比对 minConditionMoney
                List<MallCouponGoods> goodsData = item.getGoods() != null ? item.getGoods() : Collections.emptyList();

                // 【分支】type=ALL：全店商品适用，matchGoodsMoney=整单 goodsMoney
                if (typeEnum == CouponTypeEnum.ALL) {
                    matchGoods = true;
                    matchGoodsMoney = vo.getBasic().getGoodsMoney();
                // 【分支】type=CATEGORY：券指定分类，遍历 SKU 累加命中分类的 goodsMoney
                } else if (typeEnum == CouponTypeEnum.CATEGORY) {
                    List<Integer> categoryIds = goodsData.stream()
                            .map(MallCouponGoods::getCategoryId)
                            .filter(Objects::nonNull)
                            .toList();
                    for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                        List<Integer> goodsCategory = JSONUtil.parseArray(sku.getGoods().getGoodsCategory()).toList(Integer.class);
                        if (goodsCategory.stream().anyMatch(categoryIds::contains)) {
                            matchGoods = true;
                            matchGoodsMoney = matchGoodsMoney.add(sku.getGoodsMoney());
                        }
                    }
                // 【分支】type=GOODS：券指定商品，遍历 SKU 累加命中 goodsId 的 goodsMoney
                } else if (typeEnum == CouponTypeEnum.GOODS) {
                    List<Integer> goodsIds = goodsData.stream()
                            .map(MallCouponGoods::getGoodsId)
                            .filter(Objects::nonNull)
                            .toList();
                    for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                        if (goodsIds.contains(sku.getGoodsId())) {
                            matchGoods = true;
                            matchGoodsMoney = matchGoodsMoney.add(sku.getGoodsMoney());
                        }
                    }
                }

                // 【分支】无适用商品 → isNormal=false；金额未达门槛 → 同上；否则可用
                if (!matchGoods) {
                    couponVo.setIsNormal(false);
                    couponVo.setError("没有适用的商品");
                } else if (matchGoodsMoney.compareTo(item.getMinConditionMoney()) < 0) {
                    couponVo.setIsNormal(false);
                    couponVo.setError("未达到最低可使用金额");
                } else {
                    couponVo.setIsNormal(true);
                }

                couponList.add(couponVo);
            }
        }
        return couponList;
    }

    /**
     * 将 basic.full_gift_sku_list 转为 MallOrderGiftGoods 落库列表。
     */
    private List<MallOrderGiftGoods> buildOrderGiftGoodsList(OrderCreateDataVo vo) {
        // src：basic.full_gift_sku_list，每项 Map 含 goods_id/sku_id/num 等赠品字段
        List<Map<String, Object>> src = resolveFullGiftSkuListForInsert(vo);
        if (src.isEmpty()) {
            return Collections.emptyList();
        }
        int supplyId = vo.getSupplyId() != null ? vo.getSupplyId() : 0; // 供货拆单时继承子单 supplyId
        List<MallOrderGiftGoods> out = new ArrayList<>(src.size());
        // 【循环】src 每条赠品 Map m → 组装 MallOrderGiftGoods 落库实体
        for (Map<String, Object> m : src) {
            Object gobj = m.get("goods");
            @SuppressWarnings("unchecked")
            Map<String, Object> goodsMap = gobj instanceof Map ? (Map<String, Object>) gobj : null;
            MallGoods goodsEntity = gobj instanceof MallGoods ? (MallGoods) gobj : null;

            MallOrderGiftGoods g = new MallOrderGiftGoods();
            g.setSiteId(vo.getSiteId());
            g.setMemberId(vo.getMemberId());
            // goods_id：Map 直取 → goodsEntity → goodsMap 三级回退
            int goodsId = toInt(m.get("goods_id"));
            if (goodsId == 0 && goodsEntity != null && goodsEntity.getGoodsId() != null) {
                goodsId = goodsEntity.getGoodsId();
            }
            g.setGoodsId(goodsId);
            g.setSkuId(toInt(m.get("sku_id")));

            String goodsName = toStr(m.get("goods_name"));
            if (goodsName.isEmpty() && goodsEntity != null && goodsEntity.getGoodsName() != null) {
                goodsName = goodsEntity.getGoodsName();
            }
            if (goodsName.isEmpty() && goodsMap != null) {
                goodsName = toStr(goodsMap.get("goods_name"));
            }
            g.setGoodsName(goodsName);
            g.setSkuName(toStr(m.get("sku_name")));

            String goodsImage = toStr(m.get("goods_image"));
            if (goodsImage.isEmpty() && goodsEntity != null && goodsEntity.getGoodsCover() != null) {
                goodsImage = goodsEntity.getGoodsCover();
            }
            if (goodsImage.isEmpty() && goodsMap != null) {
                goodsImage = toStr(goodsMap.get("goods_cover"));
                if (goodsImage.isEmpty()) {
                    goodsImage = toStr(goodsMap.get("goods_image"));
                }
            }
            g.setGoodsImage(goodsImage);
            g.setSkuImage(toStr(m.get("sku_image")));

            g.setPrice(BigDecimal.ZERO); // 赠品售价为 0
            g.setNum(toInt(m.get("num")));
            g.setGoodsMoney(BigDecimal.ZERO);
            g.setOriginalPrice(toBigDecimal(m.get("original_price")));

            String goodsType = toStr(m.get("goods_type"));
            if (goodsType.isEmpty() && goodsEntity != null && goodsEntity.getGoodsType() != null) {
                goodsType = goodsEntity.getGoodsType();
            }
            if (goodsType.isEmpty() && goodsMap != null) {
                goodsType = toStr(goodsMap.get("goods_type"));
            }
            g.setGoodsType(goodsType);

            g.setSupplyId(supplyId);
            int supplyGoodsId = toInt(m.get("supply_goods_id"));
            if (supplyGoodsId == 0 && goodsEntity != null && goodsEntity.getSupplyGoodsId() != null) {
                supplyGoodsId = goodsEntity.getSupplyGoodsId();
            }
            if (supplyGoodsId == 0 && goodsMap != null) {
                supplyGoodsId = toInt(goodsMap.get("supply_goods_id"));
            }
            g.setSupplyGoodsId(supplyGoodsId);
            g.setSupplySkuId(toInt(m.get("supply_sku_id")));
            g.setCostGoodsMoney(toBigDecimal(m.get("cost_goods_money")));
            out.add(g);
        }
        return out;
    }

    private static List<Map<String, Object>> resolveFullGiftSkuListForInsert(OrderCreateDataVo vo) {
        // 【分支】basic 或 fullGiftSkuList 为空：无满赠，返回空列表
        if (vo.getBasic() == null || ObjectUtil.isEmpty(vo.getBasic().getFullGiftSkuList())) {
            return Collections.emptyList();
        }
        // 直接返回 basic.fullGiftSkuList（calculate 阶段已写入的赠品快照）
        return vo.getBasic().getFullGiftSkuList();
    }

    private static int toInt(Object o) {
        // 【分支】null → 0
        if (o == null) {
            return 0;
        }
        // 【分支】Number 直接 intValue
        if (o instanceof Number n) {
            return n.intValue();
        }
        // 【分支】非空 String 尝试 parseInt，失败返回 0
        if (o instanceof String s && !s.isEmpty()) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private static String toStr(Object o) {
        // 赠品 Map 字段安全转 String：null → ""，否则 toString()
        return o == null ? "" : o.toString();
    }

    private static BigDecimal toBigDecimal(Object o) {
        // 【分支】null → BigDecimal.ZERO
        if (o == null) {
            return BigDecimal.ZERO;
        }
        // 【分支】已是 BigDecimal 直接返回
        if (o instanceof BigDecimal bd) {
            return bd;
        }
        // 【分支】Number 转 double 再构造 BigDecimal
        if (o instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        // 【分支】非空 String 尝试 new BigDecimal，失败返回 ZERO
        if (o instanceof String s && !s.isEmpty()) {
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 落库前校验 order_money 与 goods/delivery/discount/mall_discount 一致，防止积分抵扣后金额异常。
     */
    private void assertOrderMoneyConsistency(OrderCreateDataVo vo) {
        // 落库前校验 basic.order_money 与 goods/delivery/discount/mall_discount 分项一致
        OrderCreateDataVo.Basic basic = vo.getBasic();
        if (basic == null) {
            return;
        }
        BigDecimal goods = ObjectUtil.defaultIfNull(basic.getGoodsMoney(), BigDecimal.ZERO);
        BigDecimal delivery = ObjectUtil.defaultIfNull(basic.getDeliveryMoney(), BigDecimal.ZERO);
        BigDecimal discount = ObjectUtil.defaultIfNull(basic.getDiscountMoney(), BigDecimal.ZERO);
        BigDecimal mallDiscount = ObjectUtil.defaultIfNull(basic.getMallDiscountMoney(), BigDecimal.ZERO);
        // expected = goods + delivery - discount - mallDiscount（最低 0，保留 2 位）
        BigDecimal expected = goods.add(delivery).subtract(discount).subtract(mallDiscount).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual = ObjectUtil.defaultIfNull(basic.getOrderMoney(), BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        // 【分支】expected 与 actual 差值 >0.01：金额计算异常
        if (expected.subtract(actual).abs().compareTo(new BigDecimal("0.01")) > 0) {
            throw new CommonException("订单金额计算异常，请重新下单");
        }
        int usePoint = basic.getUsePoint() != null ? basic.getUsePoint() : 0;
        BigDecimal pointDeduct = ObjectUtil.defaultIfNull(basic.getPointDeductMoney(), BigDecimal.ZERO);
        // 【分支】有积分抵扣时：actual 不得超过 goods+delivery-discount-pointDeduct
        if (usePoint > 0 && pointDeduct.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal maxPay = goods.add(delivery).subtract(discount).subtract(pointDeduct).max(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
            if (actual.compareTo(maxPay.add(new BigDecimal("0.01"))) > 0) {
                throw new CommonException("积分抵扣后订单金额异常，请重新下单");
            }
        }
    }

    /**
     * 金额计算
     * 将所有金额参数转换为分进行计算，最后再转换回元
     *
     * @param amounts 可变金额参数数组
     * @return 计算后的金额
     */
    public static BigDecimal moneyCalculate(BigDecimal... amounts) {
        // 各金额先乘 100 转分再累加，最后除 100 还原为元，避免 double 精度丢失
        BigDecimal totalInCents = BigDecimal.ZERO;

        // 【循环】amounts 每个 amount：转分后累加到 totalInCents
        for (BigDecimal amount : amounts) {
            BigDecimal amountInCents = amount.multiply(new BigDecimal("100"));
            totalInCents = totalInCents.add(amountInCents);
        }

        // 总分 ÷100，HALF_UP 保留 2 位小数
        return totalInCents.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * 金额按「分」向下取整再还原为元（两位小数），与参考 trait 中 floor(strval(money*100))/100 一致。
     */
    private static BigDecimal moneyFormat(BigDecimal money) {
        // 金额 ×100 后 FLOOR 取整到分，再 ÷100 还原为元（向下取整到分位）
        if (money == null) {
            return BigDecimal.ZERO;
        }
        return money.multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.FLOOR)
                .divide(new BigDecimal("100"), 2, RoundingMode.UNNECESSARY);
    }
}
