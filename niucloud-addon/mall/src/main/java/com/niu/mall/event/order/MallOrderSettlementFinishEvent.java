package com.niu.mall.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrder;
import lombok.Data;

/**
 * 订单结算完成事件
 */
@Data
public class MallOrderSettlementFinishEvent extends Event {

    /** 订单数据 */
    private MallOrder order;

}

