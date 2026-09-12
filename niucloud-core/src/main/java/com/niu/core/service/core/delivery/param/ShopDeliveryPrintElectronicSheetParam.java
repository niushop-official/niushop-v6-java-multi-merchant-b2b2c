package com.niu.core.service.core.delivery.param;

import lombok.Data;

import java.util.List;

@Data
public class ShopDeliveryPrintElectronicSheetParam {
    private Integer siteId;
    private String printType;
    private Integer orderId;
    private Integer electronicSheetId;
    private List<Integer> orderGoodsIds;
    private List<Parcel> list;
    private SenderAddress address;

    @Data
    public static class Parcel {
        private Integer deliveryId;
        private Integer electronicSheetId;
    }

    @Data
    public static class SenderAddress {
        private String contactName;
        private String mobile;
        private Integer provinceId;
        private Integer cityId;
        private Integer districtId;
        private String provinceName;
        private String cityName;
        private String districtName;
        private String address;
    }
}
