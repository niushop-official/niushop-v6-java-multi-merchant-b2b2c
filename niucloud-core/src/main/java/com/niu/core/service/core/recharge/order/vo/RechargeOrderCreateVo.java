package com.niu.core.service.core.recharge.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RechargeOrderCreateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tradeType;

    private Integer tradeId;

}
