package com.niu.mall.event.refund;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import lombok.Data;

@Data
public class SyncSupplyRefundClassEvent extends Event {

    private MallOrderRefund refundData;

    private Integer mainId;

    private String mainType;
}