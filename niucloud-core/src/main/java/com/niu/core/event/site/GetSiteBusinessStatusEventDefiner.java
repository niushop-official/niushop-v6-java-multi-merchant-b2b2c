package com.niu.core.event.site;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取站点营业状态事件
 * 对应 PHP: GetSiteBusinessStatus
 */
public abstract class GetSiteBusinessStatusEventDefiner extends CallbackListener<GetSiteBusinessStatusEventDefiner.GetSiteBusinessStatusEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class GetSiteBusinessStatusEvent extends Event {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class GetSiteBusinessStatusResult extends EventResult {
        private Integer businessStatus;
    }

    public abstract GetSiteBusinessStatusResult handleCallback(GetSiteBusinessStatusEvent event);
}
