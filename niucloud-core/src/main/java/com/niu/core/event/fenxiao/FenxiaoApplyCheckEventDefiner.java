package com.niu.core.event.fenxiao;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 分销申请条件检测
 * 对应 PHP: FenxiaoApplyCheck
 */
public abstract class FenxiaoApplyCheckEventDefiner extends CallbackListener<FenxiaoApplyCheckEventDefiner.FenxiaoApplyCheckEvent> {

    @Override
    public abstract FenxiaoApplyCheckEventResult handleCallback(FenxiaoApplyCheckEvent event);

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FenxiaoApplyCheckEvent extends Event {
        /** 会员ID */
        private Integer memberId;
        /** 分销申请条件配置 */
        private String fenxiaoCondition;
        /** 消费条件配置 */
        private String consumeCondition;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FenxiaoApplyCheckEventResult extends EventResult {
        /** 订单数量 */
        private Long orderCount;
        /** 订单金额合计 */
        private BigDecimal orderSum;
    }
}
