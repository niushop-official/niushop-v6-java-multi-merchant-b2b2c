package com.niu.core.event.pay;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public abstract class GetPayInfoByTradeDefiner extends CallbackListener<GetPayInfoByTradeDefiner.GetPayInfoByTradeEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetPayInfoByTradeEvent extends Event {
        private String tradeType;
        private Integer tradeId;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class GetPayInfoByTradeResult extends EventResult {
        private String outTradeNo;
    }

}

