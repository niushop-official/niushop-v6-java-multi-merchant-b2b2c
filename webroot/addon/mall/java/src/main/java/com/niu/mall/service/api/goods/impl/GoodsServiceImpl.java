package com.niu.mall.service.api.goods.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.image.ImageUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.goods.GoodsBrand;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.shop.ShopStatusEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.event.shop.MallShopGoodsListEventDefiner;
import com.niu.core.mapper.goods.GoodsBrandMapper;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.mapper.shop.SiteShopCategoryMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.diy.param.DiyInfoParam;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.delivery.ICoreConfigService;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.goods.ICoreGoodsCategoryService;
import com.niu.mall.entity.coupon.MallCoupon;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.goods.*;
import com.niu.mall.entity.order.config.MallOrderEvaluate;
import com.niu.mall.enums.active.ActiveTypeEnum;
import com.niu.mall.enums.coupon.CouponIsAllSiteEnum;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.mapper.coupon.MallCouponGoodsMapper;
import com.niu.mall.mapper.coupon.MallCouponMapper;
import com.niu.mall.mapper.goods.*;
import com.niu.mall.service.admin.marketing.IPlatformCouponService;
import com.niu.mall.service.api.goods.IGoodsService;
import com.niu.mall.service.api.goods.param.MallGoodsComponentsSearchParam;
import com.niu.mall.service.api.goods.param.MallGoodsDetailParam;
import com.niu.mall.service.api.goods.param.MallGoodsSearchParam;
import com.niu.mall.service.api.goods.param.ShopComponentsSearchParam;
import com.niu.mall.service.api.goods.vo.GoodsLabelListVo;
import com.niu.mall.service.api.goods.vo.MallGoodsDetailVo;
import com.niu.mall.service.api.goods.vo.MallGoodsListVo;
import com.niu.mall.service.api.goods.vo.MallGoodsSkuVo;
import com.niu.mall.service.api.marketing.IMallDiscountService;
import com.niu.mall.service.core.delivery.ICoreLocalDeliveryService;
import com.niu.mall.service.core.delivery.impl.CoreLocalDeliveryServiceImpl;
import com.niu.mall.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.mall.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GetShowPriceParam;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import com.niu.mall.service.core.goods.vo.ShowPriceVo;
import com.niu.mall.service.core.order.ICoreOrderConfigService;
import com.niu.mall.service.site.marketing.ICouponService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoodsServiceImpl implements IGoodsService {

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    @Resource
    MallGoodsSpecMapper mallGoodsSpecMapper;

    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    GoodsServiceMapper mallGoodsServiceMapper;

    @Resource
    MallGoodsLabelMapper mallGoodsLabelMapper;

    @Resource
    GoodsBrandMapper goodsBrandMapper;

    @Resource
    IDiyService diyService;

    @Resource
    MallGoodsCollectMapper mallGoodsCollectMapper;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    @Resource
    MallCouponGoodsMapper mallCouponGoodsMapper;

    @Resource
    MallCouponMapper mallCouponMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    SiteShopMapper siteShopMapper;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Resource
    IPlatformCouponService platformCouponService;

    @Resource
    ICouponService couponService;

    @Resource
    SiteShopCategoryMapper siteShopCategoryMapper;

    @Resource
    MallGoodsMapper goodsMapper;

    @Resource
    MallGoodsSkuMapper goodsSkuMapper;

    @Resource
    IMallDiscountService mallDiscountService;

    @Resource
    ICoreGoodsCategoryService coreGoodsCategoryService;

    @Resource
    ICoreGoodsActivePriceService coreGoodsActivePriceService;

    @Resource
    ICoreGoodsLimitBuyService coreGoodsLimitBuyService;

    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;

    @Resource
    ICoreLocalDeliveryService coreLocalDeliveryService;

    /**
     * 查询商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public PageResult<MallGoodsListVo> page(PageParam pageParam, MallGoodsSearchParam searchParam,Map<String, String>  allParams) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_goods_brand sgb ON g.brand_id = sgb.brand_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site ns ON g.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop nss ON g.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num + g.virtual_sale_num as sale_num," +
                "g.status,g.sort,g.create_time,member_discount,virtual_verify_type,unit," +
                "is_discount,label_ids,g.brand_id,sgb.brand_name,sgb.logo, ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");
        AdminDeliveryConfigVo deliveryConfig = coreDeliveryConfigService.getAdminDeliveryConfig();
        if (ObjectUtil.isNotEmpty(allParams)){
            String latitude = StringUtils.isNotEmpty(allParams.get("position[latitude]")) ? allParams.get("position[latitude]") : "";
            String longitude = StringUtils.isNotEmpty(allParams.get("position[longitude]")) ? allParams.get("position[longitude]") : "";
            if(StringUtils.isNotEmpty(latitude) && StringUtils.isNotEmpty(longitude) && deliveryConfig.getIsShow() == 1){
                String siteField = "ns.latitude,ns.longitude";
                siteField += ", ROUND(6371 * acos( "+
                "cos(radians("+latitude+")) "+
                "        * cos(radians(ns.latitude)) "+
                "        * cos(radians(ns.longitude) - radians("+longitude+")) "+
                "        + sin(radians("+latitude+")) "+
                "        * sin(radians(ns.latitude)) "+
                "  ), 2) AS distance";
                queryWrapper.select(siteField);
            }
        }

        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.delete_time", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) queryWrapper.like("goods_name", searchParam.getKeyword());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandId())) queryWrapper.eq("brand_id", searchParam.getBrandId());
        if (!searchParam.getSiteId().equals(0)) queryWrapper.eq("g.site_id", searchParam.getSiteId());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsMallCategory())){
            queryWrapper.in("g.goods_mall_category",coreGoodsCategoryService.getGoodsMallCategoryIdsChild(Integer.valueOf(searchParam.getGoodsMallCategory())));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory())){
            queryWrapper.like("g.goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        }
        if (ObjectUtil.isNotEmpty(searchParam.getLabelIds()))
            queryWrapper.like("label_ids", "\"" + searchParam.getLabelIds() + "\"");
        QueryMapperUtils.buildByBetween(queryWrapper, "gs.price", new Double[]{searchParam.getStartPrice(), searchParam.getEndPrice()});

        if (ObjectUtil.isNotEmpty(searchParam.getOrder()) && (searchParam.getOrder().contains("sale_num") || searchParam.getOrder().contains("price"))) {
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        } else {
            queryWrapper.orderBy(true, false, "g.sort");
            queryWrapper.orderBy(true, false, "g.create_time");
        }

        if (searchParam.getCouponId() != null && searchParam.getCouponId() > 0) {
            List<MallCouponGoods> couponGoodsList = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                    .eq("coupon_id", searchParam.getCouponId())
                    .select("category_id,goods_id"));
            List<Integer> goodsIds = couponGoodsList.stream().filter(i -> i.getGoodsId() > 0).map(i -> i.getGoodsId()).collect(Collectors.toList());
            List<Integer> categoryIds = couponGoodsList.stream().filter(i -> i.getCategoryId() > 0).map(i -> i.getCategoryId()).collect(Collectors.toList());
            if (goodsIds.size() > 0) {
                queryWrapper.in("g.goods_id", goodsIds);
            } else if (categoryIds.size() > 0) {
                queryWrapper.and(i -> {
                    for (int index = 0; index < categoryIds.size(); index++) {
                        Integer categoryId = categoryIds.get(index);
                        if (index == 0) {
                            i.like("goods_category", "\"" + categoryId + "\"");
                        } else {
                            i.or().like("goods_category", "\"" + categoryId + "\"");
                        }
                    }
                });
            }
        }

        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        IPage<MallGoodsListVo> iPage = mallGoodsMapper.selectJoinPage(new Page<>(page, limit), MallGoodsListVo.class, queryWrapper);
        List<MallGoodsListVo> list = new LinkedList<>();
        for (MallGoodsListVo vo : iPage.getRecords()) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? JSONUtil.parseObj(goodsSku.getMemberPrice()) : new JSONObject();
                goodsSkuVo.setMemberPrice(calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, goodsSku.getPrice()));
            } else {
                goodsSkuVo.setMemberPrice(goodsSku.getPrice());
            }
            //设置展示价格
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(goodsSkuVo.getGoodsId())
                    .salePrice(goodsSkuVo.getSalePrice())
                    .skuId(goodsSkuVo.getSkuId())
                    .price(goodsSkuVo.getPrice())
                    .memberDiscount(vo.getMemberDiscount())
                    .memberPrice(ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice() : null)
                    .memberId(RequestUtils.memberId())
                    .build());
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsLabelName(getGoodsLabelName(vo.getLabelIds()));
            if (vo.getBrandId() > 0) {
                MallGoodsListVo.Brand brand = new MallGoodsListVo.Brand();
                brand.setBrandId(vo.getBrandId());
                brand.setBrandName(vo.getBrandName());
                brand.setLogo(vo.getLogo());
                vo.setGoodsBrand(brand);
            }
            MallGoodsListVo.ShopInfo shopInfo = new MallGoodsListVo.ShopInfo();
            shopInfo.setSiteId(vo.getSiteId());
            shopInfo.setIsSelf(vo.getIsSelf());
            MallGoodsListVo.SiteInfo siteInfo = new MallGoodsListVo.SiteInfo();
            siteInfo.setSiteId(vo.getSiteId());
            siteInfo.setSiteName(vo.getSiteName());
            vo.setShop(shopInfo);
            vo.setSite(siteInfo);
            Integer hasBuy = coreGoodsLimitBuyService.getGoodsHasBuyNumber(vo.getSiteId(), RequestUtils.memberId(), vo.getGoodsId());
            vo.setHasBuy(hasBuy);

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public PageResult<MallGoodsListVo> shopPage(PageParam pageParam, MallGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site ns ON g.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop nss ON g.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.status,g.stock,g.sale_num + g.virtual_sale_num as sale_num,g.sort" +
                ",g.create_time,member_discount,virtual_verify_type,unit,is_discount,label_ids,g.stock,g.brand_id, ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.delete_time", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.like("g.goods_name", searchParam.getKeyword());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getBrandId())) {
            queryWrapper.eq("g.brand_id", searchParam.getBrandId());
        }
        if (!searchParam.getSiteId().equals(0)){
            queryWrapper.eq("g.site_id", searchParam.getSiteId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsMallCategory())){
            queryWrapper.in("g.goods_mall_category",coreGoodsCategoryService.getGoodsMallCategoryIdsChild(Integer.valueOf(searchParam.getGoodsMallCategory())));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory())){
            queryWrapper.like("g.goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        }
        if (ObjectUtil.isNotEmpty(searchParam.getLabelIds())){
            queryWrapper.like("label_ids", "\"" + searchParam.getLabelIds() + "\"");
        }
        QueryMapperUtils.buildByBetween(queryWrapper, "gs.price", new Double[]{searchParam.getStartPrice(), searchParam.getEndPrice()});

        if (ObjectUtil.isNotEmpty(searchParam.getOrder())) {
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        } else {
            queryWrapper.orderBy(true, false, "g.sort");
            queryWrapper.orderBy(true, false, "g.create_time");
        }

        if (searchParam.getCouponId() != null && searchParam.getCouponId() > 0) {
            List<MallCouponGoods> couponGoodsList = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                    .eq("coupon_id", searchParam.getCouponId())
                    .select("category_id,goods_id"));
            List<Integer> goodsIds = couponGoodsList.stream().filter(i -> i.getGoodsId() > 0).map(i -> i.getGoodsId()).collect(Collectors.toList());
            List<Integer> categoryIds = couponGoodsList.stream().filter(i -> i.getCategoryId() > 0).map(i -> i.getCategoryId()).collect(Collectors.toList());
            if (goodsIds.size() > 0) {
                queryWrapper.in("g.goods_id", goodsIds);
            } else if (categoryIds.size() > 0) {
                queryWrapper.and(i -> {
                    for (int index = 0; index < categoryIds.size(); index++) {
                        Integer categoryId = categoryIds.get(index);
                        if (index == 0) {
                            i.like("goods_category", "\"" + categoryId + "\"");
                        } else {
                            i.or().like("goods_category", "\"" + categoryId + "\"");
                        }
                    }
                });
            }
        }

        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        IPage<MallGoodsListVo> iPage = mallGoodsMapper.selectJoinPage(new Page<>(page, limit), MallGoodsListVo.class, queryWrapper);
        List<MallGoodsListVo> list = new LinkedList<>();
        for (MallGoodsListVo vo : iPage.getRecords()) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? JSONUtil.parseObj(goodsSku.getMemberPrice()) : new JSONObject();
                goodsSkuVo.setMemberPrice(calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, goodsSku.getPrice()));
            } else {
                goodsSkuVo.setMemberPrice(goodsSku.getPrice());
            }
            //设置展示价格
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(goodsSkuVo.getGoodsId())
                    .salePrice(goodsSkuVo.getSalePrice())
                    .skuId(goodsSkuVo.getSkuId())
                    .price(goodsSkuVo.getPrice())
                    .memberDiscount(vo.getMemberDiscount())
                    .memberPrice(ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice().toString() : null)
                    .memberId(RequestUtils.memberId())
                    .build());
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsLabelName(getGoodsLabelName(vo.getLabelIds()));
            if (vo.getBrandId() > 0) {
                MallGoodsListVo.Brand brand = new MallGoodsListVo.Brand();
                BeanUtils.copyProperties(vo, brand);
                vo.setGoodsBrand(brand);
            }
            MallGoodsListVo.ShopInfo shopInfo = new MallGoodsListVo.ShopInfo();
            shopInfo.setSiteId(vo.getSiteId());
            shopInfo.setIsSelf(vo.getIsSelf());
            MallGoodsListVo.SiteInfo siteInfo = new MallGoodsListVo.SiteInfo();
            siteInfo.setSiteId(vo.getSiteId());
            siteInfo.setSiteName(vo.getSiteName());
            vo.setShop(shopInfo);
            vo.setSite(siteInfo);

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public PageResult<MallGoodsListVo> getCouponGoodsPage(PageParam pageParam, MallGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_goods_brand sgb ON g.brand_id = sgb.brand_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site ns ON g.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop nss ON g.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.select("g.site_id,g.goods_id,g.goods_name,g.sub_title,g.goods_category,g.goods_type,g.goods_cover,g.unit,g.status,g.sale_num + g.virtual_sale_num as sale_num,g.member_discount,g.is_discount,g.virtual_receive_type,g.label_ids,g.brand_id,sgb.brand_name,sgb.logo,ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");

        // 基础条件
        queryWrapper.eq("gs.is_default", 1);
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.delete_time", 0);
        queryWrapper.eq("nss.business_status", SiteStatusEnum.ON.getCode());

        // 搜索条件
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper.like("g.goods_name", searchParam.getKeyword())
                    .or().like("g.sub_title", searchParam.getKeyword()));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getBrandId())) {
            queryWrapper.eq("g.brand_id", searchParam.getBrandId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getSiteId()) && searchParam.getSiteId() > 0) {
            queryWrapper.eq("g.site_id", searchParam.getSiteId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsMallCategory())) {
            queryWrapper.in("g.goods_mall_category", coreGoodsCategoryService.getGoodsMallCategoryIdsChild(Integer.valueOf(searchParam.getGoodsMallCategory())));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory())) {
            queryWrapper.like("g.goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        }
        if (ObjectUtil.isNotEmpty(searchParam.getLabelIds())) {
            queryWrapper.like("g.label_ids", "\"" + searchParam.getLabelIds() + "\"");
        }

        // 价格区间处理
        Double startPrice = null;
        Double endPrice = null;
        if (ObjectUtil.isNotEmpty(searchParam.getStartPrice())) {
            try {
                startPrice = Double.parseDouble(String.valueOf(searchParam.getStartPrice()));
            } catch (NumberFormatException e) {
                startPrice = null;
            }
        }
        if (ObjectUtil.isNotEmpty(searchParam.getEndPrice())) {
            try {
                endPrice = Double.parseDouble(String.valueOf(searchParam.getEndPrice()));
            } catch (NumberFormatException e) {
                endPrice = null;
            }
        }
        QueryMapperUtils.buildByBetween(queryWrapper, "gs.price", new Double[]{startPrice, endPrice});

        if (ObjectUtil.isNotEmpty(searchParam.getOrder()) &&
            ("sale_num".equals(searchParam.getOrder()) || "price".equals(searchParam.getOrder()))) {
            String orderColumn = "price".equals(searchParam.getOrder()) ? "gs.price" : searchParam.getOrder();
            queryWrapper.orderBy(true, "asc".equalsIgnoreCase(searchParam.getSort()), orderColumn);
        } else {
            queryWrapper.orderByDesc("g.create_time");
        }

        if (searchParam.getCouponId() != null && searchParam.getCouponId() > 0) {
            MallCoupon coupon = mallCouponMapper.selectOne(new QueryWrapper<MallCoupon>()
                    .eq("id", searchParam.getCouponId())
                    .select("site_id,is_all_site_join,join_site_ids"));
            List<MallCouponGoods> couponGoodsList = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                    .eq("coupon_id", searchParam.getCouponId())
                    .select("goods_id,category_id,mall_category_id,brand_id"));
            
            if (CollectionUtil.isNotEmpty(couponGoodsList)) {
                List<Integer> goodsIds = couponGoodsList.stream()
                        .filter(i -> ObjectUtil.isNotEmpty(i.getGoodsId()) && i.getGoodsId() > 0)
                        .map(MallCouponGoods::getGoodsId)
                        .collect(Collectors.toList());
                List<Integer> categoryIds = couponGoodsList.stream()
                        .filter(i -> ObjectUtil.isNotEmpty(i.getCategoryId()) && i.getCategoryId() > 0)
                        .map(MallCouponGoods::getCategoryId)
                        .collect(Collectors.toList());
                List<Integer> mallCategoryIds = couponGoodsList.stream()
                        .filter(i -> ObjectUtil.isNotEmpty(i.getMallCategoryId()) && i.getMallCategoryId() > 0)
                        .map(MallCouponGoods::getMallCategoryId)
                        .collect(Collectors.toList());
                List<Integer> brandIds = couponGoodsList.stream()
                        .filter(i -> ObjectUtil.isNotEmpty(i.getBrandId()) && i.getBrandId() > 0)
                        .map(MallCouponGoods::getBrandId)
                        .collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(goodsIds)) {
                    queryWrapper.in("g.goods_id", goodsIds);
                } else if (CollectionUtil.isNotEmpty(categoryIds)) {
                    queryWrapper.and(wrapper -> {
                        for (int index = 0; index < categoryIds.size(); index++) {
                            Integer categoryId = categoryIds.get(index);
                            if (index == 0) {
                                wrapper.like("g.goods_category", "\"" + categoryId + "\"");
                            } else {
                                wrapper.or().like("g.goods_category", "\"" + categoryId + "\"");
                            }
                        }
                    });
                } else if (CollectionUtil.isNotEmpty(mallCategoryIds)) {
                    queryWrapper.in("g.goods_mall_category", mallCategoryIds);
                } else if (CollectionUtil.isNotEmpty(brandIds)) {
                    queryWrapper.in("g.brand_id", brandIds);
                }
            }
            
            if (ObjectUtil.isNotEmpty(coupon)) {
                if (ObjectUtil.isNotEmpty(coupon.getSiteId()) && coupon.getSiteId() > 0) {
                    queryWrapper.eq("g.site_id", coupon.getSiteId());
                } else {
                    if (ObjectUtil.isNotEmpty(coupon.getIsAllSiteJoin()) 
                            && coupon.getIsAllSiteJoin().equals(CouponIsAllSiteEnum.NOT_ALL_SITE_JOIN.getType())
                            && ObjectUtil.isNotEmpty(coupon.getJoinSiteIds())) {
                        List<Integer> siteIdList = new ArrayList<>();
                        try {
                            JSONArray siteIdJson = JSONUtil.parseArray(coupon.getJoinSiteIds());
                            if (ObjectUtil.isNotEmpty(siteIdJson)) {
                                siteIdList.addAll(siteIdJson.toList(Integer.class));
                            }
                        } catch (Exception e) {
                            String siteIds = coupon.getJoinSiteIds().replace("[", "").replace("]", "");
                            String[] siteIdArray = siteIds.split(",");
                            for (String siteIdStr : siteIdArray) {
                                if (siteIdStr != null && !siteIdStr.trim().isEmpty()) {
                                    try {
                                        siteIdList.add(Integer.valueOf(siteIdStr.trim()));
                                    } catch (NumberFormatException ex) {
                                    }
                                }
                            }
                        }
                        if (CollectionUtil.isNotEmpty(siteIdList)) {
                            queryWrapper.in("g.site_id", siteIdList);
                        }
                    }
                }
            }
        }

        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        IPage<MallGoodsListVo> iPage = mallGoodsMapper.selectJoinPage(new Page<>(page, limit), MallGoodsListVo.class, queryWrapper);
        List<MallGoodsListVo> list = new LinkedList<>();
        for (MallGoodsListVo vo : iPage.getRecords()) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            if (goodsSku == null) {
                continue;
            }
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            goodsSkuVo.setMemberDiscount(vo.getMemberDiscount());
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? JSONUtil.parseObj(goodsSku.getMemberPrice()) : new JSONObject();
                goodsSkuVo.setMemberPrice(calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, goodsSku.getPrice()));
            } else {
                goodsSkuVo.setMemberPrice(goodsSku.getPrice());
            }
            //设置展示价格
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(goodsSkuVo.getGoodsId())
                    .salePrice(goodsSkuVo.getSalePrice())
                    .skuId(goodsSkuVo.getSkuId())
                    .price(goodsSkuVo.getPrice())
                    .memberDiscount(vo.getMemberDiscount())
                    .memberPrice(ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice().toString() : null)
                    .memberId(RequestUtils.memberId())
                    .build());
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsLabelName(getGoodsLabelName(vo.getLabelIds()));
            if (vo.getBrandId() != null && vo.getBrandId() > 0) {
                MallGoodsListVo.Brand brand = new MallGoodsListVo.Brand();
                brand.setBrandId(vo.getBrandId());
                brand.setBrandName(vo.getBrandName());
                brand.setLogo(vo.getLogo());
                vo.setGoodsBrand(brand);
            }
            MallGoodsListVo.ShopInfo shopInfo = new MallGoodsListVo.ShopInfo();
            shopInfo.setSiteId(vo.getSiteId());
            shopInfo.setIsSelf(vo.getIsSelf());
            MallGoodsListVo.SiteInfo siteInfo = new MallGoodsListVo.SiteInfo();
            siteInfo.setSiteId(vo.getSiteId());
            siteInfo.setSiteName(vo.getSiteName());
            vo.setShop(shopInfo);
            vo.setSite(siteInfo);

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<MallGoodsListVo> getRecommendList(Integer siteId, Integer num) {
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site ns ON g.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop nss ON g.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num + g.virtual_sale_num as sale_num," +
                "g.status,g.sort,g.create_time,member_discount,virtual_verify_type,unit,is_discount,label_ids,g.stock,g.brand_id, ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.delete_time", 0);
        queryWrapper.eq("g.site_id", siteId);
        queryWrapper.orderBy(true, false, "g.sort");
        queryWrapper.orderBy(true, false, "g.create_time");

        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        List<MallGoodsListVo> list = mallGoodsMapper.selectJoinList(MallGoodsListVo.class, queryWrapper);
        for (MallGoodsListVo vo : list) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? JSONUtil.parseObj(goodsSku.getMemberPrice()) : new JSONObject();
                goodsSkuVo.setMemberPrice(calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, goodsSku.getPrice()));
            } else {
                goodsSkuVo.setMemberPrice(goodsSku.getPrice());
            }
            //设置展示价格
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(goodsSkuVo.getGoodsId())
                    .salePrice(goodsSkuVo.getSalePrice())
                    .skuId(goodsSkuVo.getSkuId())
                    .price(goodsSkuVo.getPrice())
                    .memberDiscount(vo.getMemberDiscount())
                    .memberPrice(ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice().toString() : null)
                    .memberId(RequestUtils.memberId())
                    .build());
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsLabelName(getGoodsLabelName(vo.getLabelIds()));
            if (vo.getBrandId() > 0) {
                MallGoodsListVo.Brand brand = new MallGoodsListVo.Brand();
                BeanUtils.copyProperties(vo, brand);
                vo.setGoodsBrand(brand);
            }
            MallGoodsListVo.ShopInfo shopInfo = new MallGoodsListVo.ShopInfo();
            shopInfo.setSiteId(vo.getSiteId());
            shopInfo.setIsSelf(vo.getIsSelf());
            MallGoodsListVo.SiteInfo siteInfo = new MallGoodsListVo.SiteInfo();
            siteInfo.setSiteId(vo.getSiteId());
            siteInfo.setSiteName(vo.getSiteName());
            vo.setShop(shopInfo);
            vo.setSite(siteInfo);
        }
        return list;
    }

    @Override
    public List<MallGoodsListVo> getGoodsList(MallGoodsSearchParam searchParam) {
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site ns ON g.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop nss ON g.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num + g.virtual_sale_num as sale_num,status,g.sort,g.create_time,member_discount,virtual_verify_type,unit,is_discount,label_ids,g.stock,g.brand_id, ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");

        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.delete_time", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) queryWrapper.like("goods_name", searchParam.getKeyword());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandId())) queryWrapper.eq("brand_id", searchParam.getBrandId());
        if (!searchParam.getSiteId().equals(0)) queryWrapper.eq("g.site_id", searchParam.getSiteId());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsMallCategory())){
            queryWrapper.in("g.goods_mall_category",coreGoodsCategoryService.getGoodsMallCategoryIdsChild(Integer.valueOf(searchParam.getGoodsMallCategory())));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory())){
            queryWrapper.like("g.goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        }
            queryWrapper.like("label_ids", "\"" + searchParam.getLabelIds() + "\"");
        QueryMapperUtils.buildByBetween(queryWrapper, "gs.price", new Double[]{searchParam.getStartPrice(), searchParam.getEndPrice()});

        if (ObjectUtil.isNotEmpty(searchParam.getOrder())) {
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        } else {
            queryWrapper.orderBy(true, false, "g.sort");
            queryWrapper.orderBy(true, false, "g.create_time");
        }

        if (searchParam.getCouponId() != null && searchParam.getCouponId() > 0) {
            List<MallCouponGoods> couponGoodsList = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>()
                    .eq("coupon_id", searchParam.getCouponId())
                    .select("category_id,goods_id"));
            List<Integer> goodsIds = couponGoodsList.stream().filter(i -> i.getGoodsId() > 0).map(i -> i.getGoodsId()).collect(Collectors.toList());
            List<Integer> categoryIds = couponGoodsList.stream().filter(i -> i.getCategoryId() > 0).map(i -> i.getCategoryId()).collect(Collectors.toList());
            if (goodsIds.size() > 0) {
                queryWrapper.in("g.goods_id", goodsIds);
            } else if (categoryIds.size() > 0) {
                queryWrapper.and(i -> {
                    for (int index = 0; index < categoryIds.size(); index++) {
                        Integer categoryId = categoryIds.get(index);
                        if (index == 0) {
                            i.like("goods_mall_category", "\"" + categoryId + "\"");
                        } else {
                            i.or().like("goods_mall_category", "\"" + categoryId + "\"");
                        }
                    }
                });
            }
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsIds())) {
            queryWrapper.in("g.goods_id", searchParam.getGoodsIds());
        }

        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        List<MallGoodsListVo> list = mallGoodsMapper.selectJoinList(MallGoodsListVo.class, queryWrapper);

        for (MallGoodsListVo vo : list) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            //设置展示价格
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(goodsSkuVo.getGoodsId())
                    .salePrice(goodsSkuVo.getSalePrice())
                    .skuId(goodsSkuVo.getSkuId())
                    .price(goodsSkuVo.getPrice())
                    .memberDiscount(vo.getMemberDiscount())
                    .memberPrice(ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSkuVo.getMemberPrice().toString() : null)
                    .memberId(RequestUtils.memberId())
                    .build());
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsLabelName(getGoodsLabelName(vo.getLabelIds()));
            if (vo.getBrandId() > 0) {
                MallGoodsListVo.Brand brand = new MallGoodsListVo.Brand();
                BeanUtils.copyProperties(vo, brand);
                vo.setGoodsBrand(brand);
            }
            MallGoodsListVo.ShopInfo shopInfo = new MallGoodsListVo.ShopInfo();
            shopInfo.setSiteId(vo.getSiteId());
            shopInfo.setIsSelf(vo.getIsSelf());
            MallGoodsListVo.SiteInfo siteInfo = new MallGoodsListVo.SiteInfo();
            siteInfo.setSiteId(vo.getSiteId());
            siteInfo.setSiteName(vo.getSiteName());
            vo.setShop(shopInfo);
            vo.setSite(siteInfo);
        }
        return list;
    }

    /**
     * 计算会员价
     *
     * @param memberLevel   会员等级
     * @param calculateType 计算方式
     * @param config        会员价配置
     * @param price         商品价格
     * @return
     */
    public BigDecimal calculateMemberPrice(MemberLevelInfoVo memberLevel, String calculateType, JSONObject config, BigDecimal price) {
        return coreGoodsActivePriceService.calculateMemberPrice(memberLevel, calculateType, config, price);
    }

    /**
     * 获取商品列表供组件调用
     *
     * @param param
     */
    @Override
    public List<MallGoodsListVo> getGoodsComponents(MallGoodsComponentsSearchParam param,  Map<String, String> allParams) {
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("goods")
                .leftJoin("?_mall_goods_sku goodsSku ON goods.goods_id = goodsSku.goods_id and goodsSku.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site ns ON goods.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop shop ON goods.site_id = shop.site_id".replace("?_", GlobalConfig.tablePrefix));
        
        queryWrapper.select("goods.goods_id, goods.site_id, goods.goods_name, goods.sub_title, goods.goods_mall_category, goods.goods_category, goods.goods_type, goods.goods_cover, goods.unit, goods.status, goods.sale_num + goods.virtual_sale_num as sale_num, goods.is_discount, goods.member_discount, goods.label_ids, goods.brand_id, shop.is_self, goodsSku.price,goods.max_buy,goods.min_buy,goods.is_limit,goods.limit_type");

        if (ObjectUtil.isNotEmpty(allParams) && allParams.containsKey("position[latitude]") && allParams.containsKey("position[longitude]")){
            AdminDeliveryConfigVo deliveryConfig = coreDeliveryConfigService.getAdminDeliveryConfig();
            if(deliveryConfig.getIsShow() == 1){
                String latitude = allParams.get("position[latitude]");
                String longitude = allParams.get("position[longitude]");
                String siteField = "ns.latitude,ns.longitude";
                siteField += ", ROUND(6371 * acos( "+
                        "cos(radians("+latitude+")) "+
                        "        * cos(radians(ns.latitude)) "+
                        "        * cos(radians(ns.longitude) - radians("+longitude+")) "+
                        "        + sin(radians("+latitude+")) "+
                        "        * sin(radians(ns.latitude)) "+
                        "  ), 2) AS distance";
                queryWrapper.select(siteField);
            }
        }

        queryWrapper.eq("goodsSku.is_default", 1);
        queryWrapper.eq("goods.status", GoodsStatusEnum.ONLINE.getStatus());
        queryWrapper.eq("shop.business_status", ShopStatusEnum.ON.getCode());

        if(ObjectUtil.isNotEmpty(param.getSiteId()) && param.getSiteId() > 0){
            queryWrapper.eq("goods.site_id", param.getSiteId());
        }else{
            List<Integer> siteIds = new ArrayList<>();
            MPJQueryWrapper<Site> siteQuery = new MPJQueryWrapper<>();
            siteQuery.setAlias("ns")
                    .leftJoin("?_site_shop shop ON ns.site_id = shop.site_id".replace("?_", GlobalConfig.tablePrefix));
            siteQuery.select("ns.site_id");
            //是否支持定位
            if(ObjectUtil.isNotEmpty(param.getIsLocation()) && param.getIsLocation() > 0 && allParams.containsKey("position[province_id]") &&
                    allParams.containsKey("position[city_id]") && allParams.containsKey("position[district_id]")){
                String provinceId = allParams.get("position[province_id]");
                String cityId = allParams.get("position[city_id]");
                String districtId = allParams.get("position[district_id]");
                siteQuery.eq("ns.province_id", provinceId);
                siteQuery.eq("ns.city_id", cityId);
                siteQuery.eq("ns.district_id", districtId);
                siteQuery.ne("ns.app_type", "admin");
                siteQuery.eq("ns.status", ShopStatusEnum.ON.getCode());
                siteQuery.eq("shop.business_status", ShopStatusEnum.ON.getCode());
            }

            //是否支持配送
            if (ObjectUtil.isEmpty(param.getIsLocalDelivery()) || (ObjectUtil.isNotEmpty(param.getIsLocalDelivery()) && param.getIsLocalDelivery() == 0) ){
                List<Site> siteList = siteMapper.selectJoinList(Site.class, siteQuery);
                siteIds = siteList.stream().map(Site::getSiteId).toList();
            }else{
                siteQuery.eq("ns.is_local_delivery", 1);
                List<Site> siteList = siteMapper.selectJoinList(Site.class, siteQuery);
                if(allParams.containsKey("position[latitude]") && allParams.containsKey("position[longitude]")){
                    String latitude = allParams.get("position[latitude]");
                    String longitude = allParams.get("position[longitude]");
                    siteIds = coreLocalDeliveryService.checkLocationInArea(siteList, latitude, longitude);
                }
            }
            if(!siteIds.isEmpty()){
                queryWrapper.in("goods.site_id", siteIds);
            }

        }

        if (ObjectUtil.isNotEmpty(param.getGoodsIds()))
            queryWrapper.in("goods.goods_id", param.getGoodsIds());


        
        if (ObjectUtil.isNotEmpty(param.getGoodsCategory()) && param.getGoodsCategory() > 0)
            queryWrapper.like("goods.goods_category", "\"" + param.getGoodsCategory() + "\"");
        
        if (ObjectUtil.isNotEmpty(param.getOrder())) {
            if (param.getOrder().equals("sale_num")) {
                queryWrapper.orderByDesc("sale_num");
            } else if (param.getOrder().equals("price")) {
                queryWrapper.orderByDesc("goodsSku.price");
            }
        } else {
            if (ObjectUtil.isNotEmpty(param.getSiteId()) && param.getSiteId() > 0) {
                queryWrapper.orderByDesc("goods.shop_sort", "goods.create_time");
            } else {
                queryWrapper.orderByDesc("goods.sort", "goods.create_time");
            }
        }

        if (ObjectUtil.isNotEmpty(param.getNum()) && param.getNum() > 0) 
            queryWrapper.last("limit " + param.getNum());

        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        List<MallGoodsListVo> list = mallGoodsMapper.selectJoinList(MallGoodsListVo.class, queryWrapper);
        
        for (MallGoodsListVo vo : list) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            if (goodsSku == null) continue;
            
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            goodsSkuVo.setMemberDiscount(vo.getMemberDiscount());
            
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? JSONUtil.parseObj(goodsSku.getMemberPrice()) : new JSONObject();
                goodsSkuVo.setMemberPrice(calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, goodsSku.getPrice()));
            } else {
                goodsSkuVo.setMemberPrice(goodsSku.getPrice());
            }
            //设置展示价格
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(goodsSkuVo.getGoodsId())
                    .salePrice(goodsSkuVo.getSalePrice())
                    .skuId(goodsSku.getSkuId())
                    .price(goodsSku.getPrice())
                    .memberDiscount(vo.getMemberDiscount())
                    .memberPrice(ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice().toString() : null)
                    .memberId(RequestUtils.memberId())
                    .build());
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsLabelName(getGoodsLabelName(vo.getLabelIds()));
          
            if (vo.getBrandId() != null && vo.getBrandId() > 0) {
                com.niu.core.entity.goods.GoodsBrand brand = goodsBrandMapper.selectById(vo.getBrandId());
                if (brand != null) {
                    MallGoodsListVo.Brand brandVo = new MallGoodsListVo.Brand();
                    brandVo.setBrandId(brand.getBrandId());
                    brandVo.setBrandName(brand.getBrandName());
                    brandVo.setLogo(brand.getLogo());
                    brandVo.setColorJson(brand.getColorJson());
                    vo.setGoodsBrand(brandVo);
                }
            }
        }
        return list;
    }

    @Override
    public List<GoodsLabelListVo> getGoodsLabelName(String labelIds) {
        if (ObjectUtil.isEmpty(labelIds) || JSONUtil.parseArray(labelIds).isEmpty()) return new LinkedList<>();
        return CollectUtils.convert(mallGoodsLabelMapper.selectList(new QueryWrapper<MallGoodsLabel>().eq("status", 1).in("label_id", JSONUtil.parseArray(labelIds)).orderByDesc("sort")), GoodsLabelListVo.class);
    }

    /**
     * 查询商品详情
     *
     * @param param
     * @return
     */
    @Override
    public MallGoodsDetailVo detail(MallGoodsDetailParam param) {
        MallGoodsDetailVo vo = new MallGoodsDetailVo();

        QueryWrapper queryWrapper = new QueryWrapper<MallGoodsSku>();

        if (ObjectUtil.isEmpty(param.getSkuId()) && ObjectUtil.isNotEmpty(param.getGoodsId())) {
            queryWrapper.eq("goods_id", param.getGoodsId());
            queryWrapper.eq("is_default", 1);
        } else if (ObjectUtil.isNotEmpty(param.getSkuId())) {
            queryWrapper.eq("sku_id", param.getSkuId());
        } else {
            return null;
        }

        MallGoodsSku sku = mallGoodsSkuMapper.selectOne(queryWrapper);
        if (sku == null) return null;
        BeanUtils.copyProperties(sku, vo);

        // 查询商品
        MallGoods goods = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", sku.getGoodsId()).eq("delete_time", 0));
        if (goods == null) return null;
        MallGoodsDetailVo.Goods goodsVo = new MallGoodsDetailVo.Goods();
        BeanUtils.copyProperties(goods, goodsVo);
        if (ObjectUtil.isEmpty(goodsVo.getUnit())){
            goodsVo.setUnit("件");
        }
        goodsVo.setSaleNum(goods.getSaleNum() + goods.getVirtualSaleNum());

        vo.setType(param.getType());
        vo.setTypeName(ActiveTypeEnum.getNameByType(param.getType()));

        //设置展示价格
        ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                .goodsId(sku.getGoodsId())
                .salePrice(sku.getSalePrice())
                .skuId(sku.getSkuId())
                .price(sku.getPrice())
                .memberDiscount(goodsVo.getMemberDiscount())
                .memberPrice(ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? sku.getMemberPrice().toString() : null)
                .memberId(RequestUtils.memberId())
                .build());
        vo.setShowPrice(skuShowPrice.getShowPrice());
        vo.setShowType(skuShowPrice.getShowType());

        // 是否已收藏
        if (RequestUtils.memberId() != null) {
            Long goodsCollect = mallGoodsCollectMapper.selectCount(new QueryWrapper<MallGoodsCollect>().eq("goods_id", goodsVo.getGoodsId()).eq("member_id", RequestUtils.memberId()));
            goodsVo.setIsCollect(goodsCollect == 0 ? 0 : 1);
        }

        vo.setGoods(goodsVo);
        if(ObjectUtil.isNotEmpty(goodsVo)){
            vo.setIsLimit(goodsVo.getIsLimit());
            vo.setLimitType(goodsVo.getLimitType());
            vo.setMaxBuy(goodsVo.getMaxBuy());
            vo.setMinBuy(goodsVo.getMinBuy());
        }
        vo.setShopInfo(new MallGoodsDetailVo.ShopInfo());
        //店铺信息以及店铺会员信息
        Site site = siteMapper.selectOne(new QueryWrapper<Site>().eq("site_id", goods.getSiteId()));
        BeanUtils.copyProperties(site, vo.getShopInfo());
        SiteShop siteShop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", goods.getSiteId()));
        vo.getShopInfo().setShop(new MallGoodsDetailVo.SiteShopInfo());
        BeanUtils.copyProperties(siteShop, vo.getShopInfo().getShop());
        vo.getShopInfo().setFollowNumber(vo.getShopInfo().getShop().getFollowNumber());
        vo.getShopInfo().setBusinessStatus(vo.getShopInfo().getShop().getBusinessStatus());
        vo.getShopInfo().setLogisticsScore(siteShop.getLogisticsScore());
        vo.getShopInfo().setServiceScore(siteShop.getServiceScore());
        vo.getShopInfo().setQualityScore(siteShop.getQualityScore());
        // 查询会员的会员等级
        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        if (memberLevel != null) {
            JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
            vo.setMemberPrice(calculateMemberPrice(memberLevel, goods.getMemberDiscount(), memberPriceConfig, sku.getPrice()));
        } else {
            vo.setMemberPrice(sku.getPrice());
        }

        // 查询商品SKU列表
        List<MallGoodsDetailVo.Sku> skuListVo = new LinkedList<>();
        List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", sku.getGoodsId()));
        for (MallGoodsSku item : skuList) {
            MallGoodsDetailVo.Sku skuVo = new MallGoodsDetailVo.Sku();
            BeanUtils.copyProperties(item, skuVo);
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(item.getMemberPrice()) ? JSONUtil.parseObj(item.getMemberPrice()) : new JSONObject();
                skuVo.setMemberPrice(calculateMemberPrice(memberLevel, goods.getMemberDiscount(), memberPriceConfig, item.getPrice()));
            } else {
                skuVo.setMemberPrice(item.getPrice());
            }
            //设置展示价格
            ShowPriceVo skuShowPrice1 = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(goods.getGoodsId())
                    .salePrice(item.getSalePrice())
                    .skuId(item.getSkuId())
                    .price(item.getPrice())
                    .memberDiscount(goodsVo.getMemberDiscount())
                    .memberPrice(ObjectUtil.isNotEmpty(item.getMemberPrice()) ? item.getMemberPrice(): null)
                    .memberId(RequestUtils.memberId())
                    .build());
            skuVo.setShowPrice(skuShowPrice1.getShowPrice());
            skuVo.setShowType(skuShowPrice1.getShowType());
            skuListVo.add(skuVo);
        }
        vo.setSkuList(skuListVo);

        // 商品规格
        List<MallGoodsDetailVo.Spec> specList = CollectUtils.convert(mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", sku.getGoodsId())), MallGoodsDetailVo.Spec.class);
        vo.setGoodsSpec(specList);

        // 商品服务
        if (!goodsVo.getServiceIds().isEmpty()) {
            JSONArray serviceIds = JSONUtil.parseArray(goodsVo.getServiceIds());
            if (!serviceIds.isEmpty()) {
                List<MallGoodsDetailVo.Service> service = CollectUtils.convert(mallGoodsServiceMapper.selectList(new QueryWrapper<GoodsService>().in("service_id", serviceIds)), MallGoodsDetailVo.Service.class);
                vo.setService(service);
            }
        }

        // 商品标签
        vo.setLabelInfo(getGoodsLabelName(goods.getLabelIds()));

        // 商品品牌
        if (goodsVo.getBrandId() > 0) {
            GoodsBrand brand = goodsBrandMapper.selectOne(new QueryWrapper<GoodsBrand>().eq("brand_id", goodsVo.getBrandId()));
            MallGoodsDetailVo.Brand brandVo = new MallGoodsDetailVo.Brand();
            BeanUtils.copyProperties(brand, brandVo);
            vo.setBrandInfo(brandVo);
        }

        // 限时折扣
        if (goods.getIsDiscount().equals(1)) {
            vo.setDiscountInfo(mallDiscountService.getInfoByGoods(goodsVo.getGoodsId()));
        }

        // 自定义详情
        DiyInfoParam diyInfoParam = new DiyInfoParam();
        diyInfoParam.setName("DIY_MALL_GOODS_DETAIL");
        diyInfoParam.setId(goods.getDiyDetailId());
        vo.setDiyDetailInfo(diyService.info(diyInfoParam));

        // 获取商品主图第一张图片宽高
        ImageUtils.ImageSize firstImageSize = ImageUtils.getFirstImageSize(goods.getGoodsImage());
        vo.getImageSize().setWidth(firstImageSize.getWidth().toString());
        vo.getImageSize().setHeight(firstImageSize.getHeight().toString());
        MallOrderEvaluate orderEvaluate = coreOrderConfigService.getEvaluateConfig();
        vo.setEvaluateIsShow(orderEvaluate.getEvaluateIsShow());

        GoodsAddStatParam addStatParam = new GoodsAddStatParam();
        addStatParam.setGoodsId(goods.getGoodsId());
        addStatParam.setSiteId(vo.getSiteId());
        addStatParam.setAccessNum(1);
        coreGoodsStatService.addStat(addStatParam);

        return vo;
    }

    /**
     * @param skuId
     * @return
     */
    @Override
    public MallGoodsSkuVo getSku(Integer skuId) {
        MallGoodsSkuVo vo = new MallGoodsSkuVo();

        QueryWrapper queryWrapper = new QueryWrapper<MallGoodsSku>();
        queryWrapper.eq("sku_id", skuId);

        MallGoodsSku sku = mallGoodsSkuMapper.selectOne(queryWrapper);
        if (sku == null) return null;
        BeanUtils.copyProperties(sku, vo);

        // 查询商品
        MallGoods goods = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", sku.getGoodsId()).eq("delete_time", 0));
        if (goods == null) return null;
        MallGoodsSkuVo.Goods goodsVo = new MallGoodsSkuVo.Goods();
        BeanUtils.copyProperties(goods, goodsVo);
        vo.setGoods(goodsVo);


        // 获取展示价格
        ShowPriceVo detailShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                .goodsId(sku.getGoodsId())
                .salePrice(sku.getSalePrice())
                .skuId(sku.getSkuId())
                .price(sku.getPrice())
                .memberDiscount(goods.getMemberDiscount())
                .memberPrice(sku.getMemberPrice())
                .memberId(RequestUtils.memberId())
                .build());
        vo.setShowPrice(detailShowPrice.getShowPrice());
        vo.setShowType(detailShowPrice.getShowType());
        // 查询商品SKU列表
        List<MallGoodsSkuVo.Sku> skuListVo = new LinkedList<>();
        List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", sku.getGoodsId()));
        for (MallGoodsSku item : skuList) {
            MallGoodsSkuVo.Sku skuVo = new MallGoodsSkuVo.Sku();
            BeanUtils.copyProperties(item, skuVo);

            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(sku.getGoodsId())
                    .salePrice(sku.getSalePrice())
                    .skuId(item.getSkuId())
                    .price(item.getPrice())
                    .memberDiscount(goods.getMemberDiscount())
                    .memberPrice(item.getMemberPrice())
                    .memberId(RequestUtils.memberId())
                    .build());
            skuVo.setShowPrice(skuShowPrice.getShowPrice());
            skuVo.setShowType(skuShowPrice.getShowType());
            skuListVo.add(skuVo);
        }
        vo.setSkuList(skuListVo);

        // 商品规格
        List<MallGoodsSkuVo.Spec> specList = CollectUtils.convert(mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", sku.getGoodsId())), MallGoodsSkuVo.Spec.class);
        vo.setGoodsSpec(specList);

        return vo;
    }

    @Override
    public PageResult<MallGoodsListVo> getRecommend(PageParam pageParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site ns ON g.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_shop nss ON g.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num + g.virtual_sale_num as sale_num,status,g.sort,g.create_time,member_discount,virtual_verify_type,unit,is_discount,label_ids,g.stock,g.brand_id, ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");

        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.delete_time", 0);

        queryWrapper.orderByDesc("g.sale_num");
        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        IPage<MallGoodsListVo> iPage = mallGoodsMapper.selectJoinPage(new Page<>(page, limit), MallGoodsListVo.class, queryWrapper);
        List<MallGoodsListVo> list = new LinkedList<>();
        for (MallGoodsListVo vo : iPage.getRecords()) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? JSONUtil.parseObj(goodsSku.getMemberPrice()) : new JSONObject();
                goodsSkuVo.setMemberPrice(calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, goodsSku.getPrice()));
            } else {
                goodsSkuVo.setMemberPrice(goodsSku.getPrice());
            }
            //设置展示价格
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(goodsSkuVo.getGoodsId())
                    .salePrice(goodsSkuVo.getSalePrice())
                    .skuId(goodsSkuVo.getSkuId())
                    .price(goodsSkuVo.getPrice())
                    .memberDiscount(vo.getMemberDiscount())
                    .memberPrice(ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice().toString() : null)
                    .memberId(RequestUtils.memberId())
                    .build());
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsLabelName(getGoodsLabelName(vo.getLabelIds()));
            if (vo.getBrandId() > 0) {
                MallGoodsListVo.Brand brand = new MallGoodsListVo.Brand();
                BeanUtils.copyProperties(vo, brand);
                vo.setGoodsBrand(brand);
            }
            MallGoodsListVo.ShopInfo shopInfo = new MallGoodsListVo.ShopInfo();
            shopInfo.setSiteId(vo.getSiteId());
            shopInfo.setIsSelf(vo.getIsSelf());
            MallGoodsListVo.SiteInfo siteInfo = new MallGoodsListVo.SiteInfo();
            siteInfo.setSiteId(vo.getSiteId());
            siteInfo.setSiteName(vo.getSiteName());
            vo.setShop(shopInfo);
            vo.setSite(siteInfo);

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<com.niu.core.service.api.shop.vo.MallGoodsListVo> selectGoodsAndSkuList(MallShopGoodsListEventDefiner.MallShopGoodsListEvent event) {
        Integer siteId = event.getSiteId();
        MallShopGoodsListEventDefiner.MallShopGoodsListEventResult result = new MallShopGoodsListEventDefiner.MallShopGoodsListEventResult();
        List<com.niu.core.service.api.shop.vo.MallGoodsListVo> goods = new ArrayList<>();
        LambdaQueryWrapper<MallGoods> goodsWrapper = new LambdaQueryWrapper<>();
        goodsWrapper.eq(MallGoods::getSiteId, siteId);
        goodsWrapper.eq(MallGoods::getStatus, 1);
        goodsWrapper.last("limit " + event.getNum());
        List<MallGoods> mallGoods = goodsMapper.selectList(goodsWrapper);
        if (CollectionUtil.isNotEmpty(mallGoods)) {
            for (MallGoods mallGood : mallGoods) {
                com.niu.core.service.api.shop.vo.MallGoodsListVo mallGoodsListVo = new com.niu.core.service.api.shop.vo.MallGoodsListVo();
                BeanUtils.copyProperties(mallGood, mallGoodsListVo);
                if (mallGood.getGoodsCover() != null && !mallGood.getGoodsCover().isEmpty()) {
                    mallGoodsListVo.setGoodsCoverThumbSmall(mallGood.getGoodsCover());
                } else {
                    mallGoodsListVo.setGoodsCoverThumbSmall("");
                }
                
                goods.add(mallGoodsListVo);
            }
        }
        if (CollectionUtil.isNotEmpty(goods)) {
            for (com.niu.core.service.api.shop.vo.MallGoodsListVo good : goods) {
                LambdaQueryWrapper<MallGoodsSku> goodsSkuWrapper = new LambdaQueryWrapper<>();
                goodsSkuWrapper.in(MallGoodsSku::getGoodsId, good.getGoodsId());
                goodsSkuWrapper.eq(MallGoodsSku::getSiteId, siteId);
                goodsSkuWrapper.eq(MallGoodsSku::getIsDefault, 1);
                MallGoodsSku mallGoodsSkus = goodsSkuMapper.selectOne(goodsSkuWrapper);
                if (ObjectUtil.isNotEmpty(mallGoodsSkus)) {
                        com.niu.core.service.api.goods.vo.MallGoodsSkuListVo skuListVo = new com.niu.core.service.api.goods.vo.MallGoodsSkuListVo();
                        BeanUtils.copyProperties(mallGoodsSkus, skuListVo);
                        //设置展示价格
                        ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                                .goodsId(skuListVo.getGoodsId())
                                .salePrice(skuListVo.getSalePrice())
                                .skuId(skuListVo.getSkuId())
                                .price(skuListVo.getPrice())
                                .memberDiscount(good.getMemberDiscount())
                                .memberPrice(ObjectUtil.isNotEmpty(mallGoodsSkus.getMemberPrice()) ? mallGoodsSkus.getMemberPrice().toString() : null)
                                .memberId(RequestUtils.memberId())
                                .build());
                        skuListVo.setShowPrice(skuShowPrice.getShowPrice());
                        skuListVo.setShowType(skuShowPrice.getShowType());
                        good.setGoodsSku(skuListVo);
                    }
            }
        }
        result.setGoods(goods);
        result.setGoodsList(goods);
        return goods;
    }

    @Override
    public List<MallGoodsListVo> shopRecommendComponents(ShopComponentsSearchParam param) {
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop nss ON g.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.select("g.goods_id,g.site_id,goods_name,sub_title,goods_category,goods_type,goods_cover,g.sale_num,g.sale_num + g.virtual_sale_num as sale_num,status,member_discount,is_discount,virtual_receive_type");

        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.delete_time", 0);
        queryWrapper.eq("nss.business_status", ShopStatusEnum.ON.getCode());

        queryWrapper.orderByDesc("g.sale_num");
        queryWrapper.last("limit " + param.getNum());
        List<MallGoodsListVo> list = mallGoodsMapper.selectJoinList(MallGoodsListVo.class, queryWrapper);
        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());


        List<MallGoodsSku> goodsSkuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>()
                .in("goods_id", list.stream().map(MallGoodsListVo::getGoodsId).toList()).eq("is_default", 1));
        for (int i = 0; i < list.size(); i++) {
            MallGoodsListVo vo = list.get(i);
            List<MallGoodsSku> goodsSkuThis = goodsSkuList.stream().filter(item -> item.getGoodsId().equals(vo.getGoodsId())).toList();
            if(goodsSkuThis.isEmpty()){
                continue;
            }
            MallGoodsSku goodsSku = goodsSkuThis.get(0);

            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? JSONUtil.parseObj(goodsSku.getMemberPrice()) : null;
                goodsSkuVo.setMemberPrice(calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, goodsSku.getPrice()));
            } else {
                goodsSkuVo.setMemberPrice(goodsSku.getPrice());
            }
            //设置展示价格
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(goodsSkuVo.getGoodsId())
                    .salePrice(goodsSkuVo.getSalePrice())
                    .skuId(goodsSkuVo.getSkuId())
                    .price(goodsSkuVo.getPrice())
                    .memberDiscount(vo.getMemberDiscount())
                    .memberPrice(ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice().toString() : null)
                    .memberId(RequestUtils.memberId())
                    .build());
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsLabelName(getGoodsLabelName(vo.getLabelIds()));
            if (vo.getBrandId() != null && vo.getBrandId() > 0) {
                MallGoodsListVo.Brand brand = new MallGoodsListVo.Brand();
                BeanUtils.copyProperties(vo, brand);
                vo.setGoodsBrand(brand);
            }
            MallGoodsListVo.ShopInfo shopInfo = new MallGoodsListVo.ShopInfo();
            shopInfo.setSiteId(vo.getSiteId());
            shopInfo.setIsSelf(vo.getIsSelf());
            vo.setShop(shopInfo);
        }
        return list;
    }

}
