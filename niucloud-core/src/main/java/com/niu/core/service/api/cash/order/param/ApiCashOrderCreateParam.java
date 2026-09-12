package com.niu.core.service.api.cash.order.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiCashOrderCreateParam implements Serializable {

    private String orderKey;
}
