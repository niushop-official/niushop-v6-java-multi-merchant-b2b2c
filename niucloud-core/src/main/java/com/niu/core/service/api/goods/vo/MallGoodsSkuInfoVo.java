package com.niu.core.service.api.goods.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* 商品规格表详情
*/
@Data
public class MallGoodsSkuInfoVo implements Serializable {

private static final long serialVersionUID = 1L;


    /** sku成本价 */
    private BigDecimal costPrice;


    /** 商品id */
    private Integer goodsId;


    /** 是否默认 */
    private Integer isDefault;


    /** 划线价 */
    private BigDecimal marketPrice;


    /** 会员价，json格式，指定会员价，数据结构为：{"level_1":"10.00","level_2":"10.00"} */
    private String memberPrice;


    /** sku单价 */
    private BigDecimal price;


    /** 销量 */
    private Integer saleNum;


    /** 实际卖价（有活动显示活动价，默认原价） */
    private BigDecimal salePrice;


    /** 站点id */
    private Integer siteId;


    /** 商品sku_id */
    private Integer skuId;


    /** sku主图 */
    private String skuImage;


    /** 商品sku名称 */
    private String skuName;


    /** 商品sku编码 */
    private String skuNo;


    /** sku规格格式 */
    private String skuSpecFormat;


    /** 商品sku库存 */
    private Integer stock;


    /** 体积（单位立方米） */
    private BigDecimal volume;


    /** 重量（单位kg） */
    private BigDecimal weight;

}