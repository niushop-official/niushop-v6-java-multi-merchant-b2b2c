package com.niu.mall.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrder;
import lombok.Data;

@Data
public class OrderDeliveryAfterEvent  extends Event {
    private MallOrder order;
}
