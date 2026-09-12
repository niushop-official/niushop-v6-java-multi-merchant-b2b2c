package com.niu.mall.service.api.goods.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * VIP 卡专属商品联表查询结果
 */
@Data
public class MallVipCardGoodsJoinVo {

    private Integer siteId;
    private Integer goodsId;
    private String goodsName;
    private String subTitle;
    private String goodsCategory;
    private String goodsType;
    private String goodsCover;
    private String unit;
    private Integer status;
    private Integer saleNum;
    private String memberDiscount;
    private String vipCardDiscount;
    private Integer isDiscount;
    private String virtualReceiveType;

    private Long skuId;
    private BigDecimal price;
    private BigDecimal costPrice;
    private String memberPrice;
    private BigDecimal salePrice;
    private String svipDiscount;
    private String svipPrice;

    private Integer siteSiteId;
    private String siteName;
}
