package com.niu.core.service.core.recharge.order.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeOrderLogParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;

    /** 操作人类型 */
    private String mainType;

    /** 操作人id */
    private Integer mainId;

    /** 订单状态 */
    private Integer status;

    /** 类型 */
    private String type;

    /** 日志内容 */
    private String content;
}
