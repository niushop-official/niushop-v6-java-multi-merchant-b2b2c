package com.niu.core.controller.adminapi.login;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.auth.IConfigService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.param.SendMobileParam;
import com.niu.core.service.admin.auth.param.UserLoginParam;
import com.niu.core.service.admin.auth.param.UserMobileLoginParam;
import com.niu.core.service.admin.auth.vo.LoginConfigVo;
import com.niu.core.service.admin.auth.vo.LoginResultVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("adminapi/send")
public class LoginSendController {

    @Resource
    ILoginService loginService;

    @PostMapping("/mobile/{type}/{appType}")
    public Result sendMobileCode(@PathVariable("type") String type,
                                 @PathVariable("appType") String appType,
                                 @Validated @RequestBody SendMobileParam userLoginParam) {
        userLoginParam.setType(type);
        userLoginParam.setAppType(appType);
        return Result.success(loginService.sendMobileCode(userLoginParam));
    }
}
