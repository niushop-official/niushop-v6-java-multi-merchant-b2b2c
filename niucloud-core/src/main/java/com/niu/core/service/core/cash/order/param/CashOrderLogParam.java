package com.niu.core.service.core.cash.order.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class CashOrderLogParam implements Serializable {

    private Integer orderId;

    private String mainType;

    private Integer mainId;

    private Integer status;

    private String type;

    private String content;
}
