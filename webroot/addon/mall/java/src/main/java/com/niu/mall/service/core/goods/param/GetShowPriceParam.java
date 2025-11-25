package com.niu.mall.service.core.goods.param;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class GetShowPriceParam {

    private Integer skuId;
    private Integer goodsId;

    private BigDecimal price;

    private String memberDiscount;

    private String memberPrice;
    private BigDecimal salePrice;


    private Integer memberId;
}
