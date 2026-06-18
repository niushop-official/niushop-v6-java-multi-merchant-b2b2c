package com.niu.mall.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.service.core.order.param.OrderPayParam;
import lombok.Data;

@Data
public class MallOrderPayCustomEvent extends Event {

    public MallOrderPayCustomEvent() {
    }

    public MallOrderPayCustomEvent(MallOrder order, OrderPayParam param) {
        this.param = param;
        this.order = order;
    }

    private MallOrder order;

    private OrderPayParam param;
}
