package com.niu.mall.event.refund;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;

import java.math.BigDecimal;

@Data
public abstract class SyncSupplyOrderRefundEventDefiner extends CallbackListener<SyncSupplyOrderRefundEventDefiner.SyncSupplyOrderRefundEvent> {

    @Data
    public static class SyncSupplyOrderRefundEvent extends Event {
        private Integer refundId;
    }

    @Data
    public static class SyncSupplyOrderRefundResult extends EventResult {
        private BigDecimal money;
    }

    public abstract SyncSupplyOrderRefundResult handleCallback(SyncSupplyOrderRefundEvent event);
}
