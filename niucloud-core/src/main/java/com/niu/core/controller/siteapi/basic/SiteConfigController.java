package com.niu.core.controller.siteapi.basic;

import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.basic.site.IConfigService;
import com.niu.core.service.admin.basic.site.param.SiteConfigParam;
import com.niu.core.service.admin.basic.site.vo.SiteConfigVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 通用设置
 */
@RestController
@RequestMapping("adminapi/basic/site/config")
public class SiteConfigController {

    @Resource
    IConfigService configService;

    /**
     * 获取通用设置
     */
    @GetMapping("")
    public Result<SiteConfigVo> getConfig() {
        return Result.success(configService.getConfig());
    }

    /**
     * 设置通用设置
     */
    @PostMapping("")
    public Result<Object> setConfig(@Validated @RequestBody SiteConfigParam param) {
        configService.setConfig(param);
        return Result.success();
    }
}
