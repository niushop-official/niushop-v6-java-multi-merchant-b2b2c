package com.niu.core.service.core.cash.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CashOrderCreateResultVo implements Serializable {

    private String tradeType;

    private Integer orderId;
}
