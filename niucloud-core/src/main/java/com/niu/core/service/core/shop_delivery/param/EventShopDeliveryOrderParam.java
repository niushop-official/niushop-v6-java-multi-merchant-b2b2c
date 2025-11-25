package com.niu.core.service.core.shop_delivery.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class EventShopDeliveryOrderParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 操作人ID */
    private Integer mainId;

    /** 操作人名称 */
    private String mainName;


    private String cancelReason;

    /** 状态 */
    private String status;
}
