package com.niu.mall.service.core.goods.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopScoreCalcuParam {
    private BigDecimal qualityScore = new BigDecimal("5");
    private BigDecimal serviceScore = new BigDecimal("5");
    private BigDecimal logisticsScore = new BigDecimal("5");
    private Integer siteId;
}
