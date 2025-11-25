package com.niu.mall.service.core.goods;

import cn.hutool.json.JSONObject;
import com.niu.mall.service.core.goods.vo.GoodsCategoryConfigVo;
import com.niu.mall.service.core.goods.vo.GoodsConfigVo;
import com.niu.mall.service.core.goods.vo.ShopGoodsCategoryConfigVo;

/**
 * 商品分类服务接口
 */
public interface ICoreGoodsConfigService {

    /**
     * 获取商品分类配置
     *
     * @return GoodsCategoryConfigVo
     */
    GoodsCategoryConfigVo getGoodsCategoryConfig();

    /**
     * 设置商品分类配置
     *
     * @param data
     */
    void setGoodsCategoryConfig(JSONObject data);

    /**
     * 获取商品分类配置
     *
     * @return ShopGoodsCategoryConfigVo
     */
    ShopGoodsCategoryConfigVo getShopGoodsCategoryConfig(Integer siteId);

    /**
     * 设置商品分类配置
     *
     * @param siteId
     * @param data
     */
    void setShopGoodsCategoryConfig(Integer siteId, JSONObject data);

    /**
     * 获取商品配置
     *
     * @return GoodsConfigVo
     */
    GoodsConfigVo getConfig();

    /**
     * 设置商品配置
     *
     * @param data
     */
    void setConfig(JSONObject data);




}
