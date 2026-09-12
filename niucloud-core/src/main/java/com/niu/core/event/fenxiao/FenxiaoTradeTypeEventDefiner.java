package com.niu.core.event.fenxiao;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.event.fenxiao.vo.FenxiaoTradeTypeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 获取分销业务类型
 * 对应 PHP: FenxiaoTradeType
 */
public abstract class FenxiaoTradeTypeEventDefiner extends CallbackListener<FenxiaoTradeTypeEventDefiner.FenxiaoTradeTypeEvent> {

    @Override
    public abstract FenxiaoTradeTypeResult handleCallback(FenxiaoTradeTypeEvent event);

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FenxiaoTradeTypeEvent extends Event {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FenxiaoTradeTypeResult extends EventResult {

        /**
         * key=trade_type, value=业务类型配置
         */
        private Map<String, FenxiaoTradeTypeVo> tradeTypes;
    }
}
