package com.niu.mall.event.refund;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrderRefund;
import lombok.Data;

@Data
public class SyncSupplyRefundEditDeliveryEvent extends Event {

    private MallOrderRefund refundData;

    private String delivery;
}