package com.niu.core.service.admin.recharge.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeBillParam {
    /**
     * 日期
     */
    private String[] date;
    /**
     * 日期
     */
    private String type = "day";
}
