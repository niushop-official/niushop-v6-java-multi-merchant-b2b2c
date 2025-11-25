package com.niu.core.event.refund;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.pay.PayRefund;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RefundFailEvent extends Event {

    private String tradeType;

    private String tradeId;

    private PayRefund refund;
}
