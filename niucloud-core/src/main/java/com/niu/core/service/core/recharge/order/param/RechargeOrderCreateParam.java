package com.niu.core.service.core.recharge.order.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeOrderCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String memberMessage = "";

    private BigDecimal rechargeMoney = BigDecimal.ZERO;

    private Integer packageId =0;

    private Integer siteId;

    private Integer memberId;

    private String orderFrom;

    private Integer mainId;

    private String mainType;
}
