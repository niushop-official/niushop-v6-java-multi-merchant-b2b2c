package com.niu.core.controller.adminapi.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.web.IWebConfigService;
import com.niu.core.service.admin.web.vo.WebConfigVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 广告表控制器
 */

@RestController
@RequestMapping("adminapi/web")
@SaCheckLogin
public class WebConfigController {

    @Resource
    IWebConfigService webConfigService;

    /**
     * 获取链接
     * @return
     */
    @GetMapping("/link")
    public Result<Object> link() {

        return Result.success(webConfigService.getLink());
    }




    /**
     * 获取电脑端首页设置
     *
     * @return
     */
    @GetMapping("/config")
    public Result<WebConfigVo> getWebConfig() {
        return Result.success(webConfigService.getWebConfig());
    }

    /**
     * 设置电脑端首页配置
     *
     * @param data 配置参数
     * @return Result<Object>
     */
    @PostMapping("/config")
    public Result<Object> setWebConfig(@RequestBody JSONObject data) {
        webConfigService.setWebConfig(data);
        return Result.success();
    }

    /**
     * 获取服务器重启结果
     * @return
     */
    @GetMapping("/restart")
    public Result isRestart() {
        return Result.success();
    }

}
