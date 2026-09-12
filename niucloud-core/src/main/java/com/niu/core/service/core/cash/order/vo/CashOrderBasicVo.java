package com.niu.core.service.core.cash.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CashOrderBasicVo implements Serializable {

    private BigDecimal discountMoney = BigDecimal.ZERO;

    private BigDecimal platDiscountMoney = BigDecimal.ZERO;

    private BigDecimal totalMoney = BigDecimal.ZERO;

    private BigDecimal noDiscountMoney = BigDecimal.ZERO;

    private BigDecimal discountableMoney = BigDecimal.ZERO;

    private BigDecimal orderMoney = BigDecimal.ZERO;

    private BigDecimal siteCouponMoney = BigDecimal.ZERO;

    private BigDecimal platformCouponMoney = BigDecimal.ZERO;

    private Integer usePoint = 0;

    private BigDecimal pointDeductMoney = BigDecimal.ZERO;

    private Integer point = 0;

    private BigDecimal pointMoney = BigDecimal.ZERO;

    private Integer platformCouponId = 0;

    private Integer siteCouponId = 0;
}
