package com.niu.core.event.fenxiao;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分销订单结算
 * 对应 PHP: FenxiaoOrderSettlement
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FenxiaoOrderSettlementEvent extends Event {

    /** 业务类型 */
    private String tradeType;
    /** 订单数据 */
    private FenxiaoOrderData orderData;
}
