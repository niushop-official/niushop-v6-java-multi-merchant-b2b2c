package com.niu.core.event.fenxiao.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分销商品规格信息
 */
@Data
public class FenxiaoGoodsSkuVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规格ID
     */
    private Long skuId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 规格名称
     */
    private String skuName;

    /**
     * 规格图片
     */
    private String skuImage;

    /**
     * 规格编码
     */
    private String skuNo;

    /**
     * 规格格式
     */
    private String skuSpecFormat;

    /**
     * 销售价格
     */
    private BigDecimal price;

    /**
     * 市场价
     */
    private BigDecimal marketPrice;

    /**
     * 活动价
     */
    private BigDecimal salePrice;

    /**
     * 成本价
     */
    private BigDecimal costPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 是否默认规格：1=是，0=否
     */
    private Integer isDefault;
}
