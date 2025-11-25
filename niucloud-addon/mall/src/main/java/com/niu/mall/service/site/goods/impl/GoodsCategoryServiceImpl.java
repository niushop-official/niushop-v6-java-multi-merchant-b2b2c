package com.niu.mall.service.site.goods.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.service.core.goods.ICoreGoodsConfigService;
import com.niu.mall.service.core.goods.vo.ShopGoodsCategoryConfigVo;
import com.niu.mall.service.site.goods.IGoodsCategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


/**
 * 商品分类实现
 */
@Service
public class GoodsCategoryServiceImpl implements IGoodsCategoryService {


    @Resource
    ICoreGoodsConfigService coreGoodsConfigService;


    /**
     * 获取商品分类配置
     *
     * @return GoodsCategoryConfigVo
     */
    public ShopGoodsCategoryConfigVo getGoodsCategoryConfig() {
        return coreGoodsConfigService.getShopGoodsCategoryConfig(RequestUtils.siteId());
    }

    /**
     * 设置商品分类配置
     *
     * @param data
     */
    public void setGoodsCategoryConfig(JSONObject data) {
        coreGoodsConfigService.setShopGoodsCategoryConfig(RequestUtils.siteId(), data);
    }
}
