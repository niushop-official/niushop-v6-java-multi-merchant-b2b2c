package com.niu.mall.service.api.goods.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
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
import com.niu.core.entity.goods.GoodsLabel;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.point_deduct.PointDeduct;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.shop.ShopStatusEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.event.shop.MallShopGoodsListEventDefiner;
import com.niu.core.mapper.goods.GoodsBrandMapper;
import com.niu.core.mapper.goods.GoodsLabelMapper;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.diy.param.DiyInfoParam;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.api.vip_card.card.vo.ApiVipCardGoodsVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.core.service.core.goods.ICoreGoodsCategoryService;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.point_deduct.ICorePointDeductService;
import com.niu.core.service.core.point_deduct.vo.DeductMoneyVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.cart.MallCart;
import com.niu.core.entity.coupon.MallCoupon;
import com.niu.core.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.goods.*;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.enums.active.ActiveTypeEnum;
import com.niu.core.enums.coupon.CouponIsAllSiteEnum;
import com.niu.core.enums.coupon.CouponReceiveTypeEnum;
import com.niu.core.enums.coupon.CouponStatusEnum;
import com.niu.core.enums.coupon.CouponTypeEnum;
import com.niu.core.enums.fullactive.FullReductionTypeEnum;
import com.niu.core.enums.goods.GoodsAuditStatusEnum;
import com.niu.core.enums.goods.GoodsStatusEnum;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.mall.mapper.cart.MallCartMapper;
import com.niu.core.mapper.coupon.MallCouponGoodsMapper;
import com.niu.core.mapper.coupon.MallCouponMapper;
import com.niu.mall.mapper.goods.*;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.api.goods.IGoodsBrowseService;
import com.niu.mall.service.api.goods.IGoodsService;
import com.niu.mall.service.api.goods.param.*;
import com.niu.mall.service.api.goods.vo.*;
import com.niu.core.service.api.marketing.ICouponService;
import com.niu.mall.service.api.marketing.IMallDiscountService;
import com.niu.mall.service.api.marketing.vo.MallDiscountGoodsInfoVo;
import com.niu.mall.service.core.delivery.ICoreLocalDeliveryService;
import com.niu.mall.service.core.goods.ICoreGoodsAccessNumService;
import com.niu.mall.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.mall.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GetShowPriceParam;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import com.niu.mall.service.core.goods.vo.ShowPriceVo;
import com.niu.mall.service.core.marketing.ICoreFullGiftService;
import com.niu.core.service.admin.marketing.param.FullReductionParam;
import com.niu.core.service.core.marketing.ICoreFullReductionService;
import com.niu.core.service.core.marketing.vo.FullReductionCalculateVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoodsServiceImpl implements IGoodsService {

    private static final Pattern GOODS_IDS_PARAM_PATTERN = Pattern.compile("^(goods[_ ]ids)\\[(\\d+)]$");

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
    GoodsLabelMapper goodsLabelMapper;

    @Resource
    GoodsBrandMapper goodsBrandMapper;

    @Resource
    IDiyService diyService;

    @Resource
    MallGoodsCollectMapper mallGoodsCollectMapper;

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
    ICoreGoodsAccessNumService coreGoodsAccessNumService;

    @Resource
    IGoodsBrowseService goodsBrowseService;

    @Resource
    ICouponService couponService;
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
    @Resource
    ICoreDiyFormRecordsService coreDiyFormRecordsService;

    /**
     * 查询商品列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return 查询结果
     */
    @Override
    public PageResult<MallGoodsListVo> page(PageParam pageParam, MallGoodsSearchParam searchParam, Map<String, String> allParams) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_goods_brand sgb ON g.brand_id = sgb.brand_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site ns ON g.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop nss ON g.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num," +
                "g.status,g.sort,g.create_time,member_discount,vip_card_discount,virtual_verify_type,unit," +
                "is_discount,label_ids,g.brand_id,sgb.brand_name,sgb.logo, ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");
        AdminDeliveryConfigVo deliveryConfig = coreDeliveryConfigService.getAdminDeliveryConfig();
        if (ObjectUtil.isNotEmpty(allParams)) {
            String latitude = StringUtils.isNotEmpty(allParams.get("position[latitude]")) ? allParams.get("position[latitude]") : "";
            String longitude = StringUtils.isNotEmpty(allParams.get("position[longitude]")) ? allParams.get("position[longitude]") : "";
            if (StringUtils.isNotEmpty(latitude) && StringUtils.isNotEmpty(longitude) && deliveryConfig.getIsShow() == 1) {
                String siteField = "ns.latitude,ns.longitude";
                siteField += ", ROUND(6371 * acos( " +
                        "cos(radians(" + latitude + ")) " +
                        "        * cos(radians(ns.latitude)) " +
                        "        * cos(radians(ns.longitude) - radians(" + longitude + ")) " +
                        "        + sin(radians(" + latitude + ")) " +
                        "        * sin(radians(ns.latitude)) " +
                        "  ), 2) AS distance";
                queryWrapper.select(siteField);
            }
        }

        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.audit_status", 2);
        queryWrapper.eq("g.delete_time", 0);
        queryWrapper.eq("nss.business_status", ShopStatusEnum.ON.getCode());
        queryWrapper.eq(Objects.nonNull(searchParam.getCircleId()), "nss.circle_id", searchParam.getCircleId());
        queryWrapper.ne(Objects.nonNull(searchParam.getIsVipCard()), "g.vip_card_discount", "");

        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.like("g.goods_name", searchParam.getKeyword());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getBrandId())) queryWrapper.eq("brand_id", searchParam.getBrandId());
        if (!searchParam.getSiteId().equals(0)) queryWrapper.eq("g.site_id", searchParam.getSiteId());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsMallCategory())) {
            queryWrapper.in("g.goods_mall_category", coreGoodsCategoryService.getGoodsMallCategoryIdsChild(Integer.valueOf(searchParam.getGoodsMallCategory())));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory())) {
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
            List<Integer> goodsIds = couponGoodsList.stream().map(MallCouponGoods::getGoodsId).filter(goodsId -> goodsId > 0).toList();
            List<Integer> categoryIds = couponGoodsList.stream().map(MallCouponGoods::getCategoryId).filter(categoryId -> categoryId > 0).toList();
            if (!goodsIds.isEmpty()) {
                queryWrapper.in("g.goods_id", goodsIds);
            } else if (!categoryIds.isEmpty()) {
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
            ShowPriceVo skuShowPrice = buildSkuShowPrice(vo.getMemberDiscount(), vo.getVipCardDiscount(), goodsSku,
                    goodsSkuVo.getPrice(), goodsSkuVo.getSalePrice(),
                    ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice() : null);
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            goodsSkuVo.setMemberDiscount(vo.getMemberDiscount());
            goodsSkuVo.setVipCardDiscount(vo.getVipCardDiscount());
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
        return PageResult.build(page, limit, iPage.getTotal(), list);
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
                ",g.create_time,member_discount,vip_card_discount,virtual_verify_type,unit,is_discount,label_ids,g.stock,g.brand_id, ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.audit_status", 2);

        queryWrapper.eq("g.delete_time", 0);
        queryWrapper.eq("nss.business_status", ShopStatusEnum.ON.getCode());
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.like("g.goods_name", searchParam.getKeyword());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getBrandId())) {
            queryWrapper.eq("g.brand_id", searchParam.getBrandId());
        }
        if (!searchParam.getSiteId().equals(0)) {
            queryWrapper.eq("g.site_id", searchParam.getSiteId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsMallCategory())) {
            queryWrapper.in("g.goods_mall_category", coreGoodsCategoryService.getGoodsMallCategoryIdsChild(Integer.valueOf(searchParam.getGoodsMallCategory())));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory())) {
            queryWrapper.like("g.goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        }
        if (ObjectUtil.isNotEmpty(searchParam.getLabelIds())) {
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
            List<Integer> goodsIds = couponGoodsList.stream().map(MallCouponGoods::getGoodsId).filter(goodsId -> goodsId > 0).toList();
            List<Integer> categoryIds = couponGoodsList.stream().map(MallCouponGoods::getCategoryId).filter(categoryId -> categoryId > 0).toList();
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
            ShowPriceVo skuShowPrice = buildSkuShowPrice(vo.getMemberDiscount(), vo.getVipCardDiscount(), goodsSku,
                    goodsSkuVo.getPrice(), goodsSkuVo.getSalePrice(),
                    ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice() : null);
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            goodsSkuVo.setMemberDiscount(vo.getMemberDiscount());
            goodsSkuVo.setVipCardDiscount(vo.getVipCardDiscount());
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
        return PageResult.build(page, limit, iPage.getTotal(), list);
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

        queryWrapper.select("g.site_id,g.goods_id,g.goods_name,g.sub_title,g.goods_category,g.goods_type,g.goods_cover,g.unit,g.status,g.sale_num + g.virtual_sale_num as sale_num,g.member_discount,g.vip_card_discount,g.is_discount,g.virtual_receive_type,g.label_ids,g.brand_id,sgb.brand_name,sgb.logo,ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");

        // 基础条件
        queryWrapper.eq("gs.is_default", 1);
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
        queryWrapper.eq("g.delete_time", 0);
        queryWrapper.eq("nss.business_status", ShopStatusEnum.ON.getCode());

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
            ShowPriceVo skuShowPrice = buildSkuShowPrice(vo.getMemberDiscount(), vo.getVipCardDiscount(), goodsSku,
                    goodsSkuVo.getPrice(), goodsSkuVo.getSalePrice(),
                    ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice() : null);
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            goodsSkuVo.setMemberDiscount(vo.getMemberDiscount());
            goodsSkuVo.setVipCardDiscount(vo.getVipCardDiscount());
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
        return PageResult.build(page, limit, iPage.getTotal(), list);
    }

    @Override
    public List<MallGoodsListVo> getRecommendList(Integer siteId, Integer num) {
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site ns ON g.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop nss ON g.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num + g.virtual_sale_num as sale_num," +
                "g.status,g.sort,g.create_time,member_discount,vip_card_discount,virtual_verify_type,unit,is_discount,label_ids,g.stock,g.brand_id, ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
        queryWrapper.eq("g.delete_time", 0);
        queryWrapper.eq("nss.business_status", ShopStatusEnum.ON.getCode());
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
            ShowPriceVo skuShowPrice = buildSkuShowPrice(vo.getMemberDiscount(), vo.getVipCardDiscount(), goodsSku,
                    goodsSkuVo.getPrice(), goodsSkuVo.getSalePrice(),
                    ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice() : null);
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            goodsSkuVo.setMemberDiscount(vo.getMemberDiscount());
            goodsSkuVo.setVipCardDiscount(vo.getVipCardDiscount());
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

        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num + g.virtual_sale_num as sale_num,status,g.sort,g.create_time,member_discount,vip_card_discount,virtual_verify_type,unit,is_discount,label_ids,g.stock,g.brand_id, ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");

        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
        queryWrapper.eq("g.delete_time", 0);
        queryWrapper.eq("nss.business_status", ShopStatusEnum.ON.getCode());
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) queryWrapper.like("goods_name", searchParam.getKeyword());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandId())) queryWrapper.eq("brand_id", searchParam.getBrandId());
        if (!searchParam.getSiteId().equals(0)) queryWrapper.eq("g.site_id", searchParam.getSiteId());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsMallCategory())) {
            queryWrapper.in("g.goods_mall_category", coreGoodsCategoryService.getGoodsMallCategoryIdsChild(Integer.valueOf(searchParam.getGoodsMallCategory())));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory())) {
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
            List<Integer> goodsIds = couponGoodsList.stream().map(MallCouponGoods::getGoodsId).filter(goodsId -> goodsId > 0).toList();
            List<Integer> categoryIds = couponGoodsList.stream().map(MallCouponGoods::getCategoryId).filter(categoryId -> categoryId > 0).toList();
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
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId())){
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
        }

        List<MallGoodsListVo> list = mallGoodsMapper.selectJoinList(MallGoodsListVo.class, queryWrapper);

        for (MallGoodsListVo vo : list) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            //设置展示价格
            ShowPriceVo skuShowPrice = buildSkuShowPrice(vo.getMemberDiscount(), vo.getVipCardDiscount(), goodsSku,
                    goodsSkuVo.getPrice(), goodsSkuVo.getSalePrice(),
                    ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice() : null);
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            goodsSkuVo.setMemberDiscount(vo.getMemberDiscount());
            goodsSkuVo.setVipCardDiscount(vo.getVipCardDiscount());
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
     * @return 会员价
     */
    public BigDecimal calculateMemberPrice(MemberLevelInfoVo memberLevel, String calculateType, JSONObject config, BigDecimal price) {
        return coreGoodsActivePriceService.calculateMemberPrice(memberLevel, calculateType, config, price);
    }

    /**
     * 获取商品列表供组件调用
     *
     * @param param 搜索参数
     */
    @Override
    public List<MallGoodsListVo> getGoodsComponents(MallGoodsComponentsSearchParam param, Map<String, String> allParams) {
        AdminDeliveryConfigVo deliveryConfig = coreDeliveryConfigService.getAdminDeliveryConfig();
        boolean isDistance = hasComponentLatLng(allParams) && deliveryConfig.getIsShow() == 1;

        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("goods")
                .leftJoin("?_mall_goods_sku goodsSku ON goods.goods_id = goodsSku.goods_id and goodsSku.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site ns ON goods.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop shop ON goods.site_id = shop.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.select("goods.goods_id, goods.site_id, goods.goods_name, goods.sub_title, goods.goods_mall_category, goods.goods_category, goods.goods_type, goods.goods_cover, goods.unit, goods.status, goods.sale_num + goods.virtual_sale_num as sale_num, goods.is_discount, goods.member_discount,goods.vip_card_discount, goods.label_ids, goods.brand_id, shop.is_self, goodsSku.price,goods.max_buy,goods.min_buy,goods.is_limit,goods.limit_type,goods.virtual_receive_type");

        if (isDistance) {
            String latitude = allParams.get("position[latitude]");
            String longitude = allParams.get("position[longitude]");
            String siteField = "ns.latitude,ns.longitude";
            siteField += ", ROUND(6371 * acos( " +
                    "cos(radians(" + latitude + ")) " +
                    "        * cos(radians(ns.latitude)) " +
                    "        * cos(radians(ns.longitude) - radians(" + longitude + ")) " +
                    "        + sin(radians(" + latitude + ")) " +
                    "        * sin(radians(ns.latitude)) " +
                    "  ), 2) AS distance";
            queryWrapper.select(siteField);
        }

        queryWrapper.eq("goodsSku.is_default", 1);
        queryWrapper.eq("goods.status", GoodsStatusEnum.ONLINE.getStatus());
        queryWrapper.eq("goods.audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
        queryWrapper.eq("goods.delete_time", 0);
        queryWrapper.eq("shop.business_status", ShopStatusEnum.ON.getCode());

        List<Integer> goodsIds = resolveComponentGoodsIds(param, allParams);
        if (ObjectUtil.isNotEmpty(param.getSiteId()) && param.getSiteId() > 0) {
            queryWrapper.eq("goods.site_id", param.getSiteId());
        } else if (shouldFilterByLocation(param, allParams, goodsIds)) {
            List<Integer> siteIds = resolveComponentSiteIds(param, allParams);
            if (siteIds.isEmpty()) {
                return Collections.emptyList();
            }
            queryWrapper.in("goods.site_id", siteIds);
        }

        if (hasExplicitGoodsIdsFilter(param, allParams) && CollUtil.isEmpty(goodsIds)) {
            return Collections.emptyList();
        }
        if (CollUtil.isNotEmpty(goodsIds)) {
            queryWrapper.in("goods.goods_id", goodsIds);
        } else {
            applyComponentCircleFilter(queryWrapper);
        }

        applyComponentCategoryFilter(queryWrapper, param);

        if (ObjectUtil.isNotEmpty(param.getLabelIds()) && param.getLabelIds() > 0) {
            queryWrapper.like("goods.label_ids", "\"" + param.getLabelIds() + "\"");
        }
        if (ObjectUtil.isNotEmpty(param.getServiceIds()) && param.getServiceIds() > 0) {
            queryWrapper.like("goods.service_ids", "\"" + param.getServiceIds() + "\"");
        }

        applyComponentOrder(queryWrapper, param, isDistance);

        if (ObjectUtil.isNotEmpty(param.getNum()) && param.getNum() > 0) {
            queryWrapper.last("limit " + param.getNum());
        }

        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId())) {
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
        }

        List<MallGoodsListVo> list = mallGoodsMapper.selectJoinList(MallGoodsListVo.class, queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return list;
        }

        Map<Integer, Site> siteMap = loadComponentSiteMap(list);

        for (MallGoodsListVo vo : list) {
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
            ShowPriceVo skuShowPrice = buildSkuShowPrice(vo.getMemberDiscount(), vo.getVipCardDiscount(), goodsSku,
                    goodsSku.getPrice(), goodsSkuVo.getSalePrice(),
                    ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice() : null);
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            goodsSkuVo.setMemberDiscount(vo.getMemberDiscount());
            goodsSkuVo.setVipCardDiscount(vo.getVipCardDiscount());
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

            MallGoodsListVo.ShopInfo shopInfo = new MallGoodsListVo.ShopInfo();
            shopInfo.setSiteId(vo.getSiteId());
            shopInfo.setIsSelf(vo.getIsSelf());
            vo.setShop(shopInfo);

            Site site = siteMap.get(vo.getSiteId());
            if (site != null) {
                MallGoodsListVo.SiteInfo siteInfo = new MallGoodsListVo.SiteInfo();
                siteInfo.setSiteId(site.getSiteId());
                siteInfo.setSiteName(site.getSiteName());
                siteInfo.setFrontEndLogo(site.getFrontEndLogo());
                siteInfo.setDistance(vo.getDistance());
                vo.setSite(siteInfo);
            }
        }
        return list;
    }

    private boolean hasComponentLatLng(Map<String, String> allParams) {
        return CollUtil.isNotEmpty(allParams)
                && StringUtils.isNotEmpty(allParams.get("position[latitude]"))
                && StringUtils.isNotEmpty(allParams.get("position[longitude]"));
    }

    private boolean hasComponentProvinceCityDistrict(Map<String, String> allParams) {
        return CollUtil.isNotEmpty(allParams)
                && allParams.containsKey("position[province_id]")
                && allParams.containsKey("position[city_id]")
                && allParams.containsKey("position[district_id]")
                && StringUtils.isNotEmpty(allParams.get("position[province_id]"))
                && StringUtils.isNotEmpty(allParams.get("position[city_id]"))
                && StringUtils.isNotEmpty(allParams.get("position[district_id]"));
    }

    private boolean shouldFilterByLocation(MallGoodsComponentsSearchParam param, Map<String, String> allParams, List<Integer> goodsIds) {
        return ObjectUtil.isNotEmpty(param.getIsLocation())
                && param.getIsLocation() > 0
                && hasComponentProvinceCityDistrict(allParams)
                && goodsIds.isEmpty();
    }

    /**
     * 定位场景下解析可展示商品的站点 ID（对齐 PHP getGoodsComponents 同城配送块）
     */
    private List<Integer> resolveComponentSiteIds(MallGoodsComponentsSearchParam param, Map<String, String> allParams) {
        MPJQueryWrapper<Site> siteQuery = new MPJQueryWrapper<>();
        siteQuery.setAlias("ns")
                .leftJoin("?_site_shop shop ON ns.site_id = shop.site_id".replace("?_", GlobalConfig.tablePrefix))
                .select("ns.site_id");
        siteQuery.eq("ns.province_id", allParams.get("position[province_id]"));
        siteQuery.eq("ns.city_id", allParams.get("position[city_id]"));
        siteQuery.eq("ns.district_id", allParams.get("position[district_id]"));
        siteQuery.ne("ns.app_type", "admin");
        siteQuery.eq("ns.status", SiteStatusEnum.ON.getCode());
        siteQuery.eq("shop.business_status", ShopStatusEnum.ON.getCode());

        if (ObjectUtil.isEmpty(param.getIsLocalDelivery()) || param.getIsLocalDelivery() == 0) {
            return siteMapper.selectJoinList(Site.class, siteQuery).stream()
                    .map(Site::getSiteId)
                    .filter(Objects::nonNull)
                    .toList();
        }

        siteQuery.eq("ns.is_local_delivery", 1);
        List<Site> deliverySites = siteMapper.selectJoinList(Site.class, siteQuery);
        if (!hasComponentLatLng(allParams)) {
            return Collections.emptyList();
        }
        return coreLocalDeliveryService.checkLocationInArea(
                deliverySites,
                allParams.get("position[latitude]"),
                allParams.get("position[longitude]")
        );
    }

    private void applyComponentCircleFilter(MPJQueryWrapper<MallGoods> queryWrapper) {
        List<Integer> circleIds = RequestUtils.circleIds();
        if (CollUtil.isNotEmpty(circleIds)) {
            queryWrapper.in("shop.circle_id", circleIds);
        }
    }

    private void applyComponentCategoryFilter(MPJQueryWrapper<MallGoods> queryWrapper, MallGoodsComponentsSearchParam param) {
        if (ObjectUtil.isEmpty(param.getGoodsCategory()) || param.getGoodsCategory() <= 0) {
            return;
        }
        if (ObjectUtil.isNotEmpty(param.getSiteId()) && param.getSiteId() > 0) {
            queryWrapper.like("goods.goods_category", "\"" + param.getGoodsCategory() + "\"");
        } else {
            queryWrapper.in("goods.goods_mall_category",
                    coreGoodsCategoryService.getGoodsMallCategoryIdsChild(param.getGoodsCategory()));
        }
    }

    private void applyComponentOrder(MPJQueryWrapper<MallGoods> queryWrapper, MallGoodsComponentsSearchParam param, boolean isDistance) {
        String sort = resolveComponentSort(param.getSort());
        boolean asc = "asc".equals(sort);
        String order = param.getOrder();

        if (StringUtils.isNotEmpty(order) && ("sale_num".equals(order) || "price".equals(order) || "distance".equals(order))) {
            if ("distance".equals(order)) {
                if (!isDistance) {
                    applyComponentDefaultOrder(queryWrapper, param);
                } else {
                    queryWrapper.orderBy(true, asc, "distance");
                }
            } else if ("sale_num".equals(order)) {
                queryWrapper.orderBy(true, asc, "sale_num");
            } else {
                queryWrapper.orderBy(true, asc, "goodsSku.price");
            }
            return;
        }
        applyComponentDefaultOrder(queryWrapper, param);
    }

    private void applyComponentDefaultOrder(MPJQueryWrapper<MallGoods> queryWrapper, MallGoodsComponentsSearchParam param) {
        if (ObjectUtil.isNotEmpty(param.getSiteId()) && param.getSiteId() > 0) {
            queryWrapper.orderByDesc("goods.shop_sort", "goods.create_time");
        } else {
            queryWrapper.orderByDesc("goods.sort", "goods.create_time");
        }
    }

    private String resolveComponentSort(String sort) {
        if ("asc".equals(sort) || "desc".equals(sort)) {
            return sort;
        }
        return "desc";
    }

    private Map<Integer, Site> loadComponentSiteMap(List<MallGoodsListVo> list) {
        List<Integer> siteIds = list.stream()
                .map(MallGoodsListVo::getSiteId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (siteIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return siteMapper.selectList(new QueryWrapper<Site>()
                        .in("site_id", siteIds)
                        .select("site_id", "site_name", "front_end_logo"))
                .stream()
                .collect(Collectors.toMap(Site::getSiteId, site -> site, (a, b) -> a));
    }

    /**
     * 解析组件商品 ID：合并 param.goodsIds 与 allParams 中 goods_ids[n] 下标参数
     */
    private List<Integer> resolveComponentGoodsIds(MallGoodsComponentsSearchParam param, Map<String, String> allParams) {
        LinkedHashSet<Integer> ids = new LinkedHashSet<>();
        if (ObjectUtil.isNotEmpty(param.getGoodsIds())) {
            for (Integer id : param.getGoodsIds()) {
                if (id != null && id > 0) {
                    ids.add(id);
                }
            }
        }
        if (CollUtil.isNotEmpty(allParams)) {
            allParams.entrySet().stream()
                    .map(e -> {
                        Matcher matcher = GOODS_IDS_PARAM_PATTERN.matcher(e.getKey());
                        if (!matcher.matches()) {
                            return null;
                        }
                        try {
                            int idx = Integer.parseInt(matcher.group(2));
                            String val = e.getValue();
                            if (val == null || val.trim().isEmpty()) {
                                return null;
                            }
                            int id = Integer.parseInt(val.trim());
                            if (id <= 0) {
                                return null;
                            }
                            return Map.entry(idx, id);
                        } catch (NumberFormatException ex) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .map(Map.Entry::getValue)
                    .forEach(ids::add);
        }
        return new ArrayList<>(ids);
    }

    /**
     * 是否明确指定了 goods_ids 筛选（手动选商品模式），不含非 custom 场景下的 goods_ids= 空字符串
     */
    private boolean hasExplicitGoodsIdsFilter(MallGoodsComponentsSearchParam param, Map<String, String> allParams) {
        if (param.getGoodsIds() != null) {
            return true;
        }
        if (CollUtil.isEmpty(allParams)) {
            return false;
        }
        return allParams.keySet().stream().anyMatch(key -> GOODS_IDS_PARAM_PATTERN.matcher(key).matches());
    }

    @Override
    public List<GoodsLabelListVo> getGoodsLabelName(String labelIds) {
        if (ObjectUtil.isEmpty(labelIds) || JSONUtil.parseArray(labelIds).isEmpty()) return new LinkedList<>();
        // 对齐 PHP：商品标签来自基础表 goods_label（app\model\basic\Label），非 mall_goods_label
        return CollectUtils.convert(goodsLabelMapper.selectList(new QueryWrapper<GoodsLabel>()
                .eq("status", 1)
                .in("label_id", JSONUtil.parseArray(labelIds))
                .orderByDesc("sort")
                .orderByDesc("label_id")), GoodsLabelListVo.class);
    }

    /**
     * 查询商品详情
     *
     * @param param 详情参数
     * @return 商品详情
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
        if (ObjectUtil.isEmpty(goodsVo.getUnit())) {
            goodsVo.setUnit("件");
        }
        goodsVo.setSaleNum(goods.getSaleNum());

        vo.setType(param.getType());
        vo.setTypeName(ActiveTypeEnum.getNameByType(param.getType()));

        //设置展示价格
        ShowPriceVo skuShowPrice = buildSkuShowPrice(goodsVo.getMemberDiscount(), goods.getVipCardDiscount(), sku,
                sku.getPrice(), sku.getSalePrice(),
                ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? sku.getMemberPrice() : null);
        vo.setShowPrice(skuShowPrice.getShowPrice());
        vo.setShowType(skuShowPrice.getShowType());

        // 是否已收藏
        if (RequestUtils.memberId() != null) {
            Long goodsCollect = mallGoodsCollectMapper.selectCount(new QueryWrapper<MallGoodsCollect>().eq("goods_id", goodsVo.getGoodsId()).eq("member_id", RequestUtils.memberId()));
            goodsVo.setIsCollect(goodsCollect == 0 ? 0 : 1);
        }

        vo.setGoods(goodsVo);
        if (ObjectUtil.isNotEmpty(goodsVo)) {
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
            ShowPriceVo skuShowPrice1 = buildSkuShowPrice(goodsVo.getMemberDiscount(), goods.getVipCardDiscount(), item,
                    item.getPrice(), item.getSalePrice(),
                    ObjectUtil.isNotEmpty(item.getMemberPrice()) ? item.getMemberPrice() : null);
            skuVo.setShowPrice(skuShowPrice1.getShowPrice());
            skuVo.setShowType(skuShowPrice1.getShowType());
            skuListVo.add(skuVo);
        }
        vo.setSkuList(skuListVo);

        // 商品规格
        List<MallGoodsDetailVo.Spec> specList = CollectUtils.convert(mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", sku.getGoodsId())), MallGoodsDetailVo.Spec.class);
        vo.setGoodsSpec(specList);

        // 商品服务
        if (ObjectUtil.isNotEmpty(goodsVo.getServiceIds())) {
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

        GoodsAddStatParam addStatParam = new GoodsAddStatParam();
        addStatParam.setGoodsId(goods.getGoodsId());
        addStatParam.setSiteId(vo.getSiteId());
        addStatParam.setAccessNum(1);
        coreGoodsStatService.addStat(addStatParam);
        coreGoodsAccessNumService.inc(goods.getGoodsId(), 1);

        if (ObjectUtil.isNotEmpty(RequestUtils.memberId())) {
            GoodsBrowseParam browseParam = new GoodsBrowseParam();
            browseParam.setGoodsId(goods.getGoodsId());
            goodsBrowseService.add(browseParam);
        }

        //种草秀数据查询
        CommonEventDefiner.CommonEvent event = new CommonEventDefiner.CommonEvent();
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("relate_id", vo.getGoodsId());
        eventMap.put("relate_type", "mall");
        eventMap.put("limit", 3);
        event.setEventName("SowShowData");
        event.setData(eventMap);
        event.setSiteId(RequestUtils.siteId());
        List<CommonEventDefiner.CommonEventResult> sowShowDataResult = EventAndSubscribeOfPublisher.publishAndCallback(event);
        if (CollectionUtils.isNotEmpty(sowShowDataResult) && ObjectUtils.isNotEmpty(sowShowDataResult.get(0).getResult())) {
            vo.setSowShowList(sowShowDataResult.get(0).getResult());
        }


        // 自定义详情
        DiyInfoParam diyInfoParam = new DiyInfoParam();
        diyInfoParam.setName("DIY_MALL_GOODS_DETAIL");
        diyInfoParam.setId(goods.getDiyDetailId());
        vo.setDiyDetailInfo(diyService.info(diyInfoParam));

        //商品校验
        DiyFormRecordsParam diyRecordsParam = new DiyFormRecordsParam();
        diyRecordsParam.setFormId(vo.getGoods().getFormId());
        diyRecordsParam.setMemberId(RequestUtils.memberId());
        List<String> error = coreDiyFormRecordsService.check(diyRecordsParam);
        if (!error.isEmpty()) {
            vo.getGoods().setFormId(0);
        }

        // 获取商品主图第一张图片宽高
        if (ObjectUtil.isNotEmpty(goods.getGoodsImage())) {
            ImageUtils.ImageSize firstImageSize = ImageUtils.getFirstImageSize(goods.getGoodsImage());
            vo.getImageSize().setWidth(String.valueOf(firstImageSize.getWidth()));
            vo.getImageSize().setHeight(String.valueOf(firstImageSize.getHeight()));
        }

        vo.setRecommendGoodsList(getRecommendGoodsListByIds(goods.getRecommendGoodsIds()));

        return vo;
    }

    private List<MallGoodsListVo> getRecommendGoodsListByIds(String recommendGoodsIds) {
        if (ObjectUtil.isEmpty(recommendGoodsIds)) {
            return Collections.emptyList();
        }
        List<Integer> goodsIds = new ArrayList<>();
        if (JSONUtil.isTypeJSONArray(recommendGoodsIds)) {
            JSONUtil.parseArray(recommendGoodsIds).forEach(item -> {
                if (item != null) {
                    goodsIds.add(Integer.valueOf(item.toString()));
                }
            });
        }
        if (goodsIds.isEmpty()) {
            return Collections.emptyList();
        }
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop nss ON g.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,g.goods_name,g.sub_title,g.goods_category,g.goods_type,g.goods_cover,g.unit,g.status,g.sale_num,g.member_discount,g.vip_card_discount,g.is_discount,g.virtual_receive_type");
        queryWrapper.in("g.goods_id", goodsIds);
        queryWrapper.eq("g.status", GoodsStatusEnum.ONLINE.getStatus());
        queryWrapper.eq("g.audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
        queryWrapper.eq("g.delete_time", 0);
        queryWrapper.eq("nss.business_status", ShopStatusEnum.ON.getCode());
        queryWrapper.orderByDesc("g.sale_num");
        List<MallGoodsListVo> list = mallGoodsMapper.selectJoinList(MallGoodsListVo.class, queryWrapper);
        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId())) {
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
        }
        for (MallGoodsListVo item : list) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .eq("goods_id", item.getGoodsId()).eq("is_default", 1));
            if (goodsSku == null) {
                continue;
            }
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? JSONUtil.parseObj(goodsSku.getMemberPrice()) : new JSONObject();
                goodsSkuVo.setMemberPrice(calculateMemberPrice(memberLevel, item.getMemberDiscount(), memberPriceConfig, goodsSku.getPrice()));
            } else {
                goodsSkuVo.setMemberPrice(goodsSku.getPrice());
            }
            ShowPriceVo skuShowPrice = buildSkuShowPrice(item.getMemberDiscount(), item.getVipCardDiscount(), goodsSku,
                    goodsSkuVo.getPrice(), goodsSkuVo.getSalePrice(),
                    ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice() : null);
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            item.setGoodsSku(goodsSkuVo);
        }
        return list;
    }

    /**
     * @param skuId 规格Id
     * @return 商品规格
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
        ShowPriceVo detailShowPrice = buildSkuShowPrice(goods.getMemberDiscount(), goods.getVipCardDiscount(), sku,
                sku.getPrice(), sku.getSalePrice(), sku.getMemberPrice());
        vo.setShowPrice(detailShowPrice.getShowPrice());
        vo.setShowType(detailShowPrice.getShowType());
        // 查询商品SKU列表
        List<MallGoodsSkuVo.Sku> skuListVo = new LinkedList<>();
        List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", sku.getGoodsId()));
        for (MallGoodsSku item : skuList) {
            MallGoodsSkuVo.Sku skuVo = new MallGoodsSkuVo.Sku();
            BeanUtils.copyProperties(item, skuVo);

            ShowPriceVo skuShowPrice = buildSkuShowPrice(goods.getMemberDiscount(), goods.getVipCardDiscount(), item,
                    item.getPrice(), item.getSalePrice(), item.getMemberPrice());
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
                .leftJoin("?_site_shop nss ON g.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num + g.virtual_sale_num as sale_num,status,g.sort,g.create_time,member_discount,vip_card_discount,virtual_verify_type,unit,is_discount,label_ids,g.stock,g.brand_id, ns.site_name,nss.is_self,g.max_buy,g.min_buy,g.is_limit,g.limit_type");

        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
        queryWrapper.eq("g.delete_time", 0);
        queryWrapper.eq("nss.business_status", ShopStatusEnum.ON.getCode());

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
            ShowPriceVo skuShowPrice = buildSkuShowPrice(vo.getMemberDiscount(), vo.getVipCardDiscount(), goodsSku,
                    goodsSkuVo.getPrice(), goodsSkuVo.getSalePrice(),
                    ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice() : null);
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            goodsSkuVo.setMemberDiscount(vo.getMemberDiscount());
            goodsSkuVo.setVipCardDiscount(vo.getVipCardDiscount());
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
        return PageResult.build(page, limit, iPage.getTotal(), list);
    }

    @Override
    public List<com.niu.core.service.api.shop.vo.MallGoodsListVo> selectGoodsAndSkuList(MallShopGoodsListEventDefiner.MallShopGoodsListEvent event) {
        Integer siteId = event.getSiteId();
        MallShopGoodsListEventDefiner.MallShopGoodsListEventResult result = new MallShopGoodsListEventDefiner.MallShopGoodsListEventResult();
        List<com.niu.core.service.api.shop.vo.MallGoodsListVo> goods = new ArrayList<>();
        LambdaQueryWrapper<MallGoods> goodsWrapper = new LambdaQueryWrapper<>();
        goodsWrapper.eq(MallGoods::getSiteId, siteId);
        goodsWrapper.eq(MallGoods::getStatus, GoodsStatusEnum.ONLINE.getStatus());
        goodsWrapper.eq(MallGoods::getAuditStatus, GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
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
                    ShowPriceVo skuShowPrice = buildSkuShowPrice(good.getMemberDiscount(), good.getVipCardDiscount(), mallGoodsSkus,
                            skuListVo.getPrice(), skuListVo.getSalePrice(),
                            ObjectUtil.isNotEmpty(mallGoodsSkus.getMemberPrice()) ? mallGoodsSkus.getMemberPrice() : null);
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

        queryWrapper.select("g.goods_id,g.site_id,goods_name,sub_title,goods_category,goods_type,goods_cover,g.sale_num,g.sale_num + g.virtual_sale_num as sale_num,status,member_discount,vip_card_discount,is_discount,virtual_receive_type");

        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
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
            if (goodsSkuThis.isEmpty()) {
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
            ShowPriceVo skuShowPrice = buildSkuShowPrice(vo.getMemberDiscount(), vo.getVipCardDiscount(), goodsSku,
                    goodsSkuVo.getPrice(), goodsSkuVo.getSalePrice(),
                    ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? goodsSku.getMemberPrice() : null);
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());
            goodsSkuVo.setMemberDiscount(vo.getMemberDiscount());
            goodsSkuVo.setVipCardDiscount(vo.getVipCardDiscount());
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

    @Resource
    private IMallDiscountService discountService;
    @Resource
    private ICoreFullReductionService coreFullReductionService;
    @Resource
    private ICoreFullGiftService coreFullGiftService;

    @Override
    public List<String> getSkuPromotionList(Integer skuId) {
        List<String> promotionList = new ArrayList<>();

        // 查询SKU及关联商品
        MallGoodsSku sku = goodsSkuMapper.selectOne(new LambdaQueryWrapper<MallGoodsSku>()
                .eq(MallGoodsSku::getSkuId, skuId)
                .select(MallGoodsSku::getSkuId, MallGoodsSku::getGoodsId, MallGoodsSku::getSiteId, MallGoodsSku::getPrice)
                .last("limit 1"));
        if (sku == null) {
            return promotionList;
        }
        // 查询商品信息（用于分类、品牌等）
        MallGoods goods = goodsMapper.selectById(sku.getGoodsId());
        if (goods == null) {
            return promotionList;
        }

        // 1. 限时折扣
        MallDiscountGoodsInfoVo discountInfo = discountService.getInfoByGoods(sku.getGoodsId());
        if (discountInfo != null) {
            String activeGoodsValue = discountInfo.getActiveGoodsValue();
            try {
                List<Map<String, Object>> activeGoodsList = JSON.parseObject(activeGoodsValue, new TypeReference<>() {
                });
                Map<Long, Map<String, Object>> activeSkuMap = activeGoodsList.stream()
                        .collect(Collectors.toMap(m -> Long.parseLong(m.get("sku_id").toString()), m -> m));
                Map<String, Object> activeSku = activeSkuMap.get(Long.valueOf(skuId));
                if (activeSku != null && activeSku.containsKey("discount_rate")) {
                    String discountRate = activeSku.get("discount_rate").toString();
                    promotionList.add("限时" + discountRate + "折");
                }
            } catch (Exception e) {
                // ignore
            }
        }

        // 2. 满减
        FullReductionCalculateVo fullReductionInfo = coreFullReductionService.getFullReductionInfoOnlyCheck(
                sku.getGoodsId(), skuId, RequestUtils.memberId(), sku.getSiteId());
        if (fullReductionInfo != null) {
            String promotionType = fullReductionInfo.getPromotionType();
            String ruleType = fullReductionInfo.getRuleType();
            String typeUnit = FullReductionTypeEnum.NUM.getType().equals(fullReductionInfo.getType()) ? "件" : "元";
            List<Map<String, Object>> ruleJson = JSON.parseObject(fullReductionInfo.getRuleJson(), new TypeReference<>() {
            });
            if (ruleJson != null) {
                for (Map<String, Object> rule : ruleJson) {
                    BigDecimal amount = new BigDecimal(rule.get("amount").toString());
                    BigDecimal reduce = new BigDecimal(rule.get("reduce").toString());
                    String ruleTypeName = "ladder".equals(ruleType) ? "满" : "每满";
                    String promotionTypeName1 = "discount".equals(promotionType) ? "打" : "减";
                    String promotionTypeName2 = "discount".equals(promotionType) ? "折" : "元";
                    promotionList.add(ruleTypeName + amount + typeUnit + promotionTypeName1 + reduce + promotionTypeName2);
                }
            }
        }

        // 3. 满赠
        Map<String, Object> fullGiftInfo = coreFullGiftService.getFullGiftInfoOnlyCheck(
                sku.getGoodsId(), skuId, RequestUtils.memberId(), sku.getSiteId());
        if (fullGiftInfo != null && !fullGiftInfo.isEmpty()) {
            String ruleType = (String) fullGiftInfo.get("rule_type");
            List<Map<String, Object>> ruleJson = JSON.parseObject(fullGiftInfo.get("rule_json").toString(), new TypeReference<>() {
            });
            if (ruleJson != null) {
                for (Map<String, Object> rule : ruleJson) {
                    BigDecimal amount = new BigDecimal(rule.get("amount").toString());
                    String ruleTypeName = "ladder".equals(ruleType) ? "满" : "每满";
                    promotionList.add(ruleTypeName + amount + "元送赠品");
                }
            }
        }

        // 4. 优惠券
        // 查询与商品相关的优惠券ID（商品、分类、平台分类、品牌）
        Set<Integer> couponIds = new HashSet<>();

        // 商品券
        if (sku.getGoodsId() != null) {
            List<MallCouponGoods> goodsCoupons = mallCouponGoodsMapper.selectList(
                    new LambdaQueryWrapper<MallCouponGoods>().eq(MallCouponGoods::getGoodsId, sku.getGoodsId()));
            if (!goodsCoupons.isEmpty()) {
                couponIds.addAll(goodsCoupons.stream().map(MallCouponGoods::getCouponId).collect(Collectors.toSet()));
            } else {
                couponIds.add(-1); // 占位，确保后面in查询为空
            }
        }

        // 分类券（商品分类）
        if (goods.getGoodsCategory() != null && !goods.getGoodsCategory().isEmpty()) {
            List<Integer> categoryIds = JSONUtil.toList(goods.getGoodsCategory(), Integer.class);
            if (!categoryIds.isEmpty()) {
                List<MallCouponGoods> categoryCoupons = mallCouponGoodsMapper.selectList(
                        new LambdaQueryWrapper<MallCouponGoods>().in(MallCouponGoods::getCategoryId, categoryIds));
                if (!categoryCoupons.isEmpty()) {
                    couponIds.addAll(categoryCoupons.stream().map(MallCouponGoods::getCouponId).toList());
                } else {
                    couponIds.add(-1);
                }
            }
        }

        // 平台分类券
        if (goods.getGoodsMallCategory() != null) {
            List<MallCouponGoods> mallCategoryCoupons = mallCouponGoodsMapper.selectList(
                    new LambdaQueryWrapper<MallCouponGoods>().eq(MallCouponGoods::getMallCategoryId, goods.getGoodsMallCategory()));
            if (!mallCategoryCoupons.isEmpty()) {
                couponIds.addAll(mallCategoryCoupons.stream().map(MallCouponGoods::getCouponId).toList());
            } else {
                couponIds.add(-1);
            }
        }

        // 品牌券
        if (goods.getBrandId() != null && goods.getBrandId() > 0) {
            List<MallCouponGoods> brandCoupons = mallCouponGoodsMapper.selectList(
                    new LambdaQueryWrapper<MallCouponGoods>().eq(MallCouponGoods::getBrandId, goods.getBrandId()));
            if (!brandCoupons.isEmpty()) {
                couponIds.addAll(brandCoupons.stream().map(MallCouponGoods::getCouponId).toList());
            } else {
                couponIds.add(-1);
            }
        }

        // 查询可用优惠券（通用券 + 上述各类券）
        List<MallCoupon> coupons = mallCouponMapper.selectList(new LambdaQueryWrapper<MallCoupon>()
                .eq(MallCoupon::getReceiveType, CouponReceiveTypeEnum.USER.getType())
                .eq(MallCoupon::getStatus, CouponStatusEnum.NORMAL.getStatus())
                .in(MallCoupon::getSiteId, Arrays.asList(sku.getSiteId(), 0))
                .and(wrapper -> wrapper.eq(MallCoupon::getType, CouponTypeEnum.ALL.getType())
                        .or().in(MallCoupon::getId, couponIds)
                )
        );

        for (MallCoupon coupon : coupons) {
            BigDecimal minCondition = coupon.getMinConditionMoney();
            BigDecimal price = coupon.getPrice();
            if (minCondition.compareTo(BigDecimal.ZERO) == 0) {
                promotionList.add("无门槛立减" + price + "元");
            } else {
                promotionList.add("满" + minCondition + "减" + price + "元");
            }
        }

        return promotionList;
    }

    @Override
    public SkuPromotionDetailVo getSkuPromotionDetail(Integer skuId, Integer num) {
        SkuPromotionDetailVo result = new SkuPromotionDetailVo();
        // 价格明细
        SkuPromotionDetailVo.PriceDetails priceDetails = new SkuPromotionDetailVo.PriceDetails();
        priceDetails.setHandPrice(BigDecimal.ZERO);
        SkuPromotionDetailVo.PriceList priceList = new SkuPromotionDetailVo.PriceList();
        priceList.setOriginal_price(new SkuPromotionDetailVo.PriceListData(BigDecimal.ZERO, "商品售价"));
        priceList.setSite_promotion(new SkuPromotionDetailVo.PriceListData(BigDecimal.ZERO, "店铺优惠"));
        priceList.setAdmin_promotion(new SkuPromotionDetailVo.PriceListData(BigDecimal.ZERO, "平台优惠"));
        priceDetails.setPriceList(priceList);
        result.setPriceDetails(priceDetails);

        result.setActivityList(new HashMap<>());
        result.setCouponList(new ArrayList<>());

        // 查询SKU及商品
        MallGoodsSku sku = goodsSkuMapper.selectOne(new LambdaQueryWrapper<MallGoodsSku>()
                .eq(MallGoodsSku::getSkuId, skuId)
                .select(MallGoodsSku::getSkuId, MallGoodsSku::getGoodsId, MallGoodsSku::getSiteId, MallGoodsSku::getPrice));
        if (sku == null) {
            return result;
        }
        MallGoods goods = goodsMapper.selectById(sku.getGoodsId());
        if (goods == null) {
            return result;
        }

        BigDecimal originalPrice = sku.getPrice();
        BigDecimal sitePromotionPrice = BigDecimal.ZERO;
        BigDecimal adminPromotionPrice = BigDecimal.ZERO;

        // 限时折扣
        MallDiscountGoodsInfoVo discountInfo = discountService.getInfoByGoods(sku.getGoodsId());
        if (discountInfo != null) {
            String activeGoodsValue = discountInfo.getActiveGoodsValue();
            if (!activeGoodsValue.isEmpty()) {
                try {
                    List<Map<String, Object>> activeGoodsList = JSON.parseObject(activeGoodsValue, new TypeReference<>() {
                    });
                    Map<Long, Map<String, Object>> activeSkuMap = activeGoodsList.stream()
                            .collect(Collectors.toMap(m -> Long.parseLong(m.get("sku_id").toString()), m -> m));
                    Map<String, Object> activeSku = activeSkuMap.get(Long.valueOf(skuId));
                    if (activeSku != null && activeSku.containsKey("reduce_money")) {
                        BigDecimal reduceMoney = new BigDecimal(activeSku.get("reduce_money").toString());
                        sitePromotionPrice = sitePromotionPrice.add(reduceMoney);
                        // 构建活动信息
                        Map<String, Object> activity = new HashMap<>();
                        activity.put("activity_id", discountInfo.getActiveId());
                        activity.put("name", "限时折扣");
                        activity.put("title", "限时" + activeSku.get("discount_rate") + "折");

                        String startTime = Instant.ofEpochSecond(discountInfo.getStartTime())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String endTime = Instant.ofEpochSecond(discountInfo.getEndTime())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        activity.put("activity_time", startTime.split(" ")[0] + " - " + endTime.split(" ")[0]);
                        result.getActivityList().put("discount", activity);
                    }
                } catch (Exception e) { /* ignore */ }
            }
        }

        // 计算优惠后的商品金额（用于后续满减、满赠、积分抵扣的门槛）
        BigDecimal goodsMoney = originalPrice.subtract(sitePromotionPrice).subtract(adminPromotionPrice)
                .max(BigDecimal.ZERO);

        // 优惠券
        List<Map<String, Object>> couponList = couponService.getGoodsDetailCouponLists(sku.getGoodsId());
        if (!CollectionUtils.isEmpty(couponList)) {
            result.setCouponList(couponList);
            Map<String, BigDecimal> couponPromotion = couponService.calcMemberCouponPromotion(couponList, goodsMoney, RequestUtils.memberId());
            BigDecimal siteCouponPrice = couponPromotion.getOrDefault("site_coupon_price", BigDecimal.ZERO);
            BigDecimal adminCouponPrice = couponPromotion.getOrDefault("admin_coupon_price", BigDecimal.ZERO);
            sitePromotionPrice = sitePromotionPrice.add(siteCouponPrice);
            adminPromotionPrice = adminPromotionPrice.add(adminCouponPrice);
        }

        // 重新计算商品金额
        int previewNum = resolveSkuPromotionPreviewNum(skuId, num);
        BigDecimal lineGoodsMoney = goodsMoney.multiply(BigDecimal.valueOf(previewNum));

        // 满减
        FullReductionCalculateVo fullReductionInfo = coreFullReductionService.getFullReductionInfoOnlyCheck(
                sku.getGoodsId(), skuId, RequestUtils.memberId(), sku.getSiteId());
        if (fullReductionInfo != null) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("activity_id", fullReductionInfo.getId());
            activity.put("name", "满减活动");
            String startTime = Instant.ofEpochSecond(fullReductionInfo.getStartTime())
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String endTime = Instant.ofEpochSecond(fullReductionInfo.getEndTime())
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            activity.put("activity_time", startTime.split(" ")[0] + " - " + endTime.split(" ")[0]);
            String promotionType = fullReductionInfo.getPromotionType();
            String ruleType = fullReductionInfo.getRuleType();
            String reductionType = fullReductionInfo.getType();
            String typeUnit = FullReductionTypeEnum.NUM.getType().equals(reductionType) ? "件" : "元";
            List<FullReductionParam.Rule> ruleJson = JSON.parseObject(fullReductionInfo.getRuleJson(), new TypeReference<List<FullReductionParam.Rule>>() {
            });
            StringBuilder titleBuilder = new StringBuilder();
            if (ruleJson != null) {
                for (int i = 0; i < ruleJson.size(); i++) {
                    FullReductionParam.Rule rule = ruleJson.get(i);
                    BigDecimal amount = ObjectUtil.defaultIfNull(rule.getAmount(), BigDecimal.ZERO);
                    BigDecimal reduce = ObjectUtil.defaultIfNull(rule.getReduce(), BigDecimal.ZERO);
                    String ruleTypeName = "ladder".equals(ruleType) ? "满" : "每满";
                    String promotionTypeName1 = "discount".equals(promotionType) ? "打" : "减";
                    String promotionTypeName2 = "discount".equals(promotionType) ? "折" : "元";
                    if (i > 0) titleBuilder.append(",");
                    titleBuilder.append(ruleTypeName).append(amount).append(typeUnit)
                            .append(promotionTypeName1).append(reduce).append(promotionTypeName2);
                }
            }
            // 满减预估：满件按件数，满元按优惠后行金额
            BigDecimal matchedTotal = FullReductionTypeEnum.NUM.getType().equals(reductionType)
                    ? BigDecimal.valueOf(previewNum) : lineGoodsMoney;
            BigDecimal tempPrice = coreFullReductionService.calculateFullReductionDiscount(
                    reductionType, ruleType, promotionType, ruleJson, matchedTotal, lineGoodsMoney);
            activity.put("title", titleBuilder.toString());
            sitePromotionPrice = sitePromotionPrice.add(tempPrice);
            result.getActivityList().put("full_reduction", activity);
        }

        // 满赠
        Map<String, Object> fullGiftInfo = coreFullGiftService.getFullGiftInfoOnlyCheck(
                sku.getGoodsId(), skuId, RequestUtils.memberId(), sku.getSiteId());
        if (fullGiftInfo != null && !fullGiftInfo.isEmpty()) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("activity_id", fullGiftInfo.get("id"));
            activity.put("name", "满赠活动");
            String startTime = Instant.ofEpochSecond((Long) fullGiftInfo.get("start_time"))
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String endTime = Instant.ofEpochSecond((Long) fullGiftInfo.get("end_time"))
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            activity.put("activity_time", startTime.split(" ")[0] + " - " + endTime.split(" ")[0]);
            String ruleType = (String) fullGiftInfo.get("rule_type");
            List<Map<String, Object>> ruleJson = JSON.parseObject(fullGiftInfo.get("rule_json").toString(), new TypeReference<>() {
            });
            StringBuilder titleBuilder = new StringBuilder();
            List<Map<String, Object>> giftList = new ArrayList<>();
            if (ruleJson != null) {
                for (int i = 0; i < ruleJson.size(); i++) {
                    Map<String, Object> rule = ruleJson.get(i);
                    BigDecimal amount = new BigDecimal(rule.get("amount").toString());
                    String ruleTypeName = "ladder".equals(ruleType) ? "满" : "每满";
                    if (i > 0) titleBuilder.append(",");
                    titleBuilder.append(ruleTypeName).append(amount).append("元送赠品");
                    if (lineGoodsMoney.compareTo(amount) >= 0) {
                        // 获取赠品SKU列表（历史数据或部分规则可能无 gift_sku_data / sku_list）
                        List<Map<String, Object>> giftSkuData = (List<Map<String, Object>>) rule.get("giftSkuData");
                        List<Map<String, Object>> skuList = new ArrayList<>();
                        if (ObjectUtil.isNotEmpty(giftSkuData)) {
                            for (Map<String, Object> gift : giftSkuData) {
                                if (gift == null) {
                                    continue;
                                }
                                List<Map<String, Object>> skuItems = (List<Map<String, Object>>) gift.get("sku_list");
                                if (ObjectUtil.isNotEmpty(skuItems)) {
                                    skuList.addAll(skuItems);
                                }
                            }
                        }
                        if (!skuList.isEmpty()) {
                            List<Integer> skuIds = skuList.stream()
                                    .map(m -> Integer.parseInt(m.get("sku_id").toString()))
                                    .collect(Collectors.toList());
                            List<MallGoodsSku> giftSkus = goodsSkuMapper.selectList(new LambdaQueryWrapper<MallGoodsSku>()
                                    .in(MallGoodsSku::getSkuId, skuIds)
                                    .select(MallGoodsSku::getSkuId, MallGoodsSku::getGoodsId, MallGoodsSku::getSkuName));
                            Map<Integer, MallGoodsSku> skuMap = giftSkus.stream()
                                    .collect(Collectors.toMap(MallGoodsSku::getSkuId, s -> s));
                            for (Map<String, Object> skuItem : skuList) {
                                Integer sid = Integer.parseInt(skuItem.get("sku_id").toString());
                                MallGoodsSku gs = skuMap.get(sid);
                                if (gs != null) {
                                    skuItem.put("goods_id", gs.getGoodsId());
                                    skuItem.put("sku_name", gs.getSkuName());
                                    // 查询商品名称和封面
                                    MallGoods g = goodsMapper.selectById(gs.getGoodsId());
                                    if (g != null) {
                                        skuItem.put("goods_name", g.getGoodsName());
                                        skuItem.put("goods_cover", g.getGoodsCover());
                                    }
                                }
                                if ("cycle".equals(ruleType)) {
                                    BigDecimal giftNum = new BigDecimal(skuItem.get("num").toString());
                                    BigDecimal times = new BigDecimal(Math.floor(lineGoodsMoney.divide(amount, 2, RoundingMode.DOWN).doubleValue()));
                                    skuItem.put("num", giftNum.multiply(times).intValue());
                                }
                            }
                            giftList = skuList;
                        }
                    }
                }
            }
            activity.put("title", titleBuilder.toString());
            activity.put("gift_list", giftList);
            result.getActivityList().put("full_gift", activity);
        }

        // 积分抵扣
        // 过滤不可抵扣商品
        String pointDeductType = goods.getPointDeductType();
        BigDecimal pointDeductRate = goods.getPointDeductRate() != null ? goods.getPointDeductRate() : BigDecimal.ZERO;
        boolean canPointDeduct = !("custom".equals(pointDeductType) && pointDeductRate.compareTo(BigDecimal.ZERO) == 0);
        if (canPointDeduct) {
            Integer memberId = RequestUtils.memberId();
            Member member = coreMemberAccountService.getMemberAccountInfo(memberId);
            Integer memberPoint = Objects.nonNull(coreMemberAccountService.getMemberAccountInfo(memberId)) ? coreMemberAccountService.getMemberAccountInfo(memberId).getPoint() : 0;
            PointDeduct pointDeductConfig = corePointDeductService.getPointDeductConfig();
            PointDeduct sitePointDeductConfig = corePointDeductService.getSitePointDeductConfig(sku.getSiteId());
            if ("site".equals(pointDeductType)) {
                pointDeductRate = sitePointDeductConfig.getDeductRate();
            }
            // 重新计算商品金额（可能已被之前的优惠影响）
            BigDecimal currentGoodsMoney = originalPrice.subtract(sitePromotionPrice).subtract(adminPromotionPrice).max(BigDecimal.ZERO);
            BigDecimal price = currentGoodsMoney.multiply(pointDeductRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            DeductMoneyVo deductInfo = corePointDeductService.calculateDeductMoney(memberPoint, price, pointDeductConfig, null, null);
            BigDecimal deductMoney = deductInfo.getDeductMoney();
            adminPromotionPrice = adminPromotionPrice.add(deductMoney);
            if (deductMoney.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> pointActivity = new HashMap<>();
                pointActivity.put("activity_id", "");
                pointActivity.put("name", "积分抵扣");
                pointActivity.put("title", "积分抵扣" + deductMoney + "元");
                result.getActivityList().put("point_deduct", pointActivity);
            }
        }

        // 更新价格明细
        priceDetails.getPriceList().getOriginal_price().setPrice(originalPrice);
        priceDetails.getPriceList().getSite_promotion().setPrice(sitePromotionPrice);
        priceDetails.getPriceList().getAdmin_promotion().setPrice(adminPromotionPrice);
        BigDecimal lineOriginal = originalPrice.multiply(BigDecimal.valueOf(previewNum));
        BigDecimal lineHandPrice = lineOriginal.subtract(sitePromotionPrice).subtract(adminPromotionPrice).max(BigDecimal.ZERO);
        BigDecimal handPrice = previewNum > 1
                ? lineHandPrice.divide(BigDecimal.valueOf(previewNum), 2, RoundingMode.DOWN)
                : lineHandPrice;
        priceDetails.setHandPrice(handPrice);

        result.setPriceDetails(priceDetails);
        return result;
    }

    @Resource
    private ICorePointDeductService corePointDeductService;
    @Resource
    private ICoreMemberAccountService coreMemberAccountService;
    @Resource
    private MallCartMapper mallCartMapper;

    private int resolveSkuPromotionPreviewNum(Integer skuId, Integer num) {
        if (num != null && num > 0) {
            return num;
        }
        Integer memberId = RequestUtils.memberId();
        if (memberId == null || memberId <= 0) {
            return 1;
        }
        MallCart cart = mallCartMapper.selectOne(new LambdaQueryWrapper<MallCart>()
                .eq(MallCart::getMemberId, memberId)
                .eq(MallCart::getSkuId, skuId)
                .last("limit 1"));
        if (cart != null && cart.getNum() != null && cart.getNum() > 0) {
            return cart.getNum();
        }
        return 1;
    }
    @Resource
    private ICoreUploadService coreUploadService;

    @Override
    public List<ApiVipCardGoodsVo> getVipCardGoodsList(Integer num) {
        List<Integer> circleIds = RequestUtils.circleIds(); // 当前用户所属商圈ID

        // 1. 构建多表关联查询
        MPJQueryWrapper<MallGoods> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("g")
                .leftJoin("?_mall_goods_sku sku ON g.goods_id = sku.goods_id AND sku.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop shop ON g.site_id = shop.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site s ON g.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix))
                .select("g.site_id, g.goods_id, g.goods_name, g.sub_title, g.goods_category, " +
                        "g.goods_type, g.goods_cover, g.unit, g.status, g.sale_num, " +
                        "g.member_discount, g.vip_card_discount, g.is_discount, g.virtual_receive_type, " +
                        "sku.sku_id AS sku_id, sku.price AS price, sku.cost_price AS cost_price, " +
                        "sku.member_price AS member_price, sku.sale_price AS sale_price, " +
                        "sku.svip_discount AS svip_discount, sku.svip_price AS svip_price, " +
                        "s.site_id AS site_site_id, s.site_name AS site_name")
                .eq("shop.business_status", ShopStatusEnum.ON.getCode())
                .isNotNull("g.vip_card_discount")
                .ne("g.vip_card_discount", "")
                .eq("sku.is_default", 1)//goods_sku_is_default
                .eq("g.status", GoodsStatusEnum.ONLINE.getStatus())
                .eq("g.audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());

        // 商圈过滤
        if (circleIds != null && !circleIds.isEmpty()) {
            wrapper.in("shop.circle_id", circleIds);
        }

        // 排序
        wrapper.orderByDesc("g.sale_num");

        // 限制数量
        if (num != null && num > 0) {
            wrapper.last("limit " + num);
        }

        // 2. 执行查询，映射到联表实体
        List<MallVipCardGoodsJoinVo> resultList = goodsMapper.selectJoinList(MallVipCardGoodsJoinVo.class, wrapper);
        if (CollUtil.isEmpty(resultList)) {
            return List.of();
        }

        // 3. 转换为 VO 并处理价格
        List<ApiVipCardGoodsVo> voList = resultList.stream().map(row -> {
            ApiVipCardGoodsVo vo = new ApiVipCardGoodsVo();
            BeanUtil.copyProperties(row, vo);
            // 填充站点信息
            ApiVipCardGoodsVo.SiteInfo siteInfo = new ApiVipCardGoodsVo.SiteInfo();
            siteInfo.setSiteId(row.getSiteSiteId());
            siteInfo.setSiteName(row.getSiteName());
            vo.setSite(siteInfo);

            // 填充 SKU 信息
            ApiVipCardGoodsVo.GoodsSkuInfo skuInfo = new ApiVipCardGoodsVo.GoodsSkuInfo();
            skuInfo.setSkuId(row.getSkuId());
            skuInfo.setPrice(row.getPrice());
            skuInfo.setCostPrice(row.getCostPrice());
            skuInfo.setSalePrice(row.getSalePrice());
            skuInfo.setMemberPrice(row.getMemberPrice());
            skuInfo.setMemberDiscount(vo.getMemberDiscount());
            skuInfo.setVipCardDiscount(vo.getVipCardDiscount());
            skuInfo.setSvipPrice(row.getSvipPrice());
            skuInfo.setSvipDiscount(row.getSvipDiscount());
            // 调用价格服务获取展示价格
            MallGoodsSku vipSku = new MallGoodsSku();
            vipSku.setGoodsId(vo.getGoodsId());
            vipSku.setSkuId(Math.toIntExact(skuInfo.getSkuId()));
            vipSku.setSvipDiscount(row.getSvipDiscount());
            vipSku.setSvipPrice(row.getSvipPrice());
            ShowPriceVo skuShowPrice = buildSkuShowPrice(vo.getMemberDiscount(), vo.getVipCardDiscount(), vipSku,
                    skuInfo.getPrice(), skuInfo.getSalePrice(),
                    ObjectUtil.isNotEmpty(skuInfo.getMemberPrice()) ? skuInfo.getMemberPrice() : null);
            skuInfo.setShowPrice(skuShowPrice.getShowPrice());
            skuInfo.setShowType(skuShowPrice.getShowType());
            if (skuInfo.getShowPrice() != null && skuInfo.getPrice() != null
                    && skuInfo.getShowPrice().compareTo(skuInfo.getPrice()) < 0) {
                skuInfo.setCostPrice(skuInfo.getPrice());
            }
            vo.setGoodsSku(skuInfo);
            vo.setCostPrice(skuInfo.getCostPrice());

            // 其他派生字段
            vo.setGoodsTypeName(GoodsTypeEnum.getNameByType(vo.getGoodsType()));
            // 缩略图（若需要，可调用图片服务）
            vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(),
                    UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
            vo.setGoodsCoverThumbMid(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(),
                    UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
            return vo;
        }).collect(Collectors.toList());

        return voList;
    }

    private ShowPriceVo buildSkuShowPrice(String memberDiscount, String vipCardDiscount, MallGoodsSku sku,
                                          BigDecimal price, BigDecimal salePrice, String memberPrice) {
        return coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                .goodsId(sku.getGoodsId())
                .skuId(sku.getSkuId())
                .price(price)
                .salePrice(salePrice)
                .memberDiscount(memberDiscount)
                .memberPrice(memberPrice)
                .vipCardDiscount(vipCardDiscount)
                .svipDiscount(sku.getSvipDiscount())
                .svipPrice(sku.getSvipPrice())
                .memberId(RequestUtils.memberId())
                .build());
    }
}
