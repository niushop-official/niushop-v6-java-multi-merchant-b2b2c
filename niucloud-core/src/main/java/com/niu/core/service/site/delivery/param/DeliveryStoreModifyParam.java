package com.niu.core.service.site.delivery.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeliveryStoreModifyParam implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 门店ID
     */
    private Integer storeId;

    /**
     * 开启状态 1开启 0关闭
     */
    private Integer status;
}
