package com.niu.mall.service.api.marketing.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MarketingListParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<GoodsData> goodsData;

    private Integer memberId;

    @Data
    public static class GoodsData {
        private Integer goodsId;
        private Integer num;
        private BigDecimal price;
        private Integer siteId;
        private Integer skuId;
    }
}
