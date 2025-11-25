package com.niu.mall.controller.api;

import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.service.core.order.ICoreOrderConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("api/mall")
public class ConfigController {

    @Resource
    ICoreOrderConfigService coreOrderConfigService;


    /**
     * 获取评价配置
     *
     * @return
     */
    @GetMapping("/goods/evaluate/config")
    public Result getEvaluateConfig() {
        return Result.success(coreOrderConfigService.getEvaluateConfig());
    }
}
