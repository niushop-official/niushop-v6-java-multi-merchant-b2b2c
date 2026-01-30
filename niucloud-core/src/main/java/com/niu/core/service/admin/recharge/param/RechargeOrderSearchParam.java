package com.niu.core.service.admin.recharge.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private Integer orderStatus;

    private String orderFrom;

    private String[] createTime;

    private String[] payTime;

    private BigDecimal startMoney = BigDecimal.ZERO;

    private BigDecimal endMoney = BigDecimal.ZERO;

    private Integer packageId = 0;

    private Integer memberId;
}
