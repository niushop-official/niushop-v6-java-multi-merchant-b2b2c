package com.niu.core.controller.siteapi.localdelivery;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.local_delivery.LocalDeliveryOrderCancelReasonEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.service.site.local_delivery.ILocalDeliveryOrderService;
import com.niu.core.service.site.local_delivery.param.LocalDeliveryOrderCloseParam;
import com.niu.core.service.site.local_delivery.param.LocalDeliveryOrderSearchParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryOrderListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 同城配送服务控制器
 */
@RestController
@RequestMapping("adminapi/delivery/site/local_delivery/order")
public class LocalDeliveryOrderController {

    @Resource
    ILocalDeliveryOrderService localDeliveryOrderService;

    /**
     * 同城配送订单
     */
    @GetMapping("")
    public Result<PageResult<LocalDeliveryOrderListVo>> page(@Validated PageParam pageParam, @Validated LocalDeliveryOrderSearchParam searchParam) {
        PageResult<LocalDeliveryOrderListVo> list = localDeliveryOrderService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 同城配送订单详情
     */
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable("id") Integer id) {
        return Result.success(localDeliveryOrderService.getInfo(id));
    }

    /**
     * 同城配送订单同步
     */
    @PutMapping("/sync/{id}")
    public Result syncOrder(@PathVariable("id") Integer id) {
        localDeliveryOrderService.syncOrder(id);
        return Result.success(true);
    }

    /**
     * 同城配送订单取消
     */
    @PutMapping("/cancel/{id}")
    public Result syncOrder(@PathVariable("id") Integer id,@Validated @RequestBody LocalDeliveryOrderCloseParam  param) {
        localDeliveryOrderService.closeOrder(id,param);
        return Result.success(true);
    }

    /**
     * 同城配送订单完成
     */
    @PutMapping("/finish/{id}")
    public Result finishOrder(@PathVariable("id") Integer id) {
        localDeliveryOrderService.finishOrder(id);
        return Result.success(true);
    }

    /**
     * 同城配送订单状态列表
     */
    @GetMapping("/status")
    public Result status() {
        return Result.success(LocalDeliveryStatusEnum.getMap());
    }

    /**
     * 同城配送订单状态列表
     */
    @GetMapping("/cancel_reason")
    public Result cancelReason(@RequestParam("service") String service) {
        return Result.success(LocalDeliveryOrderCancelReasonEnum.getCancelByService(service));
    }

}
