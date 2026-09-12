package com.niu.core.event.fenxiao;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 分销订单创建后（回写业务订单佣金）
 * 对应 PHP: AfterFenxiaoOrderCreate
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AfterFenxiaoOrderCreateEvent extends Event {

    /** 业务类型 */
    private String tradeType;
    /** 订单ID */
    private Integer orderId;
    /** 店铺支付佣金 */
    private BigDecimal shopCommission;
}
