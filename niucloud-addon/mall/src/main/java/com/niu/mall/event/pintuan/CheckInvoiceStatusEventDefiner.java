package com.niu.mall.event.pintuan;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;

public abstract class CheckInvoiceStatusEventDefiner extends CallbackListener<CheckInvoiceStatusEventDefiner.CheckInvoiceStatusEvent> {

    /**
     * 事件主题定义
     */
    @Data
    public static class CheckInvoiceStatusEvent extends Event {

        private String orderType;
        private String status;

    }

    /**
     * 事件结果定义
     */
    @Data
    public static class CheckInvoiceStatusResult extends EventResult {
        private Boolean data;
    }

    // 事件响应
    public abstract CheckInvoiceStatusResult handleCallback(CheckInvoiceStatusEvent event);
}
