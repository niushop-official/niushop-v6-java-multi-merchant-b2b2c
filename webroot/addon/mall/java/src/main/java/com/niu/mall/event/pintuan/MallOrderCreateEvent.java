package com.niu.mall.event.pintuan;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;

@Data
public class MallOrderCreateEvent extends Event {

    private OrderCreateDataVo vo;
    private Integer orderId;

}
