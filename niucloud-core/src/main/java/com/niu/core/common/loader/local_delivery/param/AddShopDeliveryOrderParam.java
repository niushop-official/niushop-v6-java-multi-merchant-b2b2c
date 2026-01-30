package com.niu.core.common.loader.local_delivery.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    private Map<String, Object> createParam;

    private List<OrderGoodsInfo> orderGoodsData;
    private BigDecimal goodsWeight;
    private String tradeNo;
    private String tradeType;
    @Data
    public static class OrderDateInfo{
        private Integer orderId;
        private String orderNo;
        private String orderType;
        private BigDecimal deliveryMoney;
        private BigDecimal orderMoney;
        private String takerLongitude; // 收货地址经度
        private String takerLatitude; // 收货详细纬度
        private Integer siteId; // 站点id
        private String takerFullAddress; // 收货详细地址
        private Integer takerProvinceId; // 收货省
        private Integer takerCityId; // 收货市
        private Integer takerDistrictId; // 收货区县
        private String takerName;
        private String takerMobile;
        private String buyerAskDeliveryTime;
    }

    @Data
    public static class OrderGoodsInfo{
        private Integer num;
        private String goodsName;
        private String skuName;
        private Integer skuId;
        private String unit = "件";
    }

    private String notifyUrl;
}
