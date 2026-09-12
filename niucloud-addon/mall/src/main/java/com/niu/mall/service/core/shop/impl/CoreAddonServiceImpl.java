package com.niu.mall.service.core.shop.impl;


import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.mall.service.core.shop.ICoreAddonService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreAddonServiceImpl implements ICoreAddonService {

    @Resource
    private IDiyService diyService;

    /**
     * 插件初始化
     */
    @Override
    public void addonInit(Site site, SiteGroup siteGroup) {
        if (site == null || site.getSiteId() == null || site.getSiteId() <= 0) {
            return;
        }
        diyService.initDefaultPage(site.getSiteId(), "DIY_MALL_SITE_INDEX", "mall", "");
    }
}
