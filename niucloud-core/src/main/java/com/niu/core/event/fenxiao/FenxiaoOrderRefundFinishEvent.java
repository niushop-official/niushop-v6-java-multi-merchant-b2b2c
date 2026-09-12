package com.niu.core.event.fenxiao;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分销订单退款完成
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FenxiaoOrderRefundFinishEvent extends Event {

    /** 业务类型 */
    private String tradeType;
    /** 订单数据 */
    private FenxiaoOrderData orderData;
    /** 订单项ID */
    private Integer orderGoodsId;
    /** 订单ID */
    private Integer orderId;
}
