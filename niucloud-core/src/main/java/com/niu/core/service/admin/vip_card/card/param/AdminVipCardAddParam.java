package com.niu.core.service.admin.vip_card.card.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminVipCardAddParam {
    @NotBlank(message = "cardName不能为空")
    private String cardName;
    private String desc;
    private Integer isTrialOpen;
    private Integer trialDay;
    private BigDecimal trialPrice;
    private List<BenefitsConfigItem> benefitsConfig;
    private String cardConfig;
    private ShareConfig shareConfig;
    private Integer sort;
    private List<CardItemParam> itemList;

    @Data
    public static class BenefitsConfigItem {
        private Integer benefitsId;
        private BenefitsItem benefitsItem;
        // 其他字段...
    }

    @Data
    public static class BenefitsItem {
        private Integer benefitsItemId;
    }

    @Data
    public static class ShareConfig {
        private String shareTitle;
        private String shareDesc;
        private String shareImageH5;
        private String shareImageWeapp;
    }

    @Data
    public static class CardItemParam {
        private Integer itemId;
        private String itemName;
        private BigDecimal price;
        private BigDecimal costPrice;
        private BigDecimal renewPrice;
        private Integer isTrial;
        private Integer isRenew;
        private Integer isSale;
        private Integer timeLimit;
        private String timeType;
        private String itemConfig;
    }
}