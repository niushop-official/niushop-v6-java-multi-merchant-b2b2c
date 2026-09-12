package com.niu.core.event.account;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 获取分账业务类型事件
 * 对应 PHP: SpearTradeType
 */
public abstract class SpearTradeTypeEventDefiner extends CallbackListener<SpearTradeTypeEventDefiner.SpearTradeTypeEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SpearTradeTypeEvent extends Event {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SpearTradeTypeResult extends EventResult {
        private Map<String, String> types;
    }

    public abstract SpearTradeTypeResult handleCallback(SpearTradeTypeEvent event);
}
