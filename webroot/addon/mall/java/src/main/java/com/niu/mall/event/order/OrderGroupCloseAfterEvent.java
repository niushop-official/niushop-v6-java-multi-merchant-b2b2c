package com.niu.mall.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGroup;
import lombok.Data;

@Data
public class OrderGroupCloseAfterEvent extends Event {

    private MallOrderGroup orderGroup;

}
