package com.niu.mall.service.admin.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class GetBuySkuSelectVo {

    @JsonProperty("sku_image_thumb_small")
    private String skuImageThumbSmall;
    @JsonProperty("sku_image_thumb_mid")
    private String skuImageThumbMid;
    @JsonProperty("sku_image_thumb_big")
    private String skuImageThumbBig;
    @JsonProperty("site_id")
    private Integer siteId;
    @JsonProperty("sku_id")
    private Integer skuId;
    @JsonProperty("sku_name")
    private String skuName;
    @JsonProperty("sku_image")
    private String skuImage;
    @JsonProperty("sku_no")
    private String skuNo;
    @JsonProperty("goods_id")
    private Integer goodsId;
    @JsonProperty("sku_spec_format")
    private String skuSpecFormat;
    @JsonProperty("price")
    private String price;
    @JsonProperty("market_price")
    private String marketPrice;
    @JsonProperty("sale_price")
    private String salePrice;
    @JsonProperty("stock")
    private Integer stock;
    @JsonProperty("weight")
    private String weight;
    @JsonProperty("volume")
    private String volume;
    @JsonProperty("sale_num")
    private Integer saleNum;
    @JsonProperty("is_default")
    private Integer isDefault;
    @JsonProperty("member_price")
    private String memberPrice;
    @JsonProperty("goods")
    private GoodsDTO goods;
    @JsonProperty("skuList")
    private List<MallGoodsSku> skuList;
    @JsonProperty("goodsSpec")
    private List<?> goodsSpec;
    @JsonProperty("has_buy")
    private Integer hasBuy;
    @JsonProperty("manjian_info")
    private MallManjianRuleContentVo manjianInfo;

    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @Data
    public static class GoodsDTO extends MallGoods {
        @JsonProperty("goods_type_name")
        private String goodsTypeName;
        @JsonProperty("goods_cover_thumb_small")
        private String goodsCoverThumbSmall;
        @JsonProperty("goods_cover_thumb_mid")
        private String goodsCoverThumbMid;
        @JsonProperty("goods_cover_thumb_big")
        private String goodsCoverThumbBig;
    }

}
