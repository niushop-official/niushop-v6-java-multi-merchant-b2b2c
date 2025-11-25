package com.niu.mall.service.api.goods.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.service.api.goods.IGoodsConfigService;
import com.niu.mall.service.core.goods.ICoreGoodsConfigService;
import com.niu.mall.service.core.goods.vo.GoodsCategoryConfigVo;
import com.niu.mall.service.core.goods.vo.GoodsConfigVo;
import com.niu.mall.service.core.goods.vo.ShopGoodsCategoryConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class GoodsConfigServiceImpl implements IGoodsConfigService {

    @Resource
    ICoreGoodsConfigService coreGoodsConfigService;

    @Override
    public GoodsConfigVo getConfig() {
        return coreGoodsConfigService.getConfig();
    }

    @Override
    public ShopGoodsCategoryConfigVo getShopGoodsCategoryConfig() {
        return coreGoodsConfigService.getShopGoodsCategoryConfig(RequestUtils.siteId());
    }

    @Override
    public GoodsCategoryConfigVo getGoodsCategoryConfig() {
        return coreGoodsConfigService.getGoodsCategoryConfig();
    }
}
