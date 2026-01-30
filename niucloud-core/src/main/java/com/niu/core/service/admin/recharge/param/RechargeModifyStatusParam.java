package com.niu.core.service.admin.recharge.param;

import lombok.Data;

@Data
public class RechargeModifyStatusParam {
    private Integer packageId;
    private Integer status;
}
