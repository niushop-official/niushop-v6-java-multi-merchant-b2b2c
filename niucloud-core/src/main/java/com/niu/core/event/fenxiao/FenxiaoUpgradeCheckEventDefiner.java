package com.niu.core.event.fenxiao;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 分销升级检测
 * 对应 PHP: FenxiaoUpgradeCheck
 */
public abstract class FenxiaoUpgradeCheckEventDefiner extends CallbackListener<FenxiaoUpgradeCheckEventDefiner.FenxiaoUpgradeCheckEvent> {

    @Override
    public abstract FenxiaoUpgradeCheckEventResult handleCallback(FenxiaoUpgradeCheckEvent event);

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FenxiaoUpgradeCheckEvent extends Event {
        /** 分销商会员ID */
        private Integer fenxiaoMemberId;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FenxiaoUpgradeCheckEventResult extends EventResult {
        /** 订单数量 */
        private Long orderNum;
        /** 订单金额合计 */
        private BigDecimal orderMoney;
    }
}
