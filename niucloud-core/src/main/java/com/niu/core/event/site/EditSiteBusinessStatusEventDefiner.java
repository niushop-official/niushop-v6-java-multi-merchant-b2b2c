package com.niu.core.event.site;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 编辑站点营业状态事件（对齐 PHP EditSiteBusinessStatus）
 */
public abstract class EditSiteBusinessStatusEventDefiner extends CallbackListener<EditSiteBusinessStatusEventDefiner.EditSiteBusinessStatusEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class EditSiteBusinessStatusEvent extends Event {
        private Integer businessStatus;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class EditSiteBusinessStatusResult extends EventResult {
        private Boolean success;
    }

    public abstract EditSiteBusinessStatusResult handleCallback(EditSiteBusinessStatusEvent event);
}
