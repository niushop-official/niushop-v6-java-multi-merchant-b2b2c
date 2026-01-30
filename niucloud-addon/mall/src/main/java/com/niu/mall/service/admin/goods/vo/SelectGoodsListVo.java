package com.niu.mall.service.admin.goods.vo;

import com.niu.core.common.utils.CommonUtils;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ShopGoods列表视图
 */
@Data
public class SelectGoodsListVo implements Serializable {

    private Integer siteId;
    private Integer goodsId;
    private String goodsName;
    private String goodsType;
    private String subTitle;
    private String goodsImage;
    private String unit;
    private String goodsVideo;
    private String goodsDesc;
    private Integer sourceId;
    private String goodsCover;
    private Integer stock;
    private Integer supplyId;
    private String goodsCoverThumbSmall;
    private String specType;

    private List<GoodsSkuVO> skuList;
    private List<GoodsSpecVO> goodsSpec;
    private SupplyInfo supply;


    @Data
    public static class GoodsSkuVO {
        private Integer goodsId;
        private Integer skuId;
        private Integer isDefault;
        private String skuName;
        private String skuImage;
        private BigDecimal price;
        private BigDecimal costPrice;
        private String skuSpecFormat;
        private Integer sourceSkuId;
        private Integer stock;
        private Integer supplyId;
    }

    @Data
    public static class GoodsSpecVO {
        private Integer goodsId;
        private String specName;
        private String specValues;
    }

    @Data
    public static class SupplyInfo {
        private Integer siteId;
        private String siteName;
    }

    private String getGoodsCoverThumbSmall(){
        return CommonUtils.thumbImageSmall(this.siteId, this.goodsCover);
    }
}
