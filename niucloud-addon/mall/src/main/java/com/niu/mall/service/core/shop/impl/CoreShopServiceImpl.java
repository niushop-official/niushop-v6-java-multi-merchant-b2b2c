package com.niu.mall.service.core.shop.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.mall.service.core.shop.ICoreShopService;
import com.niu.mall.service.core.shop.vo.ShopInfoOfOrderCreateVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreShopServiceImpl implements ICoreShopService {

    @Resource
    private SiteMapper siteMapper;

    @Resource
    private SiteShopMapper siteShopMapper;

    @Override
    public ShopInfoOfOrderCreateVo getShopInfoOfOrderCreate(Integer siteId) {
        Site site = siteMapper.selectOne(new LambdaQueryWrapper<Site>()
                .eq(Site::getSiteId, siteId)
                .select(Site::getSiteId, Site::getSiteName));
        ShopInfoOfOrderCreateVo vo = new ShopInfoOfOrderCreateVo();
        if (site == null) {
            return vo;
        }
        vo.setSiteId(site.getSiteId());
        vo.setSiteName(site.getSiteName());
        SiteShop shop = siteShopMapper.selectOne(new LambdaQueryWrapper<SiteShop>()
                .eq(SiteShop::getSiteId, siteId)
                .select(SiteShop::getIsSelf, SiteShop::getServiceRate,
                        SiteShop::getBusinessStatus, SiteShop::getCircleId));
        if (shop != null) {
            ShopInfoOfOrderCreateVo.ShopInfo shopInfo = new ShopInfoOfOrderCreateVo.ShopInfo();
            BeanUtil.copyProperties(shop, shopInfo);
            vo.setShop(shopInfo);
        }
        return vo;
    }
}
