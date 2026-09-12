package com.niu.core.service.api.vip_card.card.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.core.vip_card.card.vo.VipCardItemVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ApiVipCardDetailVo {
    private Integer cardId;
    private String cardName;
    private String desc;
    private Integer status;
    private Integer isTrialOpen;
    private Integer trialDay;
    private BigDecimal trialPrice;
    private String benefitsConfig;
    private String benefitsId;
    private String cardConfig;
    private String shareConfig;
    private List<BenefitsInfo> benefitsList;
    private List<VipCardItemVo> item;
    private Boolean isOpen;
    private VipCardBuyInfo buyInfo;
    private MemberInfo memberInfo;

    @Data
    public static class BenefitsInfo {
        private Integer benefitsId;
        private String icon;
        private String name;
        private String desc;
    }

    @Data
    public static class VipCardBuyInfo {
        private Integer id;
        private Integer isExpire;
        private Integer isTrial;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long expireTime;
    }

    @Data
    public static class MemberInfo {
        private Integer memberId;
        private String nickname;
        private String headimg;
    }
}