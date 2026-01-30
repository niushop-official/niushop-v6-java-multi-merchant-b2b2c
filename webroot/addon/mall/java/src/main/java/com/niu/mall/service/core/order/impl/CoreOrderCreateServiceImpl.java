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
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.event.common.CommonEventDefiner;
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
    ICoreDiyFormRecordsService coreDiyFormRecordsService;

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
    public OrderCreateDataVo calculate(OrderCalculateParam param, MallOrderCreateDataVo mallOrderCreateDataVo) {
        OrderCreateDataVo vo = null;

        vo = this.confirm(param);
        vo.setOrderFrom(param.getOrderFrom());

        // 计算优惠
        calculateDiscount(vo, param);

        // 计算运费
        calculateDelivery(vo, param, mallOrderCreateDataVo);

        //检测限购
        checkGoodsLimitBuy(vo);


        // 计算订单金额
        OrderCreateDataVo.Basic basic = vo.getBasic();
        BigDecimal orderMoney = moneyCalculate(basic.getDeliveryMoney(),basic.getGoodsMoney().subtract(basic.getDiscountMoney()));
        basic.setOrderMoney(orderMoney.max(BigDecimal.ZERO));

        cacheOrderVo(vo);
        vo.setMemberRemark(param.getMemberRemark());
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
        if (!vo.getError().isEmpty()) throw new CommonException(vo.getError().get(0));
        //校验店铺
        checkSite(vo);
        //校验商品售价是否低于供货价 和 拼团组
        CheckOrderOfCreateEventDefiner.CheckOrderOfCreateEvent event = new CheckOrderOfCreateEventDefiner.CheckOrderOfCreateEvent();
        event.setSiteId(RequestUtils.siteId());
        event.setVo(vo);
        List<CheckOrderOfCreateEventDefiner.CheckOrderOfCreateResult> resultList = EventAndSubscribeOfPublisher.publishAndCallback(event);
        if(!resultList.isEmpty()){
            for (CheckOrderOfCreateEventDefiner.CheckOrderOfCreateResult item : resultList){
                if(item == null) continue;
                if(ObjectUtil.isNotEmpty( item.getError())){
                    throw new CommonException(item.getError().get(0));
                }
            }
        }

        // 检测库存
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().select("stock,is_sell").eq("sku_id", sku.getSkuId()));
            if (!(ObjectUtil.isNotEmpty(goodsSku.getIsSell()) && goodsSku.getIsSell().equals(1))) {
                throw new CommonException("商品"+sku.getGoods().getGoodsName()+" "+sku.getSkuName()+"暂未开售");
            }
            if (goodsSku.getStock().compareTo(sku.getNum()) == -1) {
                throw new CommonException("商品"+sku.getGoods().getGoodsName()+" "+sku.getSkuName()+"库存不足");
            }
        }

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
        order.setSupplyId(vo.getSupplyId());
        order.setIsSyncSupply(vo.getSupplyId() > 0 ? 2: 1);
        order.setSupplyDeliveryMoney(vo.getSupplyId() > 0 ? vo.getBasic().getDeliveryMoney() : BigDecimal.ZERO);

        if (ObjectUtil.isNotEmpty(vo.getDelivery().getTakeAddress())) {
            MemberAddress address = JSONUtil.toBean(JSONUtil.parseObj(vo.getDelivery().getTakeAddress()), MemberAddress.class);
            order.setTakerName(address.getName());
            order.setTakerMobile(needTakerMobile > 0 ? address.getMobile() : "");
            order.setTakerProvince(address.getProvinceId());
            if (ObjectUtil.isNotEmpty(vo.getLocalDeliveryType()) && vo.getLocalDeliveryType().equals("now")){
                order.setBuyerAskDeliveryTime(vo.getBuyerAskDeliveryTime());
            }else{
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
            item.setOrderGoodsMoney(sku.getGoodsMoney().subtract(sku.getDiscountMoney()).subtract(sku.getMallDiscountMoney()));
            item.setMallDiscountMoney(sku.getMallDiscountMoney());
            item.setStatus(1);
            item.setSupplyId(sku.getSupplyId());
            item.setSupplySkuId(sku.getSupplySkuId());
            item.setSupplyGoodsId(sku.getGoods().getSupplyGoodsId());
            orderGoods.add(item);

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
        vo.setSiteId(param.getSiteId());
        vo.setMemberId(param.getMemberId());
        vo.setOrderType(param.getOrderType());
        vo.setMemberRemark(param.getMemberRemark());
        vo.setOrderFrom(param.getOrderFrom());
        vo.setExtendData(param.getExtendData());
        //查询配送配置
        this.getConfig(vo,"delivery_type",null,param);
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
        //this.selectTakeAddress(vo, param);
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
     * 校验店铺
     */
    private void checkSite(OrderCreateDataVo vo) {
        Site site = siteMapper.selectById(vo.getSiteId());
        BeanUtil.copyProperties(site, vo.getSiteInfo());
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", vo.getSiteId()));
        if(ObjectUtil.isNotEmpty(shop) && ObjectUtil.isNotEmpty(site) && ShopStatusEnum.CLOSE.getCode().equals(shop.getBusinessStatus()))
        {
            BeanUtil.copyProperties(shop, vo.getSiteInfo().getShop());
            throw new CommonException("店铺"+site.getSiteName()+ShopStatusEnum.CLOSE.getName());
        }
    }

    /**
     * 检测限购
     * @param vo
     */
    private void checkGoodsLimitBuy(OrderCreateDataVo vo){
        String res = coreGoodsLimitBuyService.checkGoodsLimitBuy(vo.getMemberId(), vo);
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
        Map<Integer,Long> skuCartData = new HashMap<>();
        if (ObjectUtil.isNotEmpty(param.getCartIds())) {
            List<OrderHelperParam.SkuItem> skuData = CollectUtils.convert(cartMapper.selectList(new QueryWrapper<MallCart>()
                    .in("id", param.getCartIds())
                    .select("id,goods_id, site_id, sku_id, num, market_type, market_type_id") ), OrderHelperParam.SkuItem.class);
            if(skuData.isEmpty()){
                throw new CommonException("购物车数据已过期");
            }
            if(skuData.size() != param.getCartIds().size()){
                throw new CommonException("购物车数据已过期");
            }
            param.setSkuData(skuData);
            skuCartData = skuData.stream().collect(Collectors.toMap(OrderHelperParam.SkuItem::getSkuId,OrderHelperParam.SkuItem::getId));
        }else{
            if(param.getSkuData().isEmpty()){
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
        BigDecimal orderMoney = BigDecimal.ZERO,goodsMoney = BigDecimal.ZERO,deliveryMoney = BigDecimal.ZERO,costGoodsMoney = BigDecimal.ZERO;
        String body ="";
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
