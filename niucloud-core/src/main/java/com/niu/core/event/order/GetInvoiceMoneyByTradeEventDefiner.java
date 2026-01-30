package com.niu.core.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.service.api.order.vo.InvoiceListVo;
import lombok.Data;

import java.math.BigDecimal;

public abstract class GetInvoiceMoneyByTradeEventDefiner extends CallbackListener<GetInvoiceMoneyByTradeEventDefiner.GetInvoiceMoneyByTradeEvent> {

    /**
     * 事件主题定义
     */
    @Data
    public static class GetInvoiceMoneyByTradeEvent extends Event {
        private String tradeType;
        private Integer tradeId;
        private Integer memberId;

    }

    /**
     * 事件结果定义
     */
    @Data
    public static class GetInvoiceMoneyByTradeResult extends EventResult {
        private Integer siteId;
        private BigDecimal invoiceMoney;

    }

    // 事件响应
    public abstract GetInvoiceMoneyByTradeResult handleCallback(GetInvoiceMoneyByTradeEvent event);

}
