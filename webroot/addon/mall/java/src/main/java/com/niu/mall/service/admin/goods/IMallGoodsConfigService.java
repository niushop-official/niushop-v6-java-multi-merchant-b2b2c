package com.niu.mall.service.admin.goods;

import cn.hutool.json.JSONObject;
import com.niu.mall.service.core.goods.vo.GoodsConfigVo;

public interface IMallGoodsConfigService {

    /**
     * 获取商品分类配置
     *
     * @return GoodsCategoryConfigVo
     */
    GoodsConfigVo getGoodsConfig();

    /**
     * 设置商品分类配置-513
     *
     * @param data
     */
    void setGoodsConfig(JSONObject data);
}
