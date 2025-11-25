package com.niu.mall.event.refund;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import lombok.Data;

@Data
public class SyncSupplyRefundNoEvent extends Event {

    private Integer refundId;
    private String refundNo;
    private String orderRefundNo;
    private Integer mallRefundId;
}