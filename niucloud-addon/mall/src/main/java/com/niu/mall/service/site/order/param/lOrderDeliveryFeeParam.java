package com.niu.mall.service.site.order.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单参数
 */
@Data
public class lOrderDeliveryFeeParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;

    private Integer[] orderGoodsIds;

    private String deliveryType;

    private String deliveryWay;

    private String localDeliveryType;

    private BigDecimal goodsWeight;

    private Integer storeId;
}
