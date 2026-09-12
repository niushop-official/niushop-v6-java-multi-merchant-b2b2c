package com.niu.core.event.cash;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.cash.CashOrder;
import com.niu.core.service.core.cash.order.vo.CashOrderBasicVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AfterCashOrderCreateEvent extends Event {

    private Integer orderId;

    private CashOrder orderData;

    private CashOrderBasicVo basic;

    private String mainType;

    private Integer mainId;

    private Long time;
}
