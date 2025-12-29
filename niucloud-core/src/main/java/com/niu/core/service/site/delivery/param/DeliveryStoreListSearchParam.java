package com.niu.core.service.site.delivery.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeliveryStoreListSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String keyword;
    private String pickUpType;

}
