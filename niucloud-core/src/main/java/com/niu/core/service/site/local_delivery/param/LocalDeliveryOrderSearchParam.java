package com.niu.core.service.site.local_delivery.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品参数参数
 */
@Data
public class LocalDeliveryOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String deliveryNo;

    private String tradeNo;

    private String tradeType;

    private String status;

    private String[] createTime;

}
