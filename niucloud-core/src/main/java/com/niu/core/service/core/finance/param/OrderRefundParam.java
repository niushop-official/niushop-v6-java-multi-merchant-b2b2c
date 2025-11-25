package com.niu.core.service.core.finance.param;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单退款计算参数
 */
@Data
public class OrderRefundParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer orderId;

    private Integer supplyId;

    private String refundNo;

    private String orderRefundNo;

    private String settlementStatus;

    private Integer memberId;

    private BigDecimal money = BigDecimal.ZERO;

    private BigDecimal refundMoney = BigDecimal.ZERO;

    private BigDecimal shopRefundMoney = BigDecimal.ZERO;

    private BigDecimal shopNeedRefundMoney = BigDecimal.ZERO;

    private BigDecimal mallNeedRefundMoney = BigDecimal.ZERO;

    private BigDecimal orderNeedRefundMoney = BigDecimal.ZERO;

    private BigDecimal mallRefundMoney = BigDecimal.ZERO;

    private BigDecimal supplyRefundMoney = BigDecimal.ZERO;

    private BigDecimal refundCommission = BigDecimal.ZERO;

    private BigDecimal mallRate = BigDecimal.ZERO;

    private BigDecimal orderMoney = BigDecimal.ZERO;

    private BigDecimal refundDeliveryMoney = BigDecimal.ZERO;

    private BigDecimal mallCouponMoney = BigDecimal.ZERO;

    private BigDecimal mallRefundCouponMoney = BigDecimal.ZERO;

    private BigDecimal shopRefundSeparateMoney = BigDecimal.ZERO;



}
