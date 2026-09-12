package com.niu.core.enums.cash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CashOrderActionEnum {

    ORDER_CREATE_ACTION("order_create"),
    ORDER_PAY_ACTION("order_pay"),
    ORDER_CLOSE_ACTION("order_close");

    private final String action;
}
