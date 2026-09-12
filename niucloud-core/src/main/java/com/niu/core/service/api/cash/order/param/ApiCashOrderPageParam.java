package com.niu.core.service.api.cash.order.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiCashOrderPageParam implements Serializable {

    private String status;

    private String[] createTime;
}
