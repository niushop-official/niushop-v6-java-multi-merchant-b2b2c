package com.niu.mall.service.site.goods;

import cn.hutool.json.JSONObject;
import com.niu.mall.service.core.goods.vo.GoodsConfigVo;

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
     * 设置商品配置
     *
     * @param data
     */
    void setGoodsConfig(JSONObject data);
}
