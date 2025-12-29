package com.niu.mall.service.admin.points_mall.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 积分商城商品选择VO
 */
@Data
public class PointsMallGoodsSelectVo {
    
    private Integer siteId;
    private Integer goodsId;
    private String goodsName;
    private String goodsType;
    private String goodsCover;
    private Integer stock;
    
    // SKU字段
    private Integer skuId;
    private String skuName;
    private Integer skuGoodsId;
    private BigDecimal price;
    private Integer skuStock;
    
    // 站点字段
    private Integer siteSiteId;
    private String siteName;
    private String goodsTypeName;
    private String goodsCoverThumbSmall;
    private String goodsCoverThumbMid;
}
