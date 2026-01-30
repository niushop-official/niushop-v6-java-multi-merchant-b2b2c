package com.niu.core.service.admin.recharge.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeParam {
    private String packageName="";
    private BigDecimal price = BigDecimal.ZERO;
    private BigDecimal giveMoney = BigDecimal.ZERO;
    private Integer point = 0;
    private Integer growth = 0;
    private Integer sort = 0;
    private Integer status = 1;
    private Object giftJson;
}
