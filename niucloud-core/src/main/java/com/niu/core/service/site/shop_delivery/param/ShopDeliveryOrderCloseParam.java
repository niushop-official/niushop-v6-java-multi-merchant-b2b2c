package com.niu.core.service.site.shop_delivery.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品参数参数
 */
@Data
public class ShopDeliveryOrderCloseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer cancelReasonId;

    private String cancelReason;

    private Integer mainId;

    private String mainType;

    private String mainName;

    private Integer id;

}
