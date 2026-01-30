package com.niu.core.service.api.wechat.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class WechatH5OpenIdParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "wxOpenid参数缺失")
    @NotBlank(message = "wxOpenid不能为空")
    private String wxOpenid;
}
