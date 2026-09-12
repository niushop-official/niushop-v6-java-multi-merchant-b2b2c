package com.niu.mall.service.core.shop.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopInfoOfOrderCreateVo {

    private Integer siteId;
    private String siteName;
    private ShopInfo shop;

    @Data
    public static class ShopInfo {
        private Integer isSelf;
        private BigDecimal serviceRate;
        private Integer businessStatus;
        private Integer circleId;
    }
}
