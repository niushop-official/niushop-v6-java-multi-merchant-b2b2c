package com.niu.mall.listener.site;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.event.site.EditSiteBusinessStatusEventDefiner;
import com.niu.core.mapper.shop.SiteShopMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 编辑站点营业状态（商城 site_shop）
 */
@EventCallback("mall")
@Component
public class EditSiteBusinessStatusListener extends EditSiteBusinessStatusEventDefiner {

    @Resource
    private SiteShopMapper siteShopMapper;

    @Resource
    private Cached cached;

    @Override
    public EditSiteBusinessStatusResult handleCallback(EditSiteBusinessStatusEvent event) {
        EditSiteBusinessStatusResult result = new EditSiteBusinessStatusResult();
        int updated = siteShopMapper.update(null, new LambdaUpdateWrapper<SiteShop>()
                .eq(SiteShop::getSiteId, event.getSiteId())
                .set(SiteShop::getBusinessStatus, event.getBusinessStatus()));
        if (updated > 0) {
            cached.tag("site_cache_" + event.getSiteId()).clear();
            result.setSuccess(true);
        }
        return result;
    }
}
