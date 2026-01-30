package com.niu.core.controller.api.captcha;

import com.niu.core.common.domain.Result;
import com.niu.core.enums.captcha.CaptchaImgEnum;
import com.niu.core.service.core.captcha.ICoreCaptchaImgService;
import com.niu.core.service.core.captcha.param.CoreCaptchaValiDateParam;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图文验证码控制器
 */
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Resource
    ICoreCaptchaImgService coreCaptchaImgService;


    /**
     * 创建验证码
     *
     * @param captchaType
     * @return
     */
    @GetMapping("/create")
    public Result create(@Validated @RequestParam(value = "captcha_type",defaultValue = "blockPuzzle") String captchaType){
        return Result.success(coreCaptchaImgService.create(captchaType));
    }


    /**
     * 一次验证验证码
     *
     * @param coreCaptchaValiDateParam
     * @return
     */
    @GetMapping("/check")
    public Result check(@Validated CoreCaptchaValiDateParam coreCaptchaValiDateParam){

        return Result.success(coreCaptchaImgService.check(coreCaptchaValiDateParam));
    }
}
