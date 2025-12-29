package com.niu.core.service.admin.auth.param;

import com.niu.core.enums.sys.AppTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserMobileLoginParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "手机号参数缺失")
    private String mobile;

    @NotNull(message = "手机验证码有误")
    private String mobileCode;

    @NotNull(message = "手机验证码有误")
    private String mobileKey;

    @NotNull(message = "登录类型参数缺失")
    private String appType = AppTypeEnum.SITE.getName();
}
