package com.niu.core.service.core.local_delivery.param;

import com.niu.core.common.loader.local_delivery.param.AddShopDeliveryOrderParam;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class LocalDeliveryOrderCreateParam {

    private OrderDataInfo orderData;
    private Integer deliveryId;
    private Integer storeId;
    private String deliveryNo;
    private String remark;
    private String body;
    private Integer localDeliveryId;
    private String localDeliveryNo;
    private String localDeliveryType;
    private String storeNo;
    private BigDecimal goodsWeight;
    private String tradeNo;
    private String tradeType;
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
        private BigDecimal orderMoney;
        private String buyerAskDeliveryTime;
        private String takerName;
        private String takerMobile;
    }

    private List<OrderGoodsInfo> orderGoodsData;

    @Data
    public static class OrderGoodsInfo{
        private Integer num;
        private String goodsName;
        private String skuName;
        private Integer skuId;
        private String unit = "件";
    }
}
