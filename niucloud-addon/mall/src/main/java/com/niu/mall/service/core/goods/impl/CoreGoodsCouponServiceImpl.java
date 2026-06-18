package com.niu.mall.service.core.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.mall.entity.coupon.MallCoupon;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.enums.coupon.CouponIsAllSiteEnum;
import com.niu.mall.enums.goods.GoodsAuditStatusEnum;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.mapper.coupon.MallCouponGoodsMapper;
import com.niu.mall.mapper.coupon.MallCouponMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.core.goods.ICoreGoodsCouponService;
import com.niu.mall.service.core.goods.vo.CouponGoodsVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoreGoodsCouponServiceImpl implements ICoreGoodsCouponService {

    @Resource
    private MallGoodsMapper mallGoodsMapper;

    @Resource
    private MallCouponGoodsMapper mallCouponGoodsMapper;

    @Resource
    private MallCouponMapper mallCouponMapper;


    @Override
    public List<CouponGoodsVo> getCouponGoodsOne(Integer couponId) {
        List<CouponGoodsVo> resultListAll = new ArrayList<>();
        //查询优惠券
        MallCoupon coupon = mallCouponMapper.selectById(couponId);
        if (ObjectUtil.isNotEmpty(coupon)) {
            MPJQueryWrapper<MallGoods> couponGoodsWrapper = new MPJQueryWrapper<>();
            couponGoodsWrapper.setAlias("g")
                    .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                    .innerJoin("?_site_shop s ON g.site_id = s.site_id ".replace("?_", GlobalConfig.tablePrefix));
            couponGoodsWrapper.select("g.site_id,g.goods_id,g.goods_cover");
            couponGoodsWrapper.eq("s.business_status", SiteStatusEnum.ON.getCode());
            couponGoodsWrapper.eq("g.status", GoodsStatusEnum.ONLINE.getStatus());
            couponGoodsWrapper.eq("g.audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
            QueryWrapper<MallCouponGoods> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("coupon_id", couponId);
            List<MallCouponGoods> couponGoodsList = mallCouponGoodsMapper.selectList(queryWrapper);
            if (!couponGoodsList.isEmpty()) {
                List<Integer> goodsIdList = couponGoodsList.stream().filter(i -> ObjectUtil.isNotEmpty(i.getGoodsId()) && i.getGoodsId() > 0).map(MallCouponGoods::getGoodsId).toList();
                List<Integer> categoryIds = couponGoodsList.stream().filter(i -> ObjectUtil.isNotEmpty(i.getCategoryId()) && i.getCategoryId() > 0).map(MallCouponGoods::getCategoryId).toList();
                List<Integer> mallCategoryIds = couponGoodsList.stream().filter(i -> ObjectUtil.isNotEmpty(i.getMallCategoryId()) && i.getMallCategoryId() > 0).map(MallCouponGoods::getMallCategoryId).toList();
                List<Integer> brandIds = couponGoodsList.stream().filter(i -> ObjectUtil.isNotEmpty(i.getBrandId()) && i.getBrandId() > 0).map(MallCouponGoods::getBrandId).toList();
                if (!goodsIdList.isEmpty()) {
                    couponGoodsWrapper.in("g.goods_id", goodsIdList);
                } else if (!categoryIds.isEmpty()) {
                    couponGoodsWrapper.and(i -> categoryIds.forEach(j -> i.like("g.goods_category", j).or()));
                } else if (!mallCategoryIds.isEmpty()) {
                    couponGoodsWrapper.in("g.goods_mall_category", mallCategoryIds);
                } else if (!brandIds.isEmpty()) {
                    couponGoodsWrapper.in("g.brand_id", brandIds);
                }
            }

            if (ObjectUtil.isNotEmpty(coupon.getSiteId()) && coupon.getSiteId() > 0) {
                couponGoodsWrapper.eq("g.site_id", coupon.getSiteId());
            } else {
                if (coupon.getIsAllSiteJoin().equals(CouponIsAllSiteEnum.NOT_ALL_SITE_JOIN.getType())) {
                    JSONArray siteIds = JSONUtil.parseArray(coupon.getJoinSiteIds());
                    couponGoodsWrapper.in("g.site_id", siteIds);
                }
            }
            List<CouponGoodsVo> resultList = mallGoodsMapper.selectJoinList(CouponGoodsVo.class, couponGoodsWrapper);
            if (!resultList.isEmpty()) {
                resultList.forEach(i -> {
                    i.setCouponId(couponId);
                    CouponGoodsVo.goodsSkuInfo goodsSku = new CouponGoodsVo.goodsSkuInfo();
                    goodsSku.setGoodsId(i.getGoodsId());
                    i.setGoodsSku(goodsSku);

                    CouponGoodsVo.ShopInfo site = new CouponGoodsVo.ShopInfo();
                    site.setSiteId(i.getSiteId());
                    i.setShop(site);
                });
            }
            resultListAll.addAll(resultList);
        }

        return resultListAll;
    }
}
