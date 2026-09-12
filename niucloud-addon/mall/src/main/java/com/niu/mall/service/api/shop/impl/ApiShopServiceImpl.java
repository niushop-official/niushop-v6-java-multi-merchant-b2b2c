package com.niu.mall.service.api.shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.exception.ApiException;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.mall.service.api.shop.IApiShopService;
import com.niu.mall.service.api.shop.vo.ShopBusinessStatusVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiShopServiceImpl implements IApiShopService {

    @Resource
    private SiteShopMapper siteShopMapper;

    @Override
    public ShopBusinessStatusVo getBusinessStatus(Integer siteId) {
        SiteShop shop = siteShopMapper.selectOne(new LambdaQueryWrapper<SiteShop>()
                .eq(SiteShop::getSiteId, siteId)
                .select(SiteShop::getBusinessStatus));
        if (shop == null) {
            throw new ApiException("店铺不存在");
        }
        ShopBusinessStatusVo vo = new ShopBusinessStatusVo();
        vo.setBusinessStatus(shop.getBusinessStatus());
        return vo;
    }
}
