package com.niu.core.service.core.local_delivery.param;

import lombok.Data;

@Data
public class LocalDeliveryOrderCreateSave {

    private Integer siteId;
    private Integer storeId;
    private String deliveryService;

    private String deliveryNo;

    private String outDeliveryNo;

    private Integer status;

    private Integer tradeId;

    private String tradeNo;

    private String tradeType;

    private String deliveryEnd;

    private Integer deliveryEndProvinceId;

    private Integer deliveryEndCityId;

    private Integer deliveryEndDistrictId;

    private String deliveryEndLng;

    private String deliveryEndLat;

    private String body;
}
