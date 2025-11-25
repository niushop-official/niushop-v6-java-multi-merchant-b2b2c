package com.niu.core.service.core.local_delivery.param;

import com.niu.core.enums.member.AccountTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocalDeliveryOrderCloseParam {

    private String tradeNo;

    private String tradeType;

    private Integer id;

    private Integer cancelReasonId;

    private String cancelReason;

    private Integer mainId;

    private String mainType;

    private String mainName;
}
