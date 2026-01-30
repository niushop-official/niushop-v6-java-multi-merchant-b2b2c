package com.niu.core.service.admin.sms.param;

import lombok.Data;

@Data
public class OrderListParam {
    private String outTradeNo;

    private String status;

    private Long createTimeStart;

    private Long createTimeEnd;
}
