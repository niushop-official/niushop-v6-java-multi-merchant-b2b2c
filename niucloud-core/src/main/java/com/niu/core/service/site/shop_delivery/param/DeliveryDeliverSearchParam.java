package com.niu.core.service.site.shop_delivery.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 配送员参数
 */
@Data
public class DeliveryDeliverSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String deliverName;

    private String deliverMobile;

}
