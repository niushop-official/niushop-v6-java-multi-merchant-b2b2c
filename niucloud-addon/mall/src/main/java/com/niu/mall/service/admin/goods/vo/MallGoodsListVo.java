package com.niu.mall.service.admin.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.goods.GoodsAuditStatusEnum;
import com.niu.core.enums.goods.GoodsStatusEnum;
import com.niu.core.enums.goods.GoodsTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ShopGoods列表视图
 */
@Data
public class MallGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsId;  // 商品id
    private Integer siteId;  // 站点id
    private String goodsName;  // 商品名称
    private String goodsType;  // 商品类型
    private String goodsCover;  // 商品封面
    private String goodsCoverThumbSmall;
    private Integer saleNum;  // 销量
    private Integer status;  // 商品状态（1.正常0下架）
    private Integer auditStatus;  // 审核状态
    private Integer stock;
    private Integer sort;  // 排序
    private String goodsTypeName;
    private String goodsEditPath;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    private String memberDiscount;
    private String vipCardDiscount;
    private Integer isGift;
    private List<GoodsLabelArray> goodsLabelArray;
    @JsonProperty("goodsSku")
    private GoodsSku goodsSku;
    @JsonProperty("label_ids")
    private String labelIds;
    private Integer supplyId;
    private String supplyName;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long applyTime;
    private Integer goodsMallCategory;  // 商品商城分类
    private BigDecimal price;  // 商品价格
    private Integer skuId;

    @Data
    public static class GoodsSku {
        private Integer goodsId;
        private Integer skuId;
        private Integer siteId;
        private String skuName;
        private String skuNo;
        private String skuImage;
        private String skuSpecFormat;
        private BigDecimal costPrice;
        private BigDecimal marketPrice;
        private BigDecimal salePrice;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private BigDecimal price;
        private String memberPrice;
        private Integer stock;
        private Integer volume;
        private Integer weight;
        private Integer isDefault;
        private Integer isSell;
        private Integer supplyId;
        private Integer supplySkuId;
    }

    /**
     * 新增店铺信息-513
     */
    @JsonProperty("site")
    private Site site;

    @Data
    public static class Site {
        private Integer siteId;
        private String siteName;
    }

    public String getGoodsTypeName() {
        return GoodsTypeEnum.getNameByType(this.goodsType);
    }

    private String statusName;

    public String getStatusName() {
        return GoodsStatusEnum.getNameByStatus(this.status);
    }

    private String auditStatusName;

    public String getAuditStatusName() {
        return GoodsAuditStatusEnum.getNameByStatus(this.auditStatus);
    }

    @Data
    public static class GoodsLabelArray {
        @JsonProperty("label_id")
        private Integer labelId;
        @JsonProperty("label_name")
        private String labelName;
    }

    public String getGoodsEditPath() {
        Map<String, Map<String, Object>> map = GoodsTypeEnum.getMap();
        return map.get(this.goodsType).get("path").toString();
    }
}
