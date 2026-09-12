package com.niu.core.service.admin.vip_card.card.param;

import lombok.Data;

import java.util.List;

@Data
public class AdminSetConfigParam {
    private String name;
    private String icon;
    private String type;
    private String desc;
    private String explain;
    private String addon;
    private Integer status;
    private List<BenefitsItem> itemList;

    @Data
    public static class BenefitsItem {
        private Integer benefitsItemId;
        private String name;
        private Object benefitsConfig;
    }
}