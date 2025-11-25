package com.niu.mall.service.api.goods.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品收藏记录实体
 */
@Data
public class MallGoodsCollectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // 
    private Integer siteId; // 站点id
    private Integer memberId; // 会员id
    private Integer goodsId; // 商品id
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 收藏时间
    private String goodsName;
    private String goodsCover;
    private String skuName;
    private BigDecimal price;
    private BigDecimal salePrice;
    private BigDecimal showPrice;
    private String showType;
    private Integer status;
    private String memberPrice;
    private String memberDiscount;
    private Integer skuId;
    
    /** 是否参与限时折扣（从查询结果中获取，用于组装嵌套对象） */
    private Integer isDiscount;
    
    /** 商品对象（嵌套结构，供前端使用） */
    @JsonProperty("goods")
    private GoodsInfo goods;
    
    /** SKU对象（嵌套结构，供前端使用） */
    @JsonProperty("goodsSku")
    private GoodsSkuInfo goodsSku;

    private ICoreUploadService coreUploadService() {
        return SpringContext.bean(ICoreUploadService.class);
    }

    public String getGoodsCoverThumbMid() {
        return coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
    }
    
    /**
     * 商品信息（嵌套对象）
     */
    @Data
    public static class GoodsInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        
        @JsonProperty("goods_id")
        private Integer goodsId;
        
        @JsonProperty("is_discount")
        private Integer isDiscount;
        
        private String goodsName;
        private String goodsCover;
        private String memberDiscount;
        private Integer status;
    }
    
    /**
     * SKU信息（嵌套对象）
     */
    @Data
    public static class GoodsSkuInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        
        @JsonProperty("sku_id")
        private Integer skuId;
        
        private String skuName;
        private BigDecimal price;
        private BigDecimal salePrice;
        private BigDecimal memberPrice;
        private String memberDiscount;
    }

}