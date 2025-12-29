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
    public Result sendMobileCode(@Validated @RequestBody SendMobileParam userLoginParam){
        return Result.success(loginService.sendMobileCode(userLoginParam));
    }
}
