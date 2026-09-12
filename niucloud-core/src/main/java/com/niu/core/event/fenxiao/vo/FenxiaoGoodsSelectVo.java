package com.niu.core.event.fenxiao.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分销商品选择列表项
 */
@Data
public class FenxiaoGoodsSelectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品封面
     */
    private String goodsCover;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer saleNum;

    /**
     * 商品状态
     */
    private Integer status;

    /**
     * 默认规格ID
     */
    private Integer skuId;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 佣金计算价
     */
    private BigDecimal calculatePrice;

    /**
     * 是否已加入分销：1=是，0=否
     */
    private Integer isSelected;

    /**
     * 站点信息
     */
    @JsonProperty("site")
    private SiteInfo site;

    /**
     * 站点信息
     */
    @Data
    public static class SiteInfo implements Serializable {

        /**
         * 站点ID
         */
        private Integer siteId;

        /**
         * 站点名称
         */
        private String siteName;
    }
}
