package com.niu.core.event.goods;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;

/**
 * 删除角标前校验是否被插件商品使用
 */
public abstract class CheckBadgeInUseEventDefiner extends CallbackListener<CheckBadgeInUseEventDefiner.CheckBadgeInUseEvent> {

    @Data
    public static class CheckBadgeInUseEvent extends Event {
        private Integer badgeId;
    }

    @Data
    public static class CheckBadgeInUseResult extends EventResult {
        private Long count;
        private String message;
    }

    public abstract CheckBadgeInUseResult handleCallback(CheckBadgeInUseEvent event);
}
