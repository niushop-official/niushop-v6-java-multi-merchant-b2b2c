package com.niu.core.service.core.cash.order.param;

import com.niu.core.entity.cash.CashOrder;
import lombok.Data;

import java.io.Serializable;

@Data
public class AfterCashOrderPayParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;

    private CashOrder orderData;

    private String mainType;

    private Integer mainId;
}
