package com.niu.core.service.api.vip_card.card.vo;

import lombok.Data;

import java.util.List;

@Data
public class ApiVipCardComponentsVo {
    private Integer cardId;
    private String cardName;
    private Integer benefitsNum;
    private Integer benefitsOpenNum;
    private Integer isOpen = 0;
    private List<BenefitsSimple> benefitsList;

    @Data
    public static class BenefitsSimple {
        private Integer benefitsId;
        private String name;
        private String icon;
        private String type;
        private String desc;
    }
}