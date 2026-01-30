package com.niu.core.event.verify;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

public abstract class GetVerifyInfoByTradeEventDefiner extends CallbackListener<GetVerifyInfoByTradeEventDefiner.GetVerifyInfoByTradeEvent> {

    @Data
    @Accessors(chain = true)
    public static class GetVerifyInfoByTradeEvent extends Event {

        private Integer tradeId;
        private String tradeType;
    }

    @Data
    @Accessors(chain = true)
    public static class GetVerifyInfoByTradeResult extends EventResult {
        private Integer memberId;
        private String type;
        private Map<String, Object>  data;
    }

    public abstract GetVerifyInfoByTradeResult handleCallback(GetVerifyInfoByTradeEvent event);

}

