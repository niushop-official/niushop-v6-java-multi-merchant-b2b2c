package com.niu.core.listener.shop;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.site.SiteDelAfterEvent;
import com.niu.core.service.core.operation_log.IOperationLogService;
import org.springframework.stereotype.Component;

/**
 * 站点删除后监听器
 * 对应 PHP: app\listener\shop\SiteDelAfterListener
 */
@EventListen("core")
@Component
public class SiteDelAfterListener extends AbstractListener {

    public void handleEvent(SiteDelAfterEvent event) {
        if (ObjectUtil.isEmpty(event.getType()) || ObjectUtil.isEmpty(event.getMainType())
                || ObjectUtil.isNull(event.getUid()) || ObjectUtil.isEmpty(event.getData())) {
            return;
        }
        IOperationLogService operationLogService = SpringContext.bean(IOperationLogService.class);
        operationLogService.addOperationLog(event.getType(), event.getMainType(), event.getUid(), event.getData());
    }
}
