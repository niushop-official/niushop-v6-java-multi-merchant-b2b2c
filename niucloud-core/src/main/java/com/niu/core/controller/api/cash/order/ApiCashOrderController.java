package com.niu.core.controller.api.cash.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.cash.order.IApiCashOrderCreateService;
import com.niu.core.service.api.cash.order.IApiCashOrderService;
import com.niu.core.service.api.cash.order.param.ApiCashOrderCalculateParam;
import com.niu.core.service.api.cash.order.param.ApiCashOrderCreateParam;
import com.niu.core.service.api.cash.order.param.ApiCashOrderPageParam;
import com.niu.core.service.api.cash.order.vo.ApiCashOrderCreateVo;
import com.niu.core.service.api.cash.order.vo.ApiCashOrderDetailVo;
import com.niu.core.service.api.cash.order.vo.ApiCashOrderPageVo;
import com.niu.core.service.core.cash.order.vo.CashOrderCreateCacheVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * C端买单订单
 */
@SaCheckLogin
@RestController
@RequestMapping("api/cash/order")
public class ApiCashOrderController {

    @Resource
    IApiCashOrderService apiCashOrderService;

    @Resource
    IApiCashOrderCreateService apiCashOrderCreateService;


    @GetMapping("/lists")
    public Result<PageResult<ApiCashOrderPageVo>> lists(@Validated PageParam pageParam, ApiCashOrderPageParam param) {
        return Result.success(apiCashOrderService.getPage(pageParam, param));
    }

    @GetMapping("/detail/{order_id}")
    public Result<ApiCashOrderDetailVo> detail(@PathVariable("order_id") Integer orderId) {
        return Result.success(apiCashOrderService.getDetail(orderId));
    }

    @GetMapping("/status")
    public Result<Map<Integer, Map<String, Object>>> getStatus() {
        return Result.success(apiCashOrderService.getStatus());
    }

    @GetMapping("/calculate")
    public Result<CashOrderCreateCacheVo> calculate(ApiCashOrderCalculateParam param) {
        return Result.success(apiCashOrderCreateService.calculate(param));
    }

    @PostMapping("/create")
    public Result<ApiCashOrderCreateVo> create(@RequestBody ApiCashOrderCreateParam param) {
        return Result.success(apiCashOrderCreateService.create(param));
    }
}
