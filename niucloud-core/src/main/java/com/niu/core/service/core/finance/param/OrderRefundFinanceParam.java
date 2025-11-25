package com.niu.core.service.core.finance.param;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * GoodsConfigVo视图
 */
@Data
public class OrderRefundFinanceParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer orderId;

    private String refundNo;

    private String orderRefundNo;

    private Integer memberId;

    private BigDecimal money = BigDecimal.ZERO;

    private BigDecimal refundMoney = BigDecimal.ZERO;

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
