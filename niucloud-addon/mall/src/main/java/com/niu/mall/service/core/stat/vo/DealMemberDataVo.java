package com.niu.mall.service.core.stat.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DealMemberDataVo {
    private String levelName;
    private Integer memberCount;
    private Integer payMemberCount;
    private BigDecimal goodsMoney;
    private BigDecimal orderMoney;
}
