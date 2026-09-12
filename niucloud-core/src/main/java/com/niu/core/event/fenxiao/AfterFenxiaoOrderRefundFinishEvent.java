package com.niu.core.event.fenxiao;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 分销订单退款完成后（回写业务订单退还佣金）
 * 对应 PHP: AfterFenxiaoOrderRefundFinish
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AfterFenxiaoOrderRefundFinishEvent extends Event {

    /** 业务类型 */
    private String tradeType;
    /** 订单ID */
    private Integer orderId;
    /** 订单项ID */
    private Integer orderGoodsId;
    /** 退还佣金总额 */
    private BigDecimal totalCommission;
}
