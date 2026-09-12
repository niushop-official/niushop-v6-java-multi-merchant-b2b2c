package com.niu.core.service.api.vip_card.card.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApiVipCardGoodsVo {

    private Integer siteId;
    private Integer goodsId;
    private String goodsName;
    private String subTitle;
    private String goodsCategory;
    private String goodsType;
    private String goodsTypeName;
    private String goodsCover;
    private String goodsCoverThumbSmall;
    private String goodsCoverThumbMid;
    private String unit;
    private Integer status;
    private Integer saleNum;
    private String memberDiscount;
    private String vipCardDiscount;
    private Integer isDiscount;
    private String virtualReceiveType;
    private String memberPrice;
    private BigDecimal salePrice;
    /** 原价（展示用，对齐 PHP item.cost_price） */
    private BigDecimal costPrice;

    // 关联的 SKU 信息（通常为默认 SKU）
    @JsonProperty("goodsSku")
    private GoodsSkuInfo goodsSku;

    // 站点信息
    private SiteInfo site;

    @Data
    public static class GoodsSkuInfo {
        private Long skuId;
        private BigDecimal price;
        private BigDecimal costPrice;
        private BigDecimal showPrice;
        private String showType;
        private String memberDiscount;
        private String vipCardDiscount;
        private String skuImage;
        private String memberPrice;
        private String svipPrice;
        private String svipDiscount;
        private BigDecimal salePrice;
    }

    @Data
    public static class SiteInfo {
        private Integer siteId;
        private String siteName;
    }
}