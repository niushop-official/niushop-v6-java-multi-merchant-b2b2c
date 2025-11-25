package com.niu.mall.event.refund;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrder;
import lombok.Data;

@Data
public class SyncSupplyRefundCloseUpdateEvent extends Event {

    private Integer orderGoodsId;
    private String orderRefundNo;
}
