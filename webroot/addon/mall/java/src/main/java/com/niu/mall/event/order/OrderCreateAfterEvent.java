package com.niu.mall.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class OrderCreateAfterEvent extends Event {

    private OrderCreateDataVo orderData;

    private MallOrder order;
}
