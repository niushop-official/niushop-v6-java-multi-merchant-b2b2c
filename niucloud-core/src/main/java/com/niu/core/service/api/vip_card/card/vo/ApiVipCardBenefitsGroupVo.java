package com.niu.core.service.api.vip_card.card.vo;

import lombok.Data;

import java.util.List;

@Data
public class ApiVipCardBenefitsGroupVo {
    private List<BenefitsGroupItem> benefitsList;
    private Long total;

    @Data
    public static class BenefitsGroupItem {
        private Long benefitsId;
        private String name;
        private String icon;
        private String type;
        private String desc;
        private String explain;
        private String addon;
        private Integer sort;
        private ApiVipCardBenefitsGroupItemVo item;
    }
}