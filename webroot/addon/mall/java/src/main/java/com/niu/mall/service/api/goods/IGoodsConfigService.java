package com.niu.mall.service.api.goods;

import cn.hutool.json.JSONObject;
import com.niu.mall.service.core.goods.vo.GoodsCategoryConfigVo;
import com.niu.mall.service.core.goods.vo.GoodsConfigVo;
import com.niu.mall.service.core.goods.vo.ShopGoodsCategoryConfigVo;

/**
 * 商品设置接口
 */
public interface IGoodsConfigService {


    /**
     * 获取商品配置
     *
     * @return GoodsConfigVo
     */
    GoodsConfigVo getConfig();

    /**
     * 获取商品分类配置
     *
     * @return GoodsCategoryConfigVo
     */
    ShopGoodsCategoryConfigVo getShopGoodsCategoryConfig();

    /**
     * 获取商品分类配置
     *
     * @return
     */
    GoodsCategoryConfigVo getGoodsCategoryConfig();
}
