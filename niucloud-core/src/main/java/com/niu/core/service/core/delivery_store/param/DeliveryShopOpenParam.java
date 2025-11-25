package com.niu.core.service.core.delivery_store.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 配送方式设置参数
*/
@Data
public class DeliveryShopOpenParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String deliveryType;

    private String business;

    private Map<String, Object> extendData;
}
