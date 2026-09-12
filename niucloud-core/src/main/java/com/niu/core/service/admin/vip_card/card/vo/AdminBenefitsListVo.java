package com.niu.core.service.admin.vip_card.card.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminBenefitsListVo {
    private Integer benefitsId;
    private String name;
    private String icon;
    private String type;
    private String desc;
    private Integer status;
    private String statusName;
    private String addon;
    private Integer sort;
    private List<BenefitsItemVo> itemList;

    @Data
    public static class BenefitsItemVo {
        private Integer benefitsItemId;
        private String name;
        private Object benefitsConfig;
    }
}