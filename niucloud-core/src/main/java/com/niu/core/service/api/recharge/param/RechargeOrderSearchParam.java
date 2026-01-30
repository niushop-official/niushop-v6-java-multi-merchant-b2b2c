package com.niu.core.service.api.recharge.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderStatus;

    private String[] createTime;

}
