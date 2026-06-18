package com.niu.mall.service.core.marketing.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class FullGiftCalculateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer fullGiftId;

    private String title;

    private BigDecimal matchedTotal;

    private List<GiftSkuData> giftList;

    private List<MatchedGoods> matchedGoods;

    @Data
    public static class GiftSkuData implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer goodsId;

        private Integer skuId;

        private Integer num;

        private String skuName;

        private String skuImage;

        private String goodsName;

        private String goodsCover;

        private BigDecimal originalPrice;

        private BigDecimal costGoodsMoney;
    }

    @Data
    public static class MatchedGoods implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer goodsId;

        private Integer skuId;

        private BigDecimal payMoney;

        private String skuKey;
    }

}