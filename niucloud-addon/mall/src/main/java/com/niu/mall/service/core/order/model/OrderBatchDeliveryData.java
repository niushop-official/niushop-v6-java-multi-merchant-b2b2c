package com.niu.mall.service.core.order.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderBatchDeliveryData {
    private String path;
    private String type;

    //  print_delivery 和 select_delivery(只有list)
    private Integer addressId;
    private Address address;
    private String deliveryType;
    private String deliveryWay;
    private Integer electronicSheetId;
    private Integer expressCompanyId;
    private List<Orders> list;

    @Data
    public static class Address {
        private String address;
        private String cityName;
        private String contactName;
        private String districtName;
        private String mobile;
        private String provinceName;
    }

    @Data
    public static class Orders {
        private Integer orderId;
        private List<Integer> orderGoodsIds;
    }
}
