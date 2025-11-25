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
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.cart.MallCart;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.coupon.MallCouponMember;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.coupon.CouponMemberStatusEnum;
import com.niu.mall.enums.coupon.CouponTypeEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
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
import com.niu.mall.service.core.order.ICoreOrderConfigService;
import com.niu.mall.service.core.order.ICoreOrderCreateService;
import com.niu.mall.service.core.order.ICoreSiteOrderConfigService;
import com.niu.mall.service.core.order.helper.OrderCreateHelper;
import com.niu.mall.service.core.order.param.OrderCalculateParam;
import com.niu.mall.service.core.order.param.OrderCreateParam;
import com.niu.mall.service.core.order.param.OrderHelperParam;
import com.niu.mall.service.core.order.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    ICoreSiteOrderConfigService coreOrderConfigService;

    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    SiteShopMapper siteShopMapper;

    /**
     * 计算
     *
     * @param param
     */
    public OrderCreateDataVo calculate(OrderCalculateParam param) {
        OrderCreateDataVo vo = null;

        vo = this.confirm(param);

        vo.getError().clear();

        // 计算运费
        calculateDelivery(vo, param);

        //检测限购
        checkGoodsLimitBuy(vo);
        // 计算优惠
        calculateDiscount(vo, param);

        // 计算订单金额
        OrderCreateDataVo.Basic basic = vo.getBasic();
        BigDecimal orderMoney = moneyCalculate(basic.getDeliveryMoney(),basic.getGoodsMoney().subtract(basic.getDiscountMoney()));
        basic.setOrderMoney(orderMoney.max(BigDecimal.ZERO));

        cacheOrderVo(vo);
        return vo;
    }

    /**
     * 订单创建
     *
     * @return
     */
    @Override
    public OrderCreateResultVo create(OrderCreateDataVo vo) {

        // 判断有没有错误信息
        if (vo.getError().size() > 0) throw new CommonException(vo.getError().get(0));

        //校验商品售价是否低于供货价
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            //查询商品详情
            if(sku.getSupplyId() != null && sku.getSupplySkuId() != null){
                CheckOrderOfCreateEventDefiner.CheckOrderOfCreateEvent event = new CheckOrderOfCreateEventDefiner.CheckOrderOfCreateEvent();
                event.setSupplyId(sku.getSupplyId());
                event.setSupplySkuId(sku.getSupplySkuId());
                event.setGoodsMoney(vo.getBasic().getGoodsMoney());
                event.setDiscountMoney(vo.getBasic().getDeliveryMoney());
                event.setMallDiscountMoney(vo.getBasic().getMallDiscountMoney());
                event.setNum(sku.getNum());
                event.setCostPrice(vo.getBasic().getCostGoodsMoney().divide(BigDecimal.valueOf(sku.getNum()), 2, RoundingMode.HALF_UP));
                event.setGoodsName(sku.getGoods().getGoodsName());
                event.setSkuName(sku.getSkuName());
                event.setSiteId(RequestUtils.siteId());
                event.addAppSign("supply");
                List<CheckOrderOfCreateEventDefiner.CheckOrderOfCreateResult> resultList = EventAndSubscribeOfPublisher.publishAndCallback(event);
                if(!resultList.isEmpty()){
                    for (CheckOrderOfCreateEventDefiner.CheckOrderOfCreateResult item : resultList){
                        if(item == null) continue;
                        if(ObjectUtil.isNotEmpty( item.getError())){
                            throw new CommonException(item.getError());
                        }
                    }
                }
            }
        }

        vo.setOrderType("mall");

        // 检测库存
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().select("stock").eq("sku_id", sku.getSkuId()));
            if (goodsSku.getStock().compareTo(sku.getNum()) == -1) throw new CommonException("商品库存不足");
        }
        //发票创建
        vo.setMemberRemark(vo.getMemberRemark());

        Integer needTakerMobile = 0;
        if ((ObjectUtil.isNotEmpty(vo.getBasic().getVirtualVerifyMobile()) && vo.getBasic().getVirtualVerifyMobile().equals(1)) ||
                (ObjectUtil.isNotEmpty(vo.getBasic().getHasGoodsTypes()) && vo.getBasic().getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType()))){
            needTakerMobile = 1;
        }

        // 订单
        MallOrder order = new MallOrder();
        order.setOrderNo(createOrderNo());
        order.setGroupId(vo.getGroupId());
        order.setSiteId(vo.getSiteId());
        order.setSiteName(vo.getSiteInfo().getSiteName());
        order.setOrderFrom(vo.getOrderFrom());
        order.setIp(RequestUtils.ip());
        order.setOrderType("mall");
        order.setStatus(OrderStatusEnum.WAIT_PAY.getStatus().toString());
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


        if (ObjectUtil.isNotEmpty(vo.getDelivery().getTakeAddress())) {
            MemberAddress address = JSONUtil.toBean(JSONUtil.parseObj(vo.getDelivery().getTakeAddress()), MemberAddress.class);
            order.setTakerName(address.getName());
            order.setTakerMobile(needTakerMobile > 0 ? address.getMobile() : "");
            order.setTakerProvince(address.getProvinceId());
            order.setTakerCity(address.getCityId());
            order.setTakerDistrict(address.getDistrictId());
            order.setTakerAddress(address.getAddress());
            order.setTakerFullAddress(address.getFullAddress());
            order.setTakerLatitude(address.getLat());
            order.setTakerLongitude(address.getLng());
        }
        if (ObjectUtil.isNotEmpty(vo.getDelivery().getTakeStore())) {
            order.setTakeStoreId(vo.getDelivery().getTakeStore().getStoreId());
        }
        order.setCreateTime(System.currentTimeMillis() / 1000);

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
            item.setOrderGoodsMoney(sku.getGoodsMoney().subtract(sku.getDiscountMoney()).subtract(sku.getMallDiscountMoney()));
            item.setMallDiscountMoney(sku.getMallDiscountMoney());
            item.setStatus(1);
            item.setSupplyId(sku.getSupplyId());
            item.setSupplySkuId(sku.getSupplySkuId());
            item.setSupplyGoodsId(sku.getGoods().getSupplyGoodsId());
            orderGoods.add(item);
            order.setSupplyId(item.getSupplyId());
        }

        return this.createOrder(vo, order, orderGoods);
    }

    /**
     * 订单确认
     *
     * @param param
     * @return
     */
    private OrderCreateDataVo confirm(OrderCalculateParam param) {
        OrderCreateDataVo vo = new OrderCreateDataVo();
        BeanUtil.copyProperties(param, vo, "discount");
        initSite(vo);
        // 查询会员信息
        OrderCreateDataVo.Buyer buyer = new OrderCreateDataVo.Buyer();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("nickname, point").eq("member_id", param.getMemberId()));
        if (member == null) throw new CommonException("找不到买家");
        BeanUtil.copyProperties(member, buyer);
        buyer.setMemberLevel(memberLevelService.findMemberLevel(param.getMemberId()));
        vo.setBuyer(buyer);
        SiteOrderConfigVo orderConfig = coreOrderConfigService.getConfig(param.getSiteId());
        if(ObjectUtil.isNotEmpty(orderConfig.getFormId()))
        {
            DiyForm diyForm = diyFormMapper.selectOne(new QueryWrapper<DiyForm>().select("form_id").eq("form_id", orderConfig.getFormId()).eq("status", 1));
            if (diyForm != null) vo.setFormId(Integer.valueOf(orderConfig.getFormId()));
        }
        // 查询商品信息
        this.getGoodsData(vo, param);

        // 查询配送方式
        this.getDelivery(vo, param);

        //查询发票配置
        this.getConfig(vo,"invoice",null,param);

        // 查询配送方式
        this.selectTakeAddress(vo, param);
        return vo;
    }

    private void initSite(OrderCreateDataVo vo) {

        Site site = siteMapper.selectById(vo.getSiteId());
        BeanUtil.copyProperties(site, vo.getSiteInfo());
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", vo.getSiteId()));
        if(ObjectUtil.isNotEmpty(shop))
        {
            BeanUtil.copyProperties(shop, vo.getSiteInfo().getShop());
        }

    }

    /**
     * 检测限购
     * @param vo
     */
    private void checkGoodsLimitBuy(OrderCreateDataVo vo){
        String res = coreGoodsLimitBuyService.checkGoodsLimitBuy(vo.getMemberId(), vo.getLimitBuy());
        if(!res.equals("")) throw new CommonException(res);
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

        if (ObjectUtil.isNotEmpty(param.getCartIds())) {
            List<OrderHelperParam.SkuItem> skuData = CollectUtils.convert(cartMapper.selectList(new QueryWrapper<MallCart>()
                    .in("id", param.getCartIds())
                    .select("id, sku_id, num")
                    .eq("site_id", param.getSiteId())
                    .eq("member_id", param.getMemberId())), OrderHelperParam.SkuItem.class);
            param.setSkuData(skuData);
        }
        if (ObjectUtil.isNotEmpty(param.getSkuData())) {
            //订单中包含的商品形式
            Set<String> hasGoodsTypes = new HashSet<>();
            //订单中包含的配送形式
            Set<String> hasDeliveryTypes = new HashSet<>();
            Integer virtualVerifyMobile = 0;
            for (OrderHelperParam.SkuItem item : param.getSkuData()) {
                // 查询规格数据
                OrderCreateDataVo.Sku skuVo = new OrderCreateDataVo.Sku();
                MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("site_id", param.getSiteId()).eq("sku_id", item.getSkuId()));
                Assert.notNull(sku, "购物车数据已过期");

                String memberPrice = sku.getMemberPrice();
                sku.setMemberPrice(null);

                BeanUtil.copyProperties(sku, skuVo);
                skuVo.setNum(item.getNum());
                skuVo.setOriginalPrice(skuVo.getPrice());
                OrderHelperParam.SkuItem cardItem = param.getSkuData().stream().filter(s -> s.getSkuId().equals(skuVo.getSkuId())).findFirst().orElse(null);
                if(cardItem != null && ObjectUtil.isNotEmpty(cardItem.getId()))
                {
                    skuVo.setCardId(cardItem.getId());
                }else{
                    skuVo.setCardId(0L);
                }
                // 查询商品数据
                OrderCreateDataVo.Goods goodsVo = new OrderCreateDataVo.Goods();
                MallGoods goods = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("site_id", param.getSiteId()).eq("goods_id", skuVo.getGoodsId()).eq("status", 1).eq("delete_time", 0));
                Assert.notNull(goods, "购物车数据已过期");
                BeanUtil.copyProperties(goods, goodsVo);

                skuVo.setGoods(goodsVo);
                basic.setVirtualVerifyMobile(goods.getVirtualVerifyMobile());
                //查询商品限购信息
                OrderCreateDataVo.LimitBuy limitBuy = new OrderCreateDataVo.LimitBuy();
                limitBuy.setGoodsId(skuVo.getGoodsId());
                limitBuy.setSiteId(skuVo.getSiteId());
                limitBuy.setGoodsName(skuVo.getGoods().getGoodsName());
                limitBuy.setStock(skuVo.getGoods().getStock());
                limitBuy.setNum(skuVo.getNum());
                limitBuy.setIsLimit(skuVo.getGoods().getIsLimit());
                limitBuy.setLimitType(skuVo.getGoods().getLimitType());
                limitBuy.setMaxBuy(skuVo.getGoods().getMaxBuy());
                limitBuy.setMinBuy(skuVo.getGoods().getMinBuy());
                if(ObjectUtil.isEmpty(vo.getLimitBuy()))
                {
                    vo.setLimitBuy(new HashMap<>());
                }
                if(vo.getLimitBuy().containsKey("goods_" + skuVo.getGoodsId().toString()))
                {
                    limitBuy.setNum(limitBuy.getNum() + skuVo.getNum());
                }
                vo.getLimitBuy().put("goods_" + skuVo.getGoodsId().toString(), limitBuy);

                if (ObjectUtil.isNotEmpty(vo.getBuyer().getMemberLevel())) {
                    JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(memberPrice) ? JSONUtil.parseObj(memberPrice) : new JSONObject();
                    skuVo.setMemberPrice(goodsService.calculateMemberPrice(vo.getBuyer().getMemberLevel(), goods.getMemberDiscount(), memberPriceConfig, sku.getPrice()));
                    skuVo.setPrice(skuVo.getMemberPrice());
                }

                // 小计
                skuVo.setGoodsMoney(skuVo.getPrice().multiply(new BigDecimal(item.getNum())));
                skuVo.setCostGoodsMoney(skuVo.getCostPrice().multiply(new BigDecimal(item.getNum())));
                // 计算优惠信息
                GoodsMarketCalculateEventDefiner.GoodsMarketCalculateEvent event = new GoodsMarketCalculateEventDefiner.GoodsMarketCalculateEvent();
                event.addAppSign("mall");
                event.setSiteId(vo.getSiteId());
                event.setVo(vo);
                event.setSkuInfo(skuVo);
                event.setSkuData(param.getSkuData());
                List<GoodsMarketCalculateEventDefiner.GoodsMarketCalculateResult> marketData = EventAndSubscribeOfPublisher.publishAndCallback(event);

                // 累加商品总价
                basic.setGoodsMoney(basic.getGoodsMoney().add(skuVo.getGoodsMoney()));

                goodsData.put(skuVo.getSkuId(), skuVo);
                hasGoodsTypes.add(goodsVo.getGoodsType());
                if(StringUtils.isNotEmpty(goodsVo.getDeliveryType())){
                    JSONArray deliveryTypes = JSONUtil.parseArray(goodsVo.getDeliveryType());
                    deliveryTypes.forEach(info->{
                        hasDeliveryTypes.add(info.toString());
                    });
                }
                if(ObjectUtil.isNotEmpty(goodsVo.getVirtualVerifyMobile())){
                    virtualVerifyMobile = goodsVo.getVirtualVerifyMobile();
                }
                basic.setTotalNum(basic.getTotalNum() + item.getNum());
                basic.setBody(basic.getBody() + sku.getSkuName() + goods.getGoodsName());
                basic.setCostGoodsMoney(basic.getCostGoodsMoney().add(skuVo.getCostGoodsMoney()));
            }
            basic.setHasGoodsTypes(hasGoodsTypes);
            basic.setHasDeliveryTypes(hasDeliveryTypes);
            basic.setVirtualVerifyMobile(virtualVerifyMobile);
        }
        if (goodsData.size() == 0) throw new CommonException("购物车数据已过期");

        vo.setGoodsData(goodsData);
    }

    /**
     * 查询订单可用优惠券
     *
     * @param orderKey
     */
    @Override
    public List<OrderCouponListVo> getCoupon(String orderKey) {
        OrderCreateDataVo vo = loadOrderVo(orderKey, OrderCreateDataVo.class);

        List<MallCouponMember> memberCouponList = mallCouponMemberMapper.selectList(new QueryWrapper<MallCouponMember>()
                .eq("member_id", vo.getMemberId())
                .eq("site_id", vo.getSiteId())
                .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                .gt("expire_time", System.currentTimeMillis() / 1000));

        List<OrderCouponListVo> couponList = new LinkedList<>();

        if (ObjectUtil.isNotEmpty(memberCouponList)) {
            for (MallCouponMember item : memberCouponList) {
                OrderCouponListVo couponVo = new OrderCouponListVo();
                BeanUtil.copyProperties(item, couponVo);

                couponVo.setIsNormal(true);

                Boolean matchGoods = false;
                BigDecimal matchGoodsMoney = new BigDecimal(0);

                // 通用券
                if (item.getType().equals(CouponTypeEnum.ALL.getType().toString())) {
                    matchGoods = true;
                    matchGoodsMoney = vo.getBasic().getGoodsMoney();
                }
                // 品类券
                if (item.getType().equals(CouponTypeEnum.CATEGORY.getType().toString())) {
                    List<Integer> categoryIds = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                            .eq("coupon_id", item.getCouponId())
                            .select("category_id")).stream().map(i -> i.getCategoryId()).collect(Collectors.toList());

                    for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                        List<Integer> goodsCategory = JSONUtil.parseArray(sku.getGoods().getGoodsCategory()).toList(Integer.class);
                        boolean hasIntersection = goodsCategory.stream().anyMatch(categoryIds::contains);
                        if (hasIntersection) {
                            matchGoods = true;
                            matchGoodsMoney = matchGoodsMoney.add(sku.getGoodsMoney());
                        }
                    }
                }
                // 商品券
                if (item.getType().equals(CouponTypeEnum.GOODS.getType().toString())) {
                    List<Integer> goodsIds = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                            .eq("coupon_id", item.getCouponId())
                            .select("goods_id")).stream().map(i -> i.getGoodsId()).collect(Collectors.toList());

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
                } else if (matchGoodsMoney.compareTo(BigDecimal.ZERO) == 0 || matchGoodsMoney.compareTo(item.getMinConditionMoney()) == -1) {
                    couponVo.setIsNormal(false);
                    couponVo.setError("未达到最低可使用金额");
                }

                couponList.add(couponVo);
            }
        }
        return couponList;
    }

    /**
     * 金额计算
     * 将所有金额参数转换为分进行计算，最后再转换回元
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
}
