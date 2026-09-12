package com.niu.core.service.admin.auth.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class AdminForgetPasswordParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "MOBILE_NEEDED")
    private String mobile;

    @NotBlank(message = "PASSWORD_NEEDED")
    private String password;

    @NotBlank(message = "MOBILE_CAPTCHA_ERROR")
    private String mobileKey;

    @NotBlank(message = "MOBILE_CAPTCHA_ERROR")
    private String mobileCode;
}
