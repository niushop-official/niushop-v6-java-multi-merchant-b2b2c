package com.niu.mall.controller.siteapi.order;


import com.niu.core.common.domain.Result;
import com.niu.mall.service.core.order.param.SiteOrderConfigParam;
import com.niu.mall.service.core.order.vo.SiteOrderConfigVo;
import com.niu.mall.service.site.order.IOrderConfigService;
import com.niu.mall.service.core.order.param.OrderConfigParam;
import com.niu.mall.service.core.order.vo.OrderConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 订单配置制器
 */

@RestController
@RequestMapping("adminapi/mall/site/order/config")
public class ConfigController {

    @Resource
    IOrderConfigService orderConfigService;

    /**
     * 获取订单设置
     */
    @GetMapping("")
    public Result<SiteOrderConfigVo> get() {
        return Result.success(orderConfigService.getConfig());
    }

    /**
     * 设置订单配置
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> set(@Validated @RequestBody SiteOrderConfigParam orderConfigParam) {
        orderConfigService.setConfig(orderConfigParam);
        return Result.success();
    }



}
