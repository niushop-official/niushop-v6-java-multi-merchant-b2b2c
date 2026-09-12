package com.niu.core.service.admin.auth.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 登录配置
 */
@Data
public class LoginConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "bg参数缺失")
    private String bg;

    @NotNull(message = "is_captcha参数缺失")
    private Integer isCaptcha;

    @NotNull(message = "is_site_captcha参数缺失")
    private Integer isSiteCaptcha;

    @NotNull(message = "is_circle_captcha参数缺失")
    private Integer isCircleCaptcha;

    @NotNull(message = "site_bg参数缺失")
    private String siteBg;

    @NotNull(message = "circle_bg参数缺失")
    private String circleBg;

    @NotNull(message = "平台端验证类型参数缺失")
    private String captchaType;

    @NotNull(message = "站点端验证类型参数缺失")
    private String siteCaptchaType;

    @NotNull(message = "商圈端验证类型参数缺失")
    private String circleCaptchaType;
}
