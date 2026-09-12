package com.niu.core.event.delivery;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 检查运费模板是否在使用中
 * 对应 PHP: CheckShippingTemplateInUse
 */
public abstract class CheckShippingTemplateInUseEventDefiner
        extends CallbackListener<CheckShippingTemplateInUseEventDefiner.CheckShippingTemplateInUseEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CheckShippingTemplateInUseEvent extends Event {
        private Integer templateId;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CheckShippingTemplateInUseResult extends EventResult {
        /** true=使用中，不可删除 */
        private Boolean inUse;
    }

    public abstract CheckShippingTemplateInUseResult handleCallback(CheckShippingTemplateInUseEvent event);
}
