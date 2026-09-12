package com.niu.core.service.admin.vip_card.card.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminVipCardEditParam {
    @NotBlank(message = "cardName不能为空")
    private String cardName;
    private String desc;
    private Integer isTrialOpen;
    private Integer trialDay;
    private BigDecimal trialPrice;
    private List<AdminVipCardAddParam.BenefitsConfigItem> benefitsConfig;
    private String cardConfig;
    private AdminVipCardAddParam.ShareConfig shareConfig;
    private Integer sort;
    private List<AdminVipCardAddParam.CardItemParam> itemList;  // 含 itemId 用于更新
}