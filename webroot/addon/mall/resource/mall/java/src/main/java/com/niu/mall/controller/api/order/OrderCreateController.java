package com.niu.mall.controller.api.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.ParamHelper;
import com.niu.mall.service.api.order.IOrderCreateService;
import com.niu.mall.service.core.order.param.MallOrderCalculateParam;
import com.niu.mall.service.core.order.param.MallOrderCreateParam;
import com.niu.mall.service.core.order.vo.MallOrderCreateDataVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/mall/order_create")
@SaCheckLogin
public class OrderCreateController {

    @Resource
    IOrderCreateService orderCreateService;

    /**
     * 计算
     */
    @PostMapping("/calculate")
    public Result<MallOrderCreateDataVo> calculate(@Validated @RequestBody MallOrderCalculateParam param) {
        return Result.success(orderCreateService.calculate(param));
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result create(@Validated @RequestBody MallOrderCreateParam param) {
        return Result.success(orderCreateService.create(param));
    }

    /**
     * 查询店铺优惠券
     */
    @PostMapping("/coupon")
    public Result getCoupon(@Validated @RequestBody Map<String, Map<Integer, Map<String, Object>>> map) {
        Map<Integer, Map<String, Object>> body = map.get("body");
        return Result.success(orderCreateService.getCoupon(body));
    }

    /**
     * 查询平台优惠券（对齐 PHP GET）
     */
    @GetMapping("/platform_coupon")
    public Result getPlatformCouponGet(@RequestParam Map<String, String> allParams) {
        MallOrderCalculateParam param = ParamHelper.mapToBean(allParams, MallOrderCalculateParam.class);
        return Result.success(orderCreateService.getPlatformCoupon(param));
    }

    /**
     * 查询平台优惠券（兼容 POST JSON）
     */
    @PostMapping("/platform_coupon")
    public Result getPlatformCoupon(@RequestBody Map<String, Object> map) {
        return Result.success(orderCreateService.getPlatformCoupon(map));
    }

    /**
     * 查询店铺优惠券
     */
    @PostMapping("/store")
    public Result getStore(@Validated @RequestBody Map<String, Map<Integer, Map<String, Object>>> map) {
        Map<Integer, Map<String, Object>> body = map.get("body");
        return Result.success(orderCreateService.getCoupon(body));
    }
}
