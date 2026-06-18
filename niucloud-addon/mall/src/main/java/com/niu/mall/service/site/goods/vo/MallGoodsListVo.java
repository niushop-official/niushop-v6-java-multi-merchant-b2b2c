package com.niu.mall.service.site.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.mall.enums.goods.GoodsAuditStatusEnum;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
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
    private Integer shopSort;  // 排序
    private String goodsTypeName;
    private String goodsEditPath;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    private String memberDiscount;
    private Integer isGift;
    @JsonProperty("goodsSku")
    private GoodsSku goodsSku;
    /**
     * 支付总金额
     */
    private BigDecimal payMoney;
    /**
     * 供货商商品id
     */
    private Integer supplyGoodsId;
    /**
     * 供货商id
     */
    private Integer supplyId;
    /**
     * 供货商名称
     */
    private String supplyName;
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String active;
    private String goodsCategory;
    private String unit;

    @Data
    public static class GoodsSku {
        private Integer goodsId;
        private Integer skuId;
        private BigDecimal price;
        private String memberPrice;
    }

    private String statusName;

    public String getStatusName() {
        return GoodsStatusEnum.getNameByStatus(this.status);
    }

    private String auditStatusName;

    public String getAuditStatusName() {
        return GoodsAuditStatusEnum.getNameByStatus(this.auditStatus);
    }

    public String getGoodsTypeName() {
        return GoodsTypeEnum.getNameByType(this.goodsType);
    }

    public String getGoodsEditPath() {
        Map<String, Map<String, Object>> map = GoodsTypeEnum.getMap();
        return map.get(this.goodsType).get("path").toString();
    }
}
