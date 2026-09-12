package com.niu.core.service.admin.vip_card.card.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class AdminBenefitsConfigVo {
    private Integer benefitsId;
    private String name;
    private String icon;
    private String type;
    private String desc;
    private String explain;
    private Integer status;
    private String addon;
    private Integer sort;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<BenefitsItemVo> itemList;

    @Data
    public static class BenefitsItemVo {
        private Integer benefitsItemId;
        private String name;
        private Object benefitsConfig;
    }
}
