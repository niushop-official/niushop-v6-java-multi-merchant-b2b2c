package com.niu.core.event.order;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;

@Data
public class AfterInvoiceApplyEvent extends Event {

    private String tradeType;

    private Integer tradeId;

    private Integer invoiceId;
}
