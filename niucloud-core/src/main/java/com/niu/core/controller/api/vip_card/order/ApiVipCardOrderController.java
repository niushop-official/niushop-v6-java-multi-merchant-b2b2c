package com.niu.core.controller.api.vip_card.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.ParamHelper;
import com.niu.core.service.api.vip_card.order.IApiVipCardOrderCreateService;
import com.niu.core.service.api.vip_card.order.IApiVipCardOrderService;
import com.niu.core.service.api.vip_card.order.param.ApiVipCardOrderPageParam;
import com.niu.core.service.api.vip_card.order.vo.ApiVipCardOrderCreateVo;
import com.niu.core.service.api.vip_card.order.vo.ApiVipCardOrderDetailVo;
import com.niu.core.service.api.vip_card.order.vo.ApiVipCardOrderPageVo;
import com.niu.core.service.core.vip_card.order.param.CoreVipCardOrderCreateParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("api/vip_card/order")
@Api(tags = "付费会员卡订单API")
public class ApiVipCardOrderController {

    @Autowired
    private IApiVipCardOrderService orderService;

    @Autowired
    private IApiVipCardOrderCreateService orderCreateService;

    @GetMapping("/lists")
    @ApiOperation("订单列表")
    public Result<PageResult<ApiVipCardOrderPageVo>> lists(PageParam pageParam, @RequestParam Map<String, String> allParams) {
        ApiVipCardOrderPageParam param = ParamHelper.mapToBean(allParams, ApiVipCardOrderPageParam.class);
        return Result.success(orderService.getPage(pageParam, param));
    }

    @GetMapping("/detail/{orderId}")
    @ApiOperation("订单详情")
    public Result<ApiVipCardOrderDetailVo> detail(@PathVariable Integer orderId) {
        return Result.success(orderService.getDetail(orderId));
    }

    @GetMapping("/status")
    @ApiOperation("获取订单状态列表")
    public Result<Map<Integer, Map<String, Object>>> getStatus() {
        return Result.success(orderService.getStatus());
    }

    @PostMapping("/create")
    @ApiOperation("创建订单")
    public Result<ApiVipCardOrderCreateVo> create(@Valid @RequestBody CoreVipCardOrderCreateParam param) {
        return Result.success(orderCreateService.create(param));
    }
}