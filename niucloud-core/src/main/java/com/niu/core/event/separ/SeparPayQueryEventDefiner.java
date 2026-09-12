package com.niu.core.event.separ;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 汇付订单查询（资金是否解冻）
 * 对应 PHP: SeparPayQuery
 */
public abstract class SeparPayQueryEventDefiner extends CallbackListener<SeparPayQueryEventDefiner.SeparPayQueryEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SeparPayQueryEvent extends Event {
        private SeparAccountModel account;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SeparPayQueryResult extends EventResult {
        private String status;
    }

    public abstract SeparPayQueryResult handleCallback(SeparPayQueryEvent event);
}
