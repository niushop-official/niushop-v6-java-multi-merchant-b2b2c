package com.niu.core.controller.siteapi.shopdelivery;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.local_delivery.LocalDeliveryOrderCancelReasonEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.shop_delivery.ShopDeliveryStatusEnum;
import com.niu.core.service.site.local_delivery.ILocalDeliveryOrderService;
import com.niu.core.service.site.local_delivery.param.LocalDeliveryOrderCloseParam;
import com.niu.core.service.site.local_delivery.param.LocalDeliveryOrderSearchParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryOrderListVo;
import com.niu.core.service.site.shop_delivery.IShopDeliveryOrderService;
import com.niu.core.service.site.shop_delivery.param.ShopDeliveryOrderCloseParam;
import com.niu.core.service.site.shop_delivery.param.ShopDeliveryOrderSearchParam;
import com.niu.core.service.site.shop_delivery.vo.ShopDeliveryOrderListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商家配送订单控制器
 */
@RestController
@RequestMapping("adminapi/delivery/site/shop_delivery/order")
public class ShopDeliveryOrderController {

    @Resource
    IShopDeliveryOrderService shopDeliveryOrderService;

    /**
     * 商家配送订单分页列表
     */
    @GetMapping("")
    public Result<PageResult<ShopDeliveryOrderListVo>> page(@Validated PageParam pageParam, @Validated ShopDeliveryOrderSearchParam searchParam) {
        PageResult<ShopDeliveryOrderListVo> list = shopDeliveryOrderService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商家配送订单详情
     */
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable("id") Integer id) {
        return Result.success(shopDeliveryOrderService.getInfo(id));
    }

    /**
     * 同城配送订单取消
     */
    @PutMapping("/cancel/{id}")
    public Result syncOrder(@PathVariable("id") Integer id,@Validated @RequestBody ShopDeliveryOrderCloseParam param) {
        shopDeliveryOrderService.closeOrder(id,param);
        return Result.success(true);
    }

    /**
     * 同城配送订单完成
     */
    @PutMapping("/finish/{id}")
    public Result finishOrder(@PathVariable("id") Integer id) {
        shopDeliveryOrderService.finishOrder(id);
        return Result.success(true);
    }

    /**
     * 同城配送订单状态列表
     */
    @GetMapping("/status")
    public Result status() {
        return Result.success(ShopDeliveryStatusEnum.getMap());
    }

}
