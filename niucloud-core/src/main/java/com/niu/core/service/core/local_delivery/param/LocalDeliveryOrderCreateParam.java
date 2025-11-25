package com.niu.core.service.core.local_delivery.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class LocalDeliveryOrderCreateParam {

    private OrderDataInfo orderData;
    private Integer deliveryId;
    private String deliveryNo;
    private String remark;
    private String body;
    private Integer localDeliveryId;
    private String localDeliveryNo;
    private String localDeliveryType;
    private String storeNo;
    @Data
    public static class OrderDataInfo{
        private Integer siteId;
        private Integer takeStoreId;
        private Integer orderId;
        private String orderNo;
        private String orderType;
        private String takerFullAddress;
        private Integer takerProvinceId;
        private Integer takerCityId;
        private Integer takerDistrictId;
        private String takerLongitude;
        private String takerLatitude;
        private String body;
        private BigDecimal deliveryMoney;
    }
}
