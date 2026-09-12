package com.niu.mall.service.api.shop.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ShopApplyAddParam {
    @NotBlank(message = "site_name不能为空")
    private String siteName;

    @NotBlank(message = "user_name不能为空")
    private String userName;

    @NotBlank(message = "user_mobile不能为空")
    private String userMobile;

    @NotNull(message = "category_id不能为空")
    private Integer categoryId;

    private Integer childCategoryId;

    private Integer circleId;

    private String businessLicense;

    private Integer status = 1;

    @JsonProperty("mobile_key")
    @NotBlank(message = "mobile_key不能为空")
    private String mobileKey;

    @JsonProperty("mobile_code")
    @NotBlank(message = "mobile_code不能为空")
    private String mobileCode;

    private Map<String, Object> formData;


}
