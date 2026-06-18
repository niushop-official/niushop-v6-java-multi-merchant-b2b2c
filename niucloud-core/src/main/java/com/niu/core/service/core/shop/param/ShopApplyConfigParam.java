package com.niu.core.service.core.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ShopApplyConfigParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "入驻申请图片不能为空")
    private String applyBanner;
    @NotNull(message = "入驻申请标题不能为空")
    private String applyTitle;
    @NotNull(message = "入驻申请表单不能为空")
    private Integer formId;
}
