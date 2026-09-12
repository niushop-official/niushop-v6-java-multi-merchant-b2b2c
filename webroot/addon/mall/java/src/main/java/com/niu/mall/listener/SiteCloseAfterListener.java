package com.niu.mall.listener;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.site.SiteCloseAfterEvent;
import com.niu.mall.service.admin.goods.IMallGoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 站点关闭后下架商城商品
 * 对应 PHP: addon\mall\app\listener\SiteCloseAfterListener
 */
@EventListen("mall")
@Component
public class SiteCloseAfterListener extends AbstractListener {

    @Resource
    private IMallGoodsService mallGoodsService;

    public void handleEvent(SiteCloseAfterEvent event) {
        if (event.getSiteId() == null || event.getSiteId() <= 0) {
            return;
        }
        mallGoodsService.offlineBySiteId(event.getSiteId());
    }
}
