package com.niu.core.service.core.delivery_weapp.param;

import lombok.Data;

import java.util.List;

@Data
public class WeappShippingParam {

    private String transactionId;
    private Integer logisticsType;
    private Integer deliveryMode;
    private List<WeappUploadShippingParam.UploadShippingParam> shippingList;
    private String weappOpenid;
    private Boolean isAllDelivered;
    private Integer orderNumberType;

    private Integer siteId;
    private Integer tradeId;
    private String tradeType;
    private String tradeNo;
    private Integer splitTradeId;
    private String splitTradeType;
    private String splitTradeNo;
}

