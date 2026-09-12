package com.niu.core.service.core.marketing.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class FullReductionCalculateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer memberId;

    private List<GoodsSkuData> goodsSkuData;

    private boolean checkRule;

    @Data
    public static class GoodsSkuData implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer goodsId;

        private Integer skuId;

        private BigDecimal payMoney;

        private Integer num;

        private String skuKey;
    }

}