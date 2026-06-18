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
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.shop.ShopStatusEnum;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.mall.constant.MallConstant;
import com.niu.mall.entity.cart.MallCart;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGiftGoods;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.coupon.CouponStatusEnum;
import com.niu.mall.enums.coupon.CouponTypeEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.enums.trade.SettlementEnum;
import com.niu.mall.event.marketing.GoodsMarketCalculateEventDefiner;
import com.niu.mall.event.order.CheckOrderOfCreateEventDefiner;
import com.niu.mall.mapper.cart.MallCartMapper;
import com.niu.mall.mapper.coupon.MallCouponGoodsMapper;
import com.niu.mall.mapper.coupon.MallCouponMemberMapper;
import com.niu.mall.mapper.exchange.MallPointExchangeMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.api.goods.IGoodsService;
import com.niu.mall.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.mall.service.core.marketing.ICoreCouponMemberService;
import com.niu.mall.service.core.marketing.vo.MallMemberCouponListVo;
import com.niu.mall.service.core.order.ICoreOrderCreateService;
import com.niu.mall.service.core.order.ICoreSiteOrderConfigService;
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
    MallCouponMemberMapper mallCouponMemberMapper;

    @Resource
    MallCouponGoodsMapper mallCouponGoodsMapper;

    @Resource
    MallPointExchangeMapper mallPointExchangeMapper;

    @Resource
    ICoreGoodsLimitBuyService coreGoodsLimitBuyService;

    @Resource
    ICoreDiyFormRecordsService coreDiyFormRecordsService;

    @Resource
    ICoreSiteOrderConfigService coreOrderConfigService;

    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    SiteShopMapper siteShopMapper;

    @Resource
    ICoreCouponMemberService coreCouponMemberService;

    /**
     * 缓存加载后补全优惠券开关默认值（对齐 PHP Trait 默认 is_plat_coupon=1）
     */
    private void ensureCouponFlags(OrderCreateDataVo vo) {
        if (vo.getIsPlatCoupon() == null) {
            vo.setIsPlatCoupon(1);
        }
        if (vo.getIsShopCoupon() == null) {
            vo.setIsShopCoupon(1);
        }
    }

    /**
     * 缓存反序列化后补全满减/满赠开关默认值（JSON 缺字段时为 null，会导致满减被跳过）
     */
    private void ensureFullActiveFlags(OrderCreateDataVo vo) {
        if (vo.getIsJoinFullReduction() == null) {
            vo.setIsJoinFullReduction(1);
        }
        if (vo.getIsJoinFullGift() == null) {
            vo.setIsJoinFullGift(1);
        }
    }

    /**
     * 计算
     *
     * @param param
     */
    public OrderCreateDataVo calculate(OrderCalculateParam param, MallOrderCreateDataVo mallOrderCreateDataVo) {
        OrderCreateDataVo vo;
        // 与 CoreOrderCreateService::calculate 一致：无 order_key 则 confirm；否则 getOrderCache(order_key + "_basic")
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
        ensureCouponFlags(vo);
        ensureFullActiveFlags(vo);

        // 计算优惠
        calculateDiscount(vo, param);

        //优惠券和满减计算完成后根据实付金额计算满赠
        calculateFullGift(vo, param);
        // 与 CoreOrderCreateService::calculate 中 calculateFullGift 之后的 setOrderCache 一致（快照再算运费）
        this.cacheOrderVo(vo);
        this.cacheOrderVoBasic(vo);

        // 计算运费
        calculateDelivery(vo, param, mallOrderCreateDataVo);

        //检测限购
        checkGoodsLimitBuy(vo);


        // 金额格式化并回写 basic（含 mall_discount_money，与 refreshBasicOrderMoney 公式一致）
        refreshBasicOrderMoney(vo);
        OrderCreateDataVo.Basic basic = vo.getBasic();
        basic.setPointDeductMoney(moneyFormat(basic.getPointDeductMoney()));
        cacheOrderVo(vo);
        vo.setMemberRemark(param.getMemberRemark());
        return vo;
    }

    @Override
    public void recheckFullActiveBeforeCreate(OrderCreateDataVo vo) {
        super.recheckFullActiveBeforeCreate(vo);
    }

    /**
     * 订单创建
     *
     * @return
     */
    @Override
    public OrderCreateResultVo create(OrderCreateDataVo vo) {
        // 判断有没有错误信息
        if (!vo.getError().isEmpty()) throw new CommonException(vo.getError().get(0));
        recheckFullActiveBeforeCreate(vo);
        //校验店铺
        checkSite(vo);
        //校验商品售价是否低于供货价 和 拼团组
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

        // 检测库存（秒杀订单由秒杀模块校验 seckill_stock）
        boolean isSeckillOrder = "seckill".equals(vo.getOrderType());
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().select("stock,is_sell").eq("sku_id", sku.getSkuId()));
            if (!(ObjectUtil.isNotEmpty(goodsSku.getIsSell()) && goodsSku.getIsSell().equals(1))) {
                throw new CommonException("商品" + sku.getGoods().getGoodsName() + " " + sku.getSkuName() + "暂未开售");
            }
            if (!isSeckillOrder && goodsSku.getStock().compareTo(sku.getNum()) == -1) {
                throw new CommonException("商品" + sku.getGoods().getGoodsName() + " " + sku.getSkuName() + "库存不足");
            }
        }

        Integer needTakerMobile = 0;
        if ((ObjectUtil.isNotEmpty(vo.getBasic().getVirtualVerifyMobile()) && vo.getBasic().getVirtualVerifyMobile().equals(1)) ||
                (ObjectUtil.isNotEmpty(vo.getBasic().getHasGoodsTypes()) && vo.getBasic().getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType()))) {
            needTakerMobile = 1;
        }

        refreshBasicOrderMoney(vo);
        assertOrderMoneyConsistency(vo);

        // 订单
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
        order.setMemberRemark(vo.getMemberRemark());
        order.setSupplyId(vo.getSupplyId());
        order.setIsSyncSupply(vo.getSupplyId() > 0 ? 2 : 1);
        order.setSupplyDeliveryMoney(vo.getSupplyId() > 0 ? vo.getBasic().getDeliveryMoney() : BigDecimal.ZERO);
        order.setFullReductionMoney(vo.getBasic().getFullReductionMoney());
        order.setUsePoint(vo.getBasic().getUsePoint() != null ? vo.getBasic().getUsePoint() : 0);
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
        order.setCreateTime(System.currentTimeMillis() / 1000);
        order.setWeappVersion(vo.getWeappVersion());
        // 订单项

        List<MallOrderGoods> orderGoods = new LinkedList<>();


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

        order.setUsePoint(OrderPointDeductHelper.resolveOrderUsePoint(order.getUsePoint(), orderGoods));
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
     * 订单确认
     *
     * @param param
     * @return
     */
    private OrderCreateDataVo confirm(OrderCalculateParam param) {
        OrderCreateDataVo vo = new OrderCreateDataVo();
        vo.setSiteId(param.getSiteId());
        vo.setMemberId(param.getMemberId());
        vo.setOrderType(param.getOrderType());
        vo.setMemberRemark(param.getMemberRemark());
        vo.setOrderFrom(param.getOrderFrom());
        vo.setExtendData(param.getExtendData());
        //查询配送配置
        this.getConfig(vo, "delivery_type", null, param);
        initSite(vo);
        // 查询会员信息
        OrderCreateDataVo.Buyer buyer = new OrderCreateDataVo.Buyer();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("nickname, point").eq("member_id", param.getMemberId()));
        if (member == null) throw new CommonException("找不到买家");
        BeanUtil.copyProperties(member, buyer);
        buyer.setMemberLevel(memberLevelService.findMemberLevel(param.getMemberId()));
        vo.setBuyer(buyer);
        SiteOrderConfigVo orderConfig = coreOrderConfigService.getConfig(param.getSiteId());
        if (ObjectUtil.isNotEmpty(orderConfig.getFormId())) {
            DiyForm diyForm = diyFormMapper.selectOne(new QueryWrapper<DiyForm>().select("form_id").eq("form_id", orderConfig.getFormId()).eq("status", 1));
            if (diyForm != null) vo.setFormId(Integer.valueOf(orderConfig.getFormId()));
        }
        // 查询商品信息
        this.getGoodsData(vo, param);

        // 查询配送方式
        this.getDelivery(vo, param);

        //查询发票配置
        this.getConfig(vo, "invoice", null, param);

        // 查询配送方式
        //this.selectTakeAddress(vo, param);
        return vo;
    }

    private void initSite(OrderCreateDataVo vo) {

        Site site = siteMapper.selectById(vo.getSiteId());
        BeanUtil.copyProperties(site, vo.getSiteInfo());
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", vo.getSiteId()));
        if (ObjectUtil.isNotEmpty(shop)) {
            BeanUtil.copyProperties(shop, vo.getSiteInfo().getShop());
        }

    }

    /**
     * 校验店铺
     */
    private void checkSite(OrderCreateDataVo vo) {
        Site site = siteMapper.selectById(vo.getSiteId());
        BeanUtil.copyProperties(site, vo.getSiteInfo());
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", vo.getSiteId()));
        if (ObjectUtil.isNotEmpty(shop) && ObjectUtil.isNotEmpty(site) && ShopStatusEnum.CLOSE.getCode().equals(shop.getBusinessStatus())) {
            BeanUtil.copyProperties(shop, vo.getSiteInfo().getShop());
            throw new CommonException("店铺" + site.getSiteName() + ShopStatusEnum.CLOSE.getName());
        }
    }

    /**
     * 检测限购
     *
     * @param vo
     */
    private void checkGoodsLimitBuy(OrderCreateDataVo vo) {
        String res = coreGoodsLimitBuyService.checkGoodsLimitBuy(vo.getMemberId(), vo);
        if (!res.equals("")) throw new CommonException(res);
    }

    /**
     * 查询商品信息
     *
     * @param vo
     * @param param
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

        //订单中包含的商品形式
        Set<String> hasGoodsTypes = new HashSet<>();
        //订单中包含的配送形式
        Set<String> hasDeliveryTypes = new HashSet<>();
        Integer virtualVerifyMobile = 0;
        Integer totalNum = 0;
        BigDecimal orderMoney = BigDecimal.ZERO, goodsMoney = BigDecimal.ZERO, deliveryMoney = BigDecimal.ZERO, costGoodsMoney = BigDecimal.ZERO;
        String body = "";
        for (OrderHelperParam.SkuItem item : param.getSkuData()) {
            // 查询规格数据
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
            MallGoods goods = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("site_id", param.getSiteId()).eq("goods_id", skuInfo.getGoodsId()).eq("status", 1).eq("delete_time", 0));
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

            //默认金额填充
            skuInfo.setDiscountMoney(BigDecimal.ZERO);
            Integer itemVirtualVerifyMobile = ObjectUtil.isNotEmpty(skuInfo.getGoods().getVirtualVerifyMobile()) ? skuInfo.getGoods().getVirtualVerifyMobile() : 0;
            hasGoodsTypes.add(goodsVo.getGoodsType());
            if (StringUtils.isNotEmpty(goodsVo.getDeliveryType()) && JSONUtil.isTypeJSON(goodsVo.getDeliveryType())) {
                JSONArray deliveryTypes = JSONUtil.parseArray(goodsVo.getDeliveryType());
                deliveryTypes.forEach(info -> {
                    hasDeliveryTypes.add(info.toString());
                });
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
            Boolean er = false;
            if (!marketData.isEmpty()) {
                for (GoodsMarketCalculateEventDefiner.GoodsMarketCalculateResult result : marketData) {
                    if (ObjectUtil.isNotEmpty(result) && ObjectUtil.isNotEmpty(result.getSkuInfo())) {
                        skuInfo = result.getSkuInfo();
                        er = true;
                    }
                }
            }
            if (!er) {
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
     * @param orderKey
     */
    @Override
    public List<OrderCouponListVo> getCoupon(String orderKey) {
        OrderCreateDataVo vo = loadOrderVo(orderKey, OrderCreateDataVo.class);
        ensureCouponFlags(vo);

        List<MallMemberCouponListVo> memberCouponList = coreCouponMemberService.getUseCouponListByMemberId(vo.getMemberId(), vo.getSiteId());
        List<OrderCouponListVo> couponList = new LinkedList<>();

        if (ObjectUtil.isNotEmpty(memberCouponList)) {
            for (MallMemberCouponListVo item : memberCouponList) {
                if (item.getCoupon() == null || !CouponStatusEnum.NORMAL.getStatus().equals(item.getCoupon().getStatus())) {
                    continue;
                }

                OrderCouponListVo couponVo = new OrderCouponListVo();
                BeanUtil.copyProperties(item, couponVo);

                CouponTypeEnum typeEnum = CouponTypeEnum.getEnumByType(Integer.valueOf(item.getType()));
                boolean matchGoods = false;
                BigDecimal matchGoodsMoney = BigDecimal.ZERO;
                List<MallCouponGoods> goodsData = item.getGoods() != null ? item.getGoods() : Collections.emptyList();

                if (typeEnum == CouponTypeEnum.ALL) {
                    matchGoods = true;
                    matchGoodsMoney = vo.getBasic().getGoodsMoney();
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
     * 对应 CoreOrderCreateService::create 中 foreach basic.full_gift_sku_list 组 gift_goods_data。
     */
    private List<MallOrderGiftGoods> buildOrderGiftGoodsList(OrderCreateDataVo vo) {
        List<Map<String, Object>> src = resolveFullGiftSkuListForInsert(vo);
        if (src.isEmpty()) {
            return Collections.emptyList();
        }
        int supplyId = vo.getSupplyId() != null ? vo.getSupplyId() : 0;
        List<MallOrderGiftGoods> out = new ArrayList<>(src.size());
        for (Map<String, Object> m : src) {
            Object gobj = m.get("goods");
            @SuppressWarnings("unchecked")
            Map<String, Object> goodsMap = gobj instanceof Map ? (Map<String, Object>) gobj : null;
            MallGoods goodsEntity = gobj instanceof MallGoods ? (MallGoods) gobj : null;

            MallOrderGiftGoods g = new MallOrderGiftGoods();
            g.setSiteId(vo.getSiteId());
            g.setMemberId(vo.getMemberId());
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

            g.setPrice(BigDecimal.ZERO);
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
        if (vo.getBasic() == null || ObjectUtil.isEmpty(vo.getBasic().getFullGiftSkuList())) {
            return Collections.emptyList();
        }
        return vo.getBasic().getFullGiftSkuList();
    }

    private static int toInt(Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof Number n) {
            return n.intValue();
        }
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
        return o == null ? "" : o.toString();
    }

    private static BigDecimal toBigDecimal(Object o) {
        if (o == null) {
            return BigDecimal.ZERO;
        }
        if (o instanceof BigDecimal bd) {
            return bd;
        }
        if (o instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
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
        OrderCreateDataVo.Basic basic = vo.getBasic();
        if (basic == null) {
            return;
        }
        BigDecimal goods = ObjectUtil.defaultIfNull(basic.getGoodsMoney(), BigDecimal.ZERO);
        BigDecimal delivery = ObjectUtil.defaultIfNull(basic.getDeliveryMoney(), BigDecimal.ZERO);
        BigDecimal discount = ObjectUtil.defaultIfNull(basic.getDiscountMoney(), BigDecimal.ZERO);
        BigDecimal mallDiscount = ObjectUtil.defaultIfNull(basic.getMallDiscountMoney(), BigDecimal.ZERO);
        BigDecimal expected = goods.add(delivery).subtract(discount).subtract(mallDiscount).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual = ObjectUtil.defaultIfNull(basic.getOrderMoney(), BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        if (expected.subtract(actual).abs().compareTo(new BigDecimal("0.01")) > 0) {
            throw new CommonException("订单金额计算异常，请重新下单");
        }
        Integer usePoint = basic.getUsePoint() != null ? basic.getUsePoint() : 0;
        BigDecimal pointDeduct = ObjectUtil.defaultIfNull(basic.getPointDeductMoney(), BigDecimal.ZERO);
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
        BigDecimal totalInCents = BigDecimal.ZERO;

        for (BigDecimal amount : amounts) {
            // 将金额转换为分（乘以100）
            BigDecimal amountInCents = amount.multiply(new BigDecimal("100"));
            totalInCents = totalInCents.add(amountInCents);
        }

        // 将总分转换回元（除以100），并保留2位小数
        return totalInCents.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * 金额按「分」向下取整再还原为元（两位小数），与参考 trait 中 floor(strval(money*100))/100 一致。
     */
    private static BigDecimal moneyFormat(BigDecimal money) {
        if (money == null) {
            return BigDecimal.ZERO;
        }
        return money.multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.FLOOR)
                .divide(new BigDecimal("100"), 2, RoundingMode.UNNECESSARY);
    }
}
