package com.niu.core.service.core.delivery_weapp.param;

import lombok.Data;

import java.util.List;

@Data
public class WeappShippingCreateParam {

    private Integer orderNumberType;
    private String transactionId;
    private Integer logisticsType;
    private Integer deliveryMode;
    private String weappOpenid;

    private Integer siteId;
    private Integer tradeId;
    private String tradeType;
    private String tradeNo;
}

