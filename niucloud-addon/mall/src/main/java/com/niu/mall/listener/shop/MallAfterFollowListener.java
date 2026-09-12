package com.niu.mall.listener.shop;

import com.niu.core.event.shop.AfterFollowEvent;
import com.niu.core.service.core.shop.ICoreShopStatService;
import com.niu.core.service.core.shop.param.ShopStatParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MallAfterFollowListener {

    @Autowired
    private ICoreShopStatService coreShopStatService;

    @EventListener
    public void handle(AfterFollowEvent event) {
        Integer siteId = event.getSiteId();
        String addon = event.getAddon();
        Integer followNumber = event.getFollowNumber();

        // 仅当 addon 为 'mall' 时更新关注数统计
        if ("mall".equals(addon)) {
            ShopStatParam param = new ShopStatParam();
            param.setSiteId(siteId);
            param.setFollowNumber(followNumber);
            coreShopStatService.setStat(param);
        }
    }
}
