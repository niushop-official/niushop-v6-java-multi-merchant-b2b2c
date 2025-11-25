package com.niu.core.event.local_delivery;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocalDeliveryEvent extends Event {

    private String event;

    private String tradeType;

    private String tradeNo;
}
