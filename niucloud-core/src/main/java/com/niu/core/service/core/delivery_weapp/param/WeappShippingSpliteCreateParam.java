package com.niu.core.service.core.delivery_weapp.param;

import lombok.Data;

import java.util.List;

@Data
public class WeappShippingSpliteCreateParam {
    private Integer shippingId;

    private String transactionId;
    private Boolean isAllDelivered;
    private List<WeappUploadShippingParam.UploadShippingParam> shippinglist;
    private Integer siteId;
    private Integer tradeId;
    private String tradeType;
    private String tradeNo;
    private Integer splitTradeId;
    private String splitTradeType;
    private String splitTradeNo;
}

