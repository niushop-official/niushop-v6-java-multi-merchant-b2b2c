package com.niu.core.service.core.cash.order.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CoreCashOrderCalculateParam implements Serializable {

    private String orderKey;

    private Integer siteId;

    private Integer memberId;

    private String orderFrom;

    private BigDecimal totalMoney;

    private BigDecimal noDiscountMoney;
}
