package com.niu.mall.service.site.goods;

import cn.hutool.json.JSONObject;
import com.niu.mall.service.core.goods.vo.ShopGoodsCategoryConfigVo;

/**
 * 商品分类服务接口
 */
public interface IGoodsCategoryService {

    /**
     * 获取商品分类配置
     *
     * @return GoodsCategoryConfigVo
     */
    ShopGoodsCategoryConfigVo getGoodsCategoryConfig();

    /**
     * 设置商品分类配置
     *
     * @param data
     */
    void setGoodsCategoryConfig(JSONObject data);
}
