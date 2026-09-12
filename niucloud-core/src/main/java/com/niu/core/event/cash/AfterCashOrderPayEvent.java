package com.niu.core.event.cash;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.cash.CashOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AfterCashOrderPayEvent extends Event {

    private Integer orderId;

    private CashOrder orderData;

    private String mainType;

    private Integer mainId;
}
