package com.niu.mall.service.core.goods.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ShowPriceVo {

    private BigDecimal showPrice;

    @Builder.Default
    private String showType = "";

    private Integer activeId;
}
