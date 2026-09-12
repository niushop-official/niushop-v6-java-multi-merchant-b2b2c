package com.niu.core.service.api.cash.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiCashOrderCreateVo implements Serializable {

    private String tradeType;

    private Integer orderId;
}
