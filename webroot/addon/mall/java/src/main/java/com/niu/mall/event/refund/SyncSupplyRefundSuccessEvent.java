package com.niu.mall.event.refund;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.pay.PayRefund;
import lombok.Data;

@Data
public class SyncSupplyRefundSuccessEvent extends Event {

    private String tradeType;

    private String tradeId;

    private PayRefund refund;
}
