package com.niu.core.event.separ;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分账记录 trade_scene 回填查询
 * 按 trade_type + trade_id 反查订单 order_from
 */
public abstract class SeparTradeSceneQueryEventDefiner extends CallbackListener<SeparTradeSceneQueryEventDefiner.SeparTradeSceneQueryEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SeparTradeSceneQueryEvent extends Event {
        private String tradeType;
        private Integer tradeId;
        // siteId 继承自父类 Event，不需要重复声明
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SeparTradeSceneQueryResult extends EventResult {
        private String orderFrom;
    }

    public abstract SeparTradeSceneQueryResult handleCallback(SeparTradeSceneQueryEvent event);
}
