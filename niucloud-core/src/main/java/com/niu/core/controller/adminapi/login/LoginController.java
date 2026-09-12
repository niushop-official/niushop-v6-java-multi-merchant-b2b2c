package com.niu.core.controller.adminapi.login;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.addon.param.AddonSearchParam;
import com.niu.core.service.admin.auth.IConfigService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.param.AdminForgetPasswordParam;
import com.niu.core.service.admin.auth.param.SendMobileParam;
import com.niu.core.service.admin.auth.param.UserLoginParam;
import com.niu.core.service.admin.auth.param.UserMobileLoginParam;
import com.niu.core.service.admin.auth.vo.LoginConfigVo;
import com.niu.core.service.admin.auth.vo.LoginResultVo;
import com.niu.core.service.admin.sys.ISysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "管理端登录")
@RestController
@RequestMapping("adminapi/login")
public class LoginController {

    @Resource
    ILoginService loginService;
    @Resource
    IConfigService configService;

    @Operation(summary = "账号登录")
    @GetMapping("/{appType}")
    public Result<LoginResultVo> login(@PathVariable("appType") String appType, @Validated UserLoginParam userLoginParam) {
        userLoginParam.setAppType(appType);
        LoginResultVo loginResultVo = loginService.account(userLoginParam);
        return Result.success(loginResultVo);
    }

    @Operation(summary = "手机号登录")
    @PostMapping("/mobile/{appType}")
    public Result<LoginResultVo> mobile(@PathVariable("appType") String appType, @Validated @RequestBody UserMobileLoginParam userLoginParam) {
        userLoginParam.setAppType(appType);
        LoginResultVo loginResultVo = loginService.mobile(userLoginParam);
        return Result.success(loginResultVo);
    }

    /**
     * 获取登录设置
     *
     * @return
     */
    @Operation(summary = "获取登录配置")
    @GetMapping("config")
    public Result<LoginConfigVo> getConfig() {
        return Result.success(configService.getLoginConfig());
    }

    /**
     * 忘记密码重置
     */
    @Operation(summary = "忘记密码重置")
    @PostMapping("/forget_pwd")
    public Result<Object> forgetPassword(@Validated @RequestBody AdminForgetPasswordParam param) {
        loginService.resetPassword(param);
        return Result.success();
    }

    @Operation(summary = "获取当前 Token 信息")
    @RequestMapping("/tokenInfo")
    public SaResult tokenInfo() {
        return SaResult.data(StpUtil.getTokenInfo());
    }

    // 测试注销
    @Operation(summary = "退出登录")
    @RequestMapping("/logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }
}
