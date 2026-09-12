package com.niu.core.service.core.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.core.shop.ICoreShopStatService;
import com.niu.core.service.core.shop.param.ShopStatParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreShopStatServiceImpl implements ICoreShopStatService {

    @Resource
    SiteShopMapper siteShopMapper;

    @Override
    public void addStat(ShopStatParam shopStatParam) {
        if (shopStatParam == null || shopStatParam.getSiteId() == null) {
            return;
        }
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", shopStatParam.getSiteId()));
        if (ObjectUtil.isEmpty(shop)) {
            return;
        }
        if (ObjectUtil.isNotEmpty(shopStatParam.getMallSaleNum())) {
            shop.setMallSaleNum(nullSafe(shop.getMallSaleNum()) + shopStatParam.getMallSaleNum());
        }
        if (ObjectUtil.isNotEmpty(shopStatParam.getMallUpNum())) {
            shop.setMallUpNum(nullSafe(shop.getMallUpNum()) + shopStatParam.getMallUpNum());
        }
        if (ObjectUtil.isNotEmpty(shopStatParam.getFollowNumber())) {
            shop.setFollowNumber(nullSafe(shop.getFollowNumber()) + shopStatParam.getFollowNumber());
        }
        siteShopMapper.updateById(shop);
    }

    @Override
    public void setStat(ShopStatParam shopStatParam) {
        if (shopStatParam == null || shopStatParam.getSiteId() == null) {
            return;
        }
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", shopStatParam.getSiteId()));
        if (ObjectUtil.isEmpty(shop)) {
            return;
        }
        if (ObjectUtil.isNotEmpty(shopStatParam.getMallSaleNum())) {
            shop.setMallSaleNum(shopStatParam.getMallSaleNum());
        }
        if (ObjectUtil.isNotEmpty(shopStatParam.getMallUpNum())) {
            shop.setMallUpNum(shopStatParam.getMallUpNum());
        }
        if (ObjectUtil.isNotEmpty(shopStatParam.getFollowNumber())) {
            shop.setFollowNumber(shopStatParam.getFollowNumber());
        }
        siteShopMapper.updateById(shop);
    }

    private int nullSafe(Integer value) {
        return value != null ? value : 0;
    }
}
