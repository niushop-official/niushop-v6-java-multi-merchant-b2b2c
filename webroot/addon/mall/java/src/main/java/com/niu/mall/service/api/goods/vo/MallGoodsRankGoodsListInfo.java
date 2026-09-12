package com.niu.mall.service.api.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.core.entity.goods.MallGoodsSku;
import lombok.Data;

@Data
public class MallGoodsRankGoodsListInfo {
    private Integer siteId;
    private Integer goodsId;
    private String goodsName;
    private String goodsCover;
    private String unit;
    private Integer saleNum;
    private String memberDiscount;
    private String vipCardDiscount;
    private Integer statSaleNum;
    private Integer statCollectNum;
    private Integer statEvaluateNum;
    private Integer statAccessNum;
    private String createTime;
    private Integer goodsSort;
    private Integer sort;
    private String goodsCoverThumbMid;
    @JsonProperty("goodsSku")
    private MallGoodsSkuRankVo goodsSku;
    private Integer rankNum;
}