package com.niu.core.service.admin.auth.param;

import com.niu.core.enums.sys.AppTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class SendMobileParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "手机号参数缺失")
    private String mobile;

    private String type = "login";

    @NotNull(message = "登录类型参数缺失")
    private String appType = AppTypeEnum.SITE.getName();

    private String captchaKey;//验证码

    private String captchaCode;//验证码参数
}
