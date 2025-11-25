package com.niu.core.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.order.param.MallOrderSearchParam;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public abstract class OrderPageEventDefiner extends CallbackListener<OrderPageEventDefiner.OrderPageEvent> {

    /**
     * 事件主题定义
     */
    @Data
    public static class OrderPageEvent extends Event {
        private PageParam pageParam = new PageParam();
        private MallOrderSearchParam searchParam;

    }


    /**
     * 事件结果定义
     */
    @Data
    public static class OrderEventResult extends EventResult {
        private Long todayOrderCount = 0L;
        private Long totalOrderCount = 0L;
        private Long yesterdayOrderCount = 0L;
        private String todaySaleMoney = "0.00";
        private String yesterdaySaleMoney = "0.00";
        private String totalSaleMoney = "0.00";
        private String todayRefundMoney = "0.00";
        private String yesterdayRefundMoney = "0.00";
        private String totalRefundMoney = "0.00";
        private Long waitPayOrder = 0L;
        private Long waitDeliveryOrder = 0L;
        private Long waitTakeOrder = 0L;
        private Long refundOrder = 0L;
        private Long saleGoodsNum = 0L;
        private Long auditGoodsNum = 0L;
    }

    // 事件响应
    public abstract OrderEventResult handleCallback(OrderPageEvent event);

}
