package com.niu.mall.service.api.shop.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountCheckParam {
    @NotBlank(message = "account不能为空")
    private String account;
}
