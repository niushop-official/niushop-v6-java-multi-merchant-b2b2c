package com.niu.core.service.admin.vip_card.card.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminVipCardInfoVo {
    private Integer cardId;
    private Integer siteId;
    private String cardName;
    private String desc;
    private Integer status;
    private Integer isTrialOpen;
    private Integer trialDay;
    private BigDecimal trialPrice;
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonCamelToUnderlineSerializer.class)
    private String benefitsConfig;
    private String benefitsId;
    private String benefitsItemId;
    private String cardConfig;
    private ShareConfigVo shareConfig;
    private Long createTime;
    private Integer sort;
    private Integer memberNum;
    private List<VipCardItemVo> item;

    @Data
    public static class ShareConfigVo {
        private String shareTitle;
        private String shareDesc;
        private String shareImageH5;
        private String shareImageWeapp;
    }

    @Data
    public static class VipCardItemVo {
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