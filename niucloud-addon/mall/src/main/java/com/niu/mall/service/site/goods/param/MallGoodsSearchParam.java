package com.niu.mall.service.site.goods.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品参数
 */
@Data
public class MallGoodsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String goodsName;

    private String goodsType;

    private String subTitle;

    private String goodsCover;

    private String goodsImage;

    private String goodsCategory;

    private String goodsDesc;

    private Integer brandId;

    private Integer labelIds;

    private String serviceIds;

    private String unit;

    private Integer stock;

    private Integer saleNum;

    private Integer virtualSaleNum;

    private Integer status;
    /**
     * 复合条件 同时查询商品状态和审核状态
     */
    private Integer statusType;

    private String deliveryType;

    private Integer isFreeShipping;

    private String feeType;

    private BigDecimal deliveryMoney;

    private Integer deliveryTemplateId;

    private Integer virtualAutoDelivery;

    private String virtualReceiveType;

    private Integer virtualVerifyType;

    private Integer virtualIndate;

    private Integer supplierId;

    private String goodsSource;

    private Integer attrId;

    private String attrFormat;

    private Integer isDiscount;

    private String memberDiscount;

    private Integer posterId;

    private String order;

    private String sort;

    private Integer startSaleNum = 0;

    private Integer endSaleNum = 0;

    private Double startPrice = 0.00;

    private Double endPrice = 0.00;

    private Long startTime;

    private Long endTime;
}
