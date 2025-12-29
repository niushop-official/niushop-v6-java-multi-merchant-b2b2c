package com.niu.core.service.site.local_delivery.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品参数参数
 */
@Data
public class LocalDeliverySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer storeId;

}
