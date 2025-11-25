package com.niu.mall.service.core.shop;

import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;

public interface ICoreAddonService {

    /**
     * 插件初始化
     *
     * @param site
     */
    void addonInit(Site site, SiteGroup siteGroup);
}
