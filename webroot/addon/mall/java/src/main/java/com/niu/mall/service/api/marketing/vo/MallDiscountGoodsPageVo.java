package com.niu.mall.service.api.marketing.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 限时折扣活动商品分页项（对齐 PHP DiscountService::getGoodsPage）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallDiscountGoodsPageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsId;

    private Integer skuId;

    private String skuName;

    private String skuImage;

    private Integer stock;

    private BigDecimal price;

    private String goodsTypeName;

    private String goodsType;

    private String goodsName;
}
