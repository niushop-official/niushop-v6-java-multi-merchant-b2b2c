package com.niu.mall.service.core.goods.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.mall.service.core.goods.ICoreGoodsConfigService;
import com.niu.mall.service.core.goods.vo.GoodsCategoryConfigVo;
import com.niu.mall.service.core.goods.vo.GoodsConfigVo;
import com.niu.mall.service.core.goods.vo.ShopGoodsCategoryConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 商品分类实现
 */
@Service
public class CoreGoodsConfigServiceImpl implements ICoreGoodsConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 获取商品分类配置
     *
     * @return GoodsCategoryConfigVo
     */
    public GoodsCategoryConfigVo getGoodsCategoryConfig() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "GOODS_CATEGORY_CONFIG");
        if (config.isEmpty()) return new GoodsCategoryConfigVo();
        return JSONUtil.toBean(config, GoodsCategoryConfigVo.class);
    }

    /**
     * 设置商品分类配置
     *
     * @param data
     */
    public void setGoodsCategoryConfig(JSONObject data) {
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "GOODS_CATEGORY_CONFIG", data);
    }

    @Override
    public ShopGoodsCategoryConfigVo getShopGoodsCategoryConfig(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "MALL_SHOP_GOODS_CATEGORY_CONFIG");
        if (config.isEmpty()) return new ShopGoodsCategoryConfigVo();
        return JSONUtil.toBean(config, ShopGoodsCategoryConfigVo.class);
    }

    @Override
    public void setShopGoodsCategoryConfig(Integer siteId, JSONObject data) {
        coreConfigService.setConfig(siteId, "MALL_SHOP_GOODS_CATEGORY_CONFIG", data);
    }

    @Override
    public GoodsConfigVo getConfig() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "MALL_GOODS_CONFIG");
        if (config.isEmpty()) return new GoodsConfigVo();
        return JSONUtil.toBean(config, GoodsConfigVo.class);
    }

    @Override
    public void setConfig(JSONObject data) {
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "MALL_GOODS_CONFIG", data);
    }
}
