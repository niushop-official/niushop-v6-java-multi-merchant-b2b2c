package com.niu.core.common.loader.local_delivery.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 同城配送店铺订单创建参数
*/
@Data
public class AddShopDeliveryOrderParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer deliveryId;
    private String deliveryNo;
    private String remark;
    private String body;
    private OrderDateInfo orderDate;

    @Data
    public static class OrderDateInfo{
        private Integer orderId;
        private String orderNo;
        private String orderType;
        private BigDecimal deliveryMoney;
        private String takerLongitude; // 收货地址经度
        private String takerLatitude; // 收货详细纬度
        private Integer siteId; // 站点id
        private String takerFullAddress; // 收货详细地址
        private Integer takerProvinceId; // 收货省
        private Integer takerCityId; // 收货市
        private Integer takerDistrictId; // 收货区县
    }
}
