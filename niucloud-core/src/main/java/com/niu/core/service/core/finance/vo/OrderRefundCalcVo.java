package com.niu.core.service.core.finance.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * GoodsConfigVo视图
 */
@Data
public class OrderRefundCalcVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal mallRefundMoney;

    private BigDecimal shopRefundMoney;

    private BigDecimal mallNeedRefundMoney;

    private BigDecimal shopNeedRefundMoney;

    private BigDecimal mallRefundCouponMoney;

    private BigDecimal shopRefundSeparateMoney;
    private BigDecimal orderNeedRefundMoney;
    private BigDecimal refundCommission;
    private BigDecimal refundDeliveryMoney;
}
