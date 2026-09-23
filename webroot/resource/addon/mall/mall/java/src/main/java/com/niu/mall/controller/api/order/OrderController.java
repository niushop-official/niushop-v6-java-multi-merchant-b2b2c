package com.niu.mall.controller.api.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.service.api.order.IOrderService;
import com.niu.mall.service.api.order.param.DeliveryPackageSearchParam;
import com.niu.mall.service.api.order.param.MallOrderSearchParam;
import com.niu.mall.service.core.order.ICoreOrderConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.Map;

@RestController
@RequestMapping("api/mall/order")
@SaCheckLogin
public class OrderController {

    @Resource
    IOrderService orderService;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    /**
     * 查询订单列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return
     */
    @GetMapping("")
    public Result<?> page(@Validated PageParam pageParam, @Validated MallOrderSearchParam searchParam) {
        return Result.success(orderService.page(pageParam, searchParam));
    }

    /**
     * 获取订单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping("/{order_id}")
    public Result detail(@PathVariable(name = "order_id") Integer orderId,@RequestParam Map<String, String> allParams) {
        return Result.success(orderService.detail(orderId, allParams));
    }

    /**
     * 订单状态
     *
     * @return
     */
    @GetMapping("/status")
    public Result status() {
        return Result.success(OrderStatusEnum.getMap());
    }

    /**
     * 订单数量
     *
     * @return
     */
    @GetMapping("/num")
    public Result<?> getNum()  {
        return Result.success(orderService.getNum());
    }

    /**
     * 关闭订单
     *
     * @param id
     * @return
     */
    @PutMapping("/close/{id}")
    public Result<?> orderClose(@PathVariable("id") Integer id) {
        return Result.success(orderService.close(id));
    }

    /**
     * 订单完成
     *
     * @param id
     * @return
     */
    @PutMapping("/finish/{id}")
    public Result orderFinish(@PathVariable("id") Integer id) {
        orderService.orderFinish(id);
        return Result.success();
    }

    /**
     * 物流跟踪
     *
     * @return
     */
    @GetMapping("/logistics")
    public Result getPackage(DeliveryPackageSearchParam searchParam) {
        return Result.success(orderService.getPackage(searchParam));
    }
}
