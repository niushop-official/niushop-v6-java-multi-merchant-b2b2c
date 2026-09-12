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

    /**
     * 付费会员卡折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price
     */
    private String vipCardDiscount;

    /**
     * 付费会员折扣 JSON
     */
    private String svipDiscount;

    /**
     * 付费会员价 JSON
     */
    private String svipPrice;

    private Integer memberId;
}
