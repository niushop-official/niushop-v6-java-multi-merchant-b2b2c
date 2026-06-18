package com.niu.mall.service.api.goods.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class SkuPromotionDetailVo {
    private Map<String, Object> activityList;
    private List<Map<String, Object>> couponList;
    private PriceDetails priceDetails;

    @Data
    public static class PriceDetails {
        private BigDecimal handPrice;
        private PriceList priceList;
    }

    @Data
    public static class PriceList {
        private PriceListData admin_promotion;
        private PriceListData original_price;
        private PriceListData site_promotion;
    }

    @Data
    public static class PriceListData {
        public PriceListData() {
        }

        public PriceListData(BigDecimal price, String desc) {
            this.price = price;
            this.desc = desc;
        }

        private BigDecimal price;
        private String desc;
    }
}
