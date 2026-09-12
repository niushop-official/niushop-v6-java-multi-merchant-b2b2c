package com.niu.mall.service.core.shop.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShopApplyConfigVo {
    @JsonProperty("apply_title")
    private String applyTitle;      // 商户入驻申请标题
    @JsonProperty("apply_banner")
    private String applyBanner;     // 商户入驻申请banner
    @JsonProperty("form_id")
    private Integer formId;         // 商户入驻申请资料表单id
}
