package com.niu.core.service.core.local_delivery.param;

import lombok.Data;

@Data
public class LocalDeliveryOrderFinishParam {

    private String tradeNo;

    private String tradeType;

    private Integer id;

    private Integer cancelReasonId;

    private String cancelReason;

    private Integer mainId;

    private String mainType;

    private String mainName;
}
