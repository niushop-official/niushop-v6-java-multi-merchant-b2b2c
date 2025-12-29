package com.niu.mall.service.admin.points_mall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 积分商城商品选择详情返回VO
 */
@Data
public class PointsMallGoodsSelectDetailVo {
    
    /**
     * 商品信息
     */
    private GoodsInfo goodsInfo;
    
    /**
     * 商品信息内部类
     */
    @Data
    public static class GoodsInfo {
        
        /**
         * 商品名称
         */
        private String goodsName;
        
        /**
         * 商品副标题
         */
        private String subTitle;
        
        /**
         * 商品类型
         */
        private String goodsType;
        
        /**
         * 商品封面
         */
        private String goodsCover;
        
        /**
         * 商品图片
         */
        private String goodsImage;
        
        /**
         * 商品视频
         */
        private String goodsVideo;
        
        /**
         * 商品描述
         */
        private String goodsDesc;
        
        /**
         * 商品分类
         */
        private Object goodsMallCategory;
        
        /**
         * 商品单位
         */
        private String unit;
        
        /**
         * SKU列表
         */
        private List<SkuInfo> skuList;
        
        /**
         * 规格类型：single-单规格，multi-多规格
         */
        private String specType;
        
        /**
         * 规格列表（仅多规格时有值）
         */
        private List<SpecInfo> specList;
        
        /**
         * 商品来源
         */
        private String source;
        
        /**
         * 来源商品ID
         */
        private Integer sourceId;
        
        /**
         * 商品类目
         */
        private Object goodsCategory;
        
        /**
         * 商品状态
         */
        private Integer status;
    }
    
    /**
     * SKU信息
     */
    @Data
    public static class SkuInfo {
        
        /**
         * SKU ID
         */
        private Integer skuId;
        
        /**
         * SKU名称
         */
        private String skuName;
        
        /**
         * SKU图片
         */
        private String skuImage;
        
        /**
         * 商品ID
         */
        private Integer goodsId;
        
        /**
         * SKU规格格式
         */
        private String skuSpecFormat;
        
        /**
         * 价格
         */
        private BigDecimal price;
        
        /**
         * 市场价
         */
        private BigDecimal marketPrice;
        
        /**
         * 是否默认：0-否，1-是
         */
        private Integer isDefault;
        
        /**
         * 是否参与积分兑换：0-否，1-是
         */
        private Integer isJoin;
    }
    
    /**
     * 规格信息
     */
    @Data
    public static class SpecInfo {
        
        /**
         * 规格ID
         */
        private Integer specId;
        
        /**
         * 商品ID
         */
        private Integer goodsId;
        
        /**
         * 规格名称
         */
        private String specName;
        
        /**
         * 规格值
         */
        private String specValues;
    }
}
