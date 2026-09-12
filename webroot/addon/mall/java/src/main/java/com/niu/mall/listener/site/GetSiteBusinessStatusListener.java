package com.niu.mall.listener.site;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.event.site.EditSiteBusinessStatusEventDefiner;
import com.niu.core.event.site.GetSiteBusinessStatusEventDefiner;
import com.niu.core.mapper.shop.SiteShopMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 获取站点营业状态（商城 site_shop）
 */
@EventCallback("mall")
@Component
public class GetSiteBusinessStatusListener extends GetSiteBusinessStatusEventDefiner {

    @Resource
    private SiteShopMapper siteShopMapper;

    @Override
    public GetSiteBusinessStatusResult handleCallback(GetSiteBusinessStatusEvent event) {
        SiteShop shop = siteShopMapper.selectOne(
                new LambdaQueryWrapper<SiteShop>().eq(SiteShop::getSiteId, event.getSiteId())
                        .select(SiteShop::getSiteId,SiteShop::getBusinessStatus).last("limit 1"));
        GetSiteBusinessStatusResult result = new GetSiteBusinessStatusResult();
        if (shop != null && shop.getBusinessStatus() != null) {
            result.setBusinessStatus(shop.getBusinessStatus());
        }
        return result;
    }
}
