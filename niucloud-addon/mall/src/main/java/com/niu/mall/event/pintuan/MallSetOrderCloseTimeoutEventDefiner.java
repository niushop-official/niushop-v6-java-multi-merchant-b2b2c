package com.niu.mall.event.pintuan;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.event.order.ShopOrderDiscountCreateEventDefiner;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public abstract class MallSetOrderCloseTimeoutEventDefiner extends CallbackListener<MallSetOrderCloseTimeoutEventDefiner.MallSetOrderCloseTimeoutEvent> {
        /**
        * 事件主题定义
        */
        @Data
        public static class MallSetOrderCloseTimeoutEvent extends Event {

                private long time;
                private MallOrder orderData;
        }

        /**
        * 事件结果定义
        */
        @Data
        @EqualsAndHashCode(callSuper=false)
        public static class MallSetOrderCloseTimeoutResult extends EventResult {
                private Boolean er;
        }

        // 事件响应
        public abstract MallSetOrderCloseTimeoutResult handleCallback(MallSetOrderCloseTimeoutEvent event);
}
