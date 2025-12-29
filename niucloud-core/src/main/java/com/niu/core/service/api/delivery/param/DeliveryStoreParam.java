package com.niu.core.service.api.delivery.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 商家地址库参数
 */
@Data
public class DeliveryStoreParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String pickUpType;

    private Object position;

    private String keyword;

}
