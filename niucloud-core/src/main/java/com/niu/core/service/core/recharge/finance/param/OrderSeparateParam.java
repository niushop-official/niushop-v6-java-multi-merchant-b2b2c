package com.niu.core.service.core.recharge.finance.param;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单退款计算参数
 */
@Data
public class OrderSeparateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String outTradeNo;

    private String orderNo;

    private Integer memberId;

    private Integer orderId;

    private Integer siteId;

    private BigDecimal orderMoney = BigDecimal.ZERO;

}
