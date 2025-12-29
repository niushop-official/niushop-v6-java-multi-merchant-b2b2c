package com.niu.core.service.site.delivery.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeliveryStoreSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String storeName;
    private String[] createTime;
    private String pickUpType;

}
