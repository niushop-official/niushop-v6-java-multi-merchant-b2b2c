package com.niu.mall.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrder;
import lombok.Data;

/**
 * 订单结算事件
 */
@Data
public class MallOrderSettlementEvent extends Event {

    /** 订单实体 */
    private MallOrder order;

}

