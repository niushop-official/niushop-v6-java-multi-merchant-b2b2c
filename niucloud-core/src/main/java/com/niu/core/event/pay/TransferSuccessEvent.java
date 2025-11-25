package com.niu.core.event.pay;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.pay.PayTransfer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TransferSuccessEvent extends Event {
    private PayTransfer transfer;
}
