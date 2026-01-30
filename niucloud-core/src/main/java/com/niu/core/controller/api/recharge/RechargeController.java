package com.niu.core.controller.api.recharge;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.service.api.recharge.IRechargeConfigService;
import com.niu.core.service.api.recharge.IRechargeOrderCreateService;
import com.niu.core.service.api.recharge.IRechargeOrderService;
import com.niu.core.service.api.recharge.IRechargeService;
import com.niu.core.service.api.recharge.param.RechargeOrderSearchParam;
import com.niu.core.service.api.recharge.vo.RechargeOrderInfoVo;
import com.niu.core.service.api.recharge.vo.RechargeOrderListVo;
import com.niu.core.service.core.recharge.order.param.RechargeOrderCreateParam;
import com.niu.core.service.core.recharge.order.vo.RechargeOrderCreateVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/recharge")
public class RechargeController {

    @Resource
    IRechargeOrderCreateService rechargeOrderCreateService;

    @Resource
    IRechargeOrderService rechargeOrderService;

    @Resource
    IRechargeService rechargeService;

    @Resource
    IRechargeConfigService rechargeConfigService;

    /**
     * 充值订单创建
     *
     * @param param
     * @return
     */
    @PostMapping("/order_create/create")
    public Result<RechargeOrderCreateVo> create(@Validated @RequestBody RechargeOrderCreateParam param) {
        return Result.success(rechargeOrderCreateService.create(param));
    }

    /**
     * 订单列表
     *
     * @param
     * @return
     */
    @GetMapping("/order/list")
    public Result<PageResult<RechargeOrderListVo>> list(PageParam pageParam, RechargeOrderSearchParam searchParam) {
        return Result.success(rechargeOrderService.list(pageParam, searchParam));
    }

    /**
     * 订单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping("/order/detail/{order_id}")
    public Result<RechargeOrderInfoVo> detail(@PathVariable("order_id") Integer orderId) {
        return Result.success(rechargeOrderService.detail(orderId));
    }

    @GetMapping("/package")
    public Result<?> getRechargeLists() {
        return Result.success(rechargeService.list());
    }

    @GetMapping("/config")
    public Result<?> getRechargeConfig() {
        return Result.success(rechargeConfigService.getRechargeConfig());
    }

    @GetMapping("/order/status")
    public Result<Map<Integer, Map<String, Object>>> status() {
        return Result.success(RechargeOrderStatusEnum.getMap());
    }

}
