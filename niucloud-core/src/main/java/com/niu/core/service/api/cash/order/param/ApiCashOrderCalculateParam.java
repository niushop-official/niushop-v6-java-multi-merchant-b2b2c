package com.niu.core.service.api.cash.order.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ApiCashOrderCalculateParam implements Serializable {

    private String orderKey;

    private Integer siteId;

    private BigDecimal totalMoney;

    private BigDecimal noDiscountMoney;
}
