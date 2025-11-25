package com.niu.mall.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrderRefund;
import lombok.Data;

@Data
public class OrderRefundFinishAfterEvent extends Event {

    private MallOrderRefund refund;

}
