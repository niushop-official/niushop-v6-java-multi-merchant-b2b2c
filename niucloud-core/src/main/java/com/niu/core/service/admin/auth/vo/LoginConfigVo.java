package com.niu.core.service.admin.auth.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
@Data
public class LoginConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isCaptcha = 0; // 是否启用验证码
    private Integer isSiteCaptcha = 0; // 是否启用站点验证码
    private Integer isCircleCaptcha = 0; // 是否启用商圈验证码
    private String bg = ""; // 平台端登录背景
    private String siteBg = ""; // 站点登录端背景
    private String circleBg = ""; // 商圈登录端背景
    private String captchaType = "blockPuzzle"; // 平台端验证码类型
    private String siteCaptchaType = "blockPuzzle"; // 站点端验证码类型
    private String circleCaptchaType = "blockPuzzle"; // 商圈端验证码类型

}
