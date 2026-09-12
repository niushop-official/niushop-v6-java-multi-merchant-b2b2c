package com.niu.core.controller.api.login;

import cn.dev33.satoken.annotation.SaIgnore;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.member.MemberLoginTypeEnum;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.login.param.AccountLoginParam;
import com.niu.core.service.api.login.param.MobileLoginParam;
import com.niu.core.service.api.login.param.ResetPasswordParam;
import com.niu.core.service.api.login.param.SendMobileCodeParam;
import com.niu.core.service.api.login.vo.LoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "前台登录")
@RestController
@RequestMapping("/api")
public class LoginController {

    @Resource
    IRegisterService registerService;

    @Resource
    ILoginService loginService;

    /**
     * 账号登录
     *
     * @param param
     * @return
     */
    @Operation(summary = "账号登录")
    @GetMapping("/login")
    public Result<LoginVo> account(@Validated AccountLoginParam param) {
        registerService.checkLoginConfig(MemberLoginTypeEnum.USERNAME.getType());
        return Result.success(loginService.account(param));
    }

    /**
     * 手机验证码登录
     *
     * @param param
     * @return
     */
    @Operation(summary = "手机号登录")
    @PostMapping("/login/mobile")
    public Result<LoginVo> mobile(@Validated @RequestBody MobileLoginParam param) {
        //registerService.checkLoginConfig(MemberLoginTypeEnum.MOBILE.getType());
        registerService.checkMobileCode(param.getMobile(), param.getMobileKey(), param.getMobileCode());
        return Result.success(loginService.mobile(param));
    }

    /**
     * 重置密码
     *
     * @param param
     * @return
     */
    @Operation(summary = "重置密码")
    @PostMapping("/password/reset")
    public Result<?> resetPassword(@Validated @RequestBody ResetPasswordParam param) {
        registerService.checkMobileCode(param.getMobile(), param.getMobileKey(), param.getMobileCode());
        loginService.resetPassword(param);
        return Result.success();
    }

    /**
     * 登录注册配置
     *
     * @return
     */
    @Operation(summary = "获取登录配置")
    @GetMapping("/login/config")
    public Result<LoginConfigVo> getLoginConfig() {
        return Result.success(loginService.getLoginConfig());
    }

    /**
     * 手机号发送验证码
     *
     * @return
     */
    @Operation(summary = "发送手机验证码")
    @PostMapping("/send/mobile/{type}")
    public Result<?> sendMobileCode(@PathVariable("type") String type, @Validated @RequestBody SendMobileCodeParam param) {
        param.setType(type);
        return Result.success(loginService.sendMobileCode(param));
    }

    /**
     * 退出登录
     *
     * @return
     */
    @Operation(summary = "退出登录")
    @PutMapping("/auth/logout")
    @SaIgnore
    public Result logout() {
        loginService.logout();
        return Result.success("MEMBER_LOGOUT");
    }
}
