package com.niu.core.service.admin.vip_card.card.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminVipCardPageVo {
    private Integer cardId;
    private Integer siteId;
    private String cardName;
    private Integer status;
    private String statusName;
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonCamelToUnderlineSerializer.class)
    private String benefitsConfig;
    private String benefitsId;
    private List<BenefitsPageVo> benefitsList;
    private Long createTime;
    private Integer sort;
    private Integer memberNum;
    private Integer buyNum = 0;       // 购买人数
    private List<CardItemVo> item;

    @Data
    public static class CardItemVo {
        private Integer itemId;
        private String itemName;
        private BigDecimal price;
        private BigDecimal renewPrice;
        private Integer timeLimit;
        private String timeType;
    }

    @Data
    public static class BenefitsPageVo {
        private Integer benefitsId;
        private String icon;
        private String name;
        private String type;
    }
}