package com.niu.mall.service.api.goods;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.event.shop.MallShopGoodsListEventDefiner;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.mall.service.api.goods.param.MallGoodsComponentsSearchParam;
import com.niu.mall.service.api.goods.param.MallGoodsDetailParam;
import com.niu.mall.service.api.goods.param.MallGoodsSearchParam;
import com.niu.mall.service.api.goods.param.ShopComponentsSearchParam;
import com.niu.mall.service.api.goods.vo.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IGoodsService {

    /**
     * 查询商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<MallGoodsListVo> page(PageParam pageParam, MallGoodsSearchParam searchParam, Map<String, String> position);

    /**
     * 店铺商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<MallGoodsListVo> shopPage(PageParam pageParam, MallGoodsSearchParam searchParam);

    /**
     * 优惠券商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<MallGoodsListVo> getCouponGoodsPage(PageParam pageParam, MallGoodsSearchParam searchParam);

    /**
     * 推荐商品列表
     *
     * @param siteId
     * @param num
     * @return
     */
    List<MallGoodsListVo> getRecommendList(Integer siteId, Integer num);

    /**
     * 常规获取商品列表
     *
     * @param searchParam
     * @return
     */
    List<MallGoodsListVo> getGoodsList(MallGoodsSearchParam searchParam);

    /**
     * 获取商品详情
     *
     * @param param
     * @return
     */
    MallGoodsDetailVo detail(MallGoodsDetailParam param);

    /**
     * 获取商品规格信息，切换规格
     *
     * @param skuId
     * @return
     */
    MallGoodsSkuVo getSku(Integer skuId);

    /**
     * 计算会员价
     *
     * @param memberLevel
     * @param calculateType
     * @param config
     * @param price
     * @return
     */
    BigDecimal calculateMemberPrice(MemberLevelInfoVo memberLevel, String calculateType, JSONObject config, BigDecimal price);

    /**
     * 获取商品列表供组件调用
     *
     * @param param
     */
    List<MallGoodsListVo> getGoodsComponents(MallGoodsComponentsSearchParam param, Map<String, String> allParams);

    /**
     * 根据商品标签获取标签名称
     *
     * @param labelIds
     * @return
     */
    List<GoodsLabelListVo> getGoodsLabelName(String labelIds);

    PageResult<MallGoodsListVo> getRecommend(PageParam pageParam);

    List<com.niu.core.service.api.shop.vo.MallGoodsListVo> selectGoodsAndSkuList(MallShopGoodsListEventDefiner.MallShopGoodsListEvent event);

    /**
     * 获取店铺推荐组件商品列表
     *
     * @param param
     * @return
     */
    List<MallGoodsListVo> shopRecommendComponents(ShopComponentsSearchParam param);

    List<String> getSkuPromotionList(Integer skuId);

    SkuPromotionDetailVo getSkuPromotionDetail(Integer skuId);
}
