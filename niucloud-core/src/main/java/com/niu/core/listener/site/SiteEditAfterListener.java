package com.niu.core.listener.site;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.site.SiteEditAfterEvent;
import com.niu.core.service.core.delivery_store.ICoreDeliveryStoreService;
import org.springframework.stereotype.Component;

@EventListen("core")
@Component
public class SiteEditAfterListener extends AbstractListener {

    private ICoreDeliveryStoreService coreDeliveryStoreService() {
        return SpringContext.bean(ICoreDeliveryStoreService.class);
    }

    /**
     * 监听事件
     */
    public void handleEvent(SiteEditAfterEvent event) {
        coreDeliveryStoreService().editSysStore(event.getSiteId());
    }
}
