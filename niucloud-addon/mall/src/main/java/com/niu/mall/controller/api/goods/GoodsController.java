package com.niu.mall.controller.api.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.api.goods.IGoodsService;
import com.niu.mall.service.api.goods.param.MallGoodsComponentsSearchParam;
import com.niu.mall.service.api.goods.param.MallGoodsDetailParam;
import com.niu.mall.service.api.goods.param.MallGoodsSearchParam;
import com.niu.mall.service.api.goods.param.ShopComponentsSearchParam;
import com.niu.mall.service.api.goods.vo.*;
import com.niu.mall.service.api.marketing.IMallCouponGoodsService;
import com.niu.mall.service.api.marketing.param.MallGoodsCouponSearchParam;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/mall")
public class GoodsController {

    @Resource
    IGoodsService goodsService;

    @Resource
    IMallCouponGoodsService mallCouponGoodsService;

    /**
     * 获取商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/goods/pages")
    public Result<PageResult<MallGoodsListVo>> page(@RequestParam Map<String, String> allParams, @Validated PageParam pageParam, @Validated MallGoodsSearchParam searchParam) {
        PageResult<MallGoodsListVo> list = goodsService.page(pageParam, searchParam, allParams);
        return Result.success(list);
    }

    /**
     * 店铺商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/goods_shop/pages")
    public Result<PageResult<MallGoodsListVo>> shopPage(@Validated PageParam pageParam, @Validated MallGoodsSearchParam searchParam) {
        PageResult<MallGoodsListVo> list = goodsService.shopPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 店铺商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/goods_coupon/pages")
    public Result<PageResult<MallGoodsListVo>> couponGoodsPages(@Validated PageParam pageParam, @Validated MallGoodsSearchParam searchParam) {
        PageResult<MallGoodsListVo> list = goodsService.getCouponGoodsPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 店铺商品列表
     *
     * @return
     */
    @GetMapping("/goods/recommend/{site_id}")
    public Result<List<MallGoodsListVo>> getRecommendList(@Validated @PathVariable("site_id") Integer siteId, @RequestParam(name = "limit", defaultValue = "0") Integer limit) {
        return Result.success(goodsService.getRecommendList(siteId, limit));
    }

    /**
     * 猜你喜欢
     *
     * @return
     */
    @GetMapping("/goods/recommend")
    public Result<PageResult<MallGoodsListVo>> getRecommend(@Validated PageParam pageParam) {
        return Result.success(goodsService.getRecommend(pageParam));
    }

    /**
     * 获取商品详情
     *
     * @param param
     * @return
     */
    @GetMapping("/goods/detail")
    public Result<MallGoodsDetailVo> detail(@Validated MallGoodsDetailParam param) {
        return Result.success(goodsService.detail(param));
    }

    /**
     * 获取商品规格信息，切换规格
     *
     * @return
     */
    @GetMapping("/goods/sku/{sku_id}")
    public Result<MallGoodsSkuVo> sku(@PathVariable("sku_id") Integer skuId) {
        return Result.success(goodsService.getSku(skuId));
    }

    /**
     * 获取商品列表供组件调用
     *
     * @return
     */
    @GetMapping("/goods/components")
    public Result components(MallGoodsComponentsSearchParam param, @RequestParam Map<String, String> allParams) {
        return Result.success(goodsService.getGoodsComponents(param, allParams));
    }

    /**
     * 获取商品列表供组件调用
     *
     * @return
     */
    @GetMapping("/goods/shop_recommend/components")
    public Result shopRecommendComponents(@RequestParam(value = "goods_ids[]", required = false) Integer[] goodsIds, ShopComponentsSearchParam param) {
        param.setGoodsIds(goodsIds);
        return Result.success(goodsService.shopRecommendComponents(param));
    }


    /**
     * 获取优惠券适用商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/goods_coupon")
    public Result<PageResult<MallGoodsCouponVo>> page(@Validated PageParam pageParam, @Validated MallGoodsCouponSearchParam searchParam) {
        PageResult<MallGoodsCouponVo> list = mallCouponGoodsService.getCouponGoodsPageList(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取商品规格优惠信息列表
     *
     * @param skuId 商品规格ID
     * @return 优惠信息列表
     */
    @GetMapping("/goods/sku_promotion/list")
    public Result<List<String>> skuPromotionList(@RequestParam(value = "sku_id") Integer skuId) {
        List<String> list = goodsService.getSkuPromotionList(skuId);
        return Result.success(list);
    }

    /**
     * 获取商品规格优惠信息详情
     *
     * @param skuId 商品规格ID
     * @return 优惠信息详情
     */
    @GetMapping("/goods/sku_promotion/detail")
    public Result<SkuPromotionDetailVo> skuPromotionDetail(@RequestParam(value = "sku_id") Integer skuId) {
        SkuPromotionDetailVo detail = goodsService.getSkuPromotionDetail(skuId);
        return Result.success(detail);
    }
}
