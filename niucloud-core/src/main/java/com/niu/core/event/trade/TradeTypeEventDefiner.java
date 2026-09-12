package com.niu.core.event.trade;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 业务类型事件，对齐 PHP event('TradeType')
 */
public abstract class TradeTypeEventDefiner extends CallbackListener<TradeTypeEventDefiner.TradeTypeEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class TradeTypeEvent extends Event {
        /** 支持场景：goods_brand / goods_category / coupon 等 */
        private String supportType;
        /** 按业务类型查配置时传入，如 mall / waimai */
        private String tradeType;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class TradeTypeResult extends EventResult {
        /** support_type / 空查询时返回：key=业务类型，value=名称 */
        private Map<String, String> types;
        /** trade_type 查询时返回配置 */
        private Map<String, Object> config;
    }

    public abstract TradeTypeResult handleCallback(TradeTypeEvent event);
}
