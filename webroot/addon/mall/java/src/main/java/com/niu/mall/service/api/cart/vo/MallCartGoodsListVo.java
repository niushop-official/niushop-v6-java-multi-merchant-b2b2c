package com.niu.mall.service.api.cart.vo;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.core.enums.goods.GoodsTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 购物车商品列表视图（对齐 PHP CartService::getGoodsList）
 */
@Data
public class MallCartGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer siteId;
    private Integer memberId;
    private Integer goodsId;
    private Integer skuId;
    private Integer num;
    private Integer marketType;
    private Integer marketTypeId;
    private Integer status;
    private String invalidRemark = "";
    /** 对齐 PHP：顶层 sku_spec_format */
    private String skuSpecFormat = "";
    private Goods goods;
    @JsonProperty("goodsSku")
    private GoodsSku goodsSku;
    @JsonProperty("skuList")
    private List<GoodsSku> skuList = new ArrayList<>();
    @JsonProperty("goodsSpec")
    private List<GoodsSpec> goodsSpec = new ArrayList<>();
    private SiteInfo site;
    private List<String> promotionList = new ArrayList<>();

    @Data
    public static class Goods {
        private Integer goodsId;
        private String goodsName;
        private String goodsType;
        private String subTitle;
        private Integer siteId;
        private String goodsCover;
        private String goodsCoverThumbBig;
        private String goodsCoverThumbSmall;
        private String goodsCoverThumbMid;
        private Integer status;
        private String unit;
        private Integer stock;
        private Integer saleNum;
        private Integer isDiscount;
        private String memberDiscount;
        private String vipCardDiscount;
        private Long deleteTime;
        private Integer hasBuy;
        private Integer isLimit;
        private Integer limitType;
        private Integer maxBuy;
        private Integer minBuy;
        private Integer formId;

        public String getGoodsTypeName() {
            return GoodsTypeEnum.getNameByType(this.goodsType);
        }

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsCoverThumbSmall() {
            String imageStr = coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
            if (ObjectUtil.isEmpty(imageStr)) {
                return this.getGoodsCover();
            }
            return imageStr;
        }

        public String getGoodsCoverThumbMid() {
            String imageStr = coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
            if (ObjectUtil.isEmpty(imageStr)) {
                return this.getGoodsCover();
            }
            return imageStr;
        }

        public String getGoodsCoverThumbBig() {
            String imageStr = coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType());
            if (ObjectUtil.isEmpty(imageStr)) {
                return this.getGoodsCover();
            }
            return imageStr;
        }
    }

    @Data
    public static class GoodsSku {
        private Integer skuId;
        private Integer siteId;
        private String skuName;
        private String skuImage;
        private String skuImageThumbBig;
        private String skuImageThumbSmall;
        private String skuImageThumbMid;
        private String skuNo;
        private Integer goodsId;
        private String skuSpecFormat;
        private BigDecimal price;
        private BigDecimal marketPrice;
        private BigDecimal salePrice;
        private BigDecimal costPrice;
        private Integer stock;
        private BigDecimal weight;
        private BigDecimal volume;
        private Integer saleNum;
        private Integer isDefault;
        private Integer isSell;
        private String memberPrice;
        private String memberDiscount;
        private String vipCardDiscount;
        private String svipPrice;
        private String svipDiscount;
        private BigDecimal showPrice;
        private String showType;
        /** 对齐 PHP goodsSku.market_list */
        private List<Map<String, Object>> marketList = new ArrayList<>();

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getSkuImageThumbSmall() {
            String imageStr = coreUploadService().thumb(this.siteId, this.getSkuImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
            if (ObjectUtil.isEmpty(imageStr)) {
                return this.getSkuImage();
            }
            return imageStr;
        }

        public String getSkuImageThumbMid() {
            String imageStr = coreUploadService().thumb(this.siteId, this.getSkuImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
            if (ObjectUtil.isEmpty(imageStr)) {
                return this.getSkuImage();
            }
            return imageStr;
        }

        public String getSkuImageThumbBig() {
            String imageStr = coreUploadService().thumb(this.siteId, this.getSkuImage(), UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType());
            if (ObjectUtil.isEmpty(imageStr)) {
                return this.getSkuImage();
            }
            return imageStr;
        }
    }

    @Data
    public static class GoodsSpec {
        private Integer specId;
        private Integer goodsId;
        private String specName;
        private String specValues;
    }

    @Data
    public static class SiteInfo {
        private Integer siteId;
        private String siteName;
        private String logo;
    }
}
