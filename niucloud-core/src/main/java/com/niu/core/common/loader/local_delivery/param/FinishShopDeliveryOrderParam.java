package com.niu.core.common.loader.local_delivery.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class FinishShopDeliveryOrderParam implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String outDeliveryNo;
    private String deliveryNo;
    /** 操作人ID */
    private Integer mainId;
    /** 操作人名称 */
    private String mainType;
    /** 操作人名称 */
    private String mainName;


    private String cancelReason;

    /** 状态 */
    private String status;
    private String notifyUrl;
}
