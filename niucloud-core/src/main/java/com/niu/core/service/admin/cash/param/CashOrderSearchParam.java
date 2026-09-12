package com.niu.core.service.admin.cash.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CashOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String searchType;

    private String searchName;

    private String keyword;

    private String status;

    private String payType;

    private String orderFrom;

    private String[] createTime;

    private String[] payTime;

    private BigDecimal startMoney = BigDecimal.ZERO;

    private BigDecimal endMoney = BigDecimal.ZERO;
}
