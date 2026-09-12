package com.niu.core.event.sys;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 获取营销列表事件
 * 对应 PHP: ShowPromotion
 */
public abstract class ShowPromotionEventDefiner extends CallbackListener<ShowPromotionEventDefiner.ShowPromotionEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ShowPromotionEvent extends Event {
        private String appType;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ShowPromotionResult extends EventResult {
        private List<Map<String, String>> list;
    }

    public abstract ShowPromotionResult handleCallback(ShowPromotionEvent event);
}
