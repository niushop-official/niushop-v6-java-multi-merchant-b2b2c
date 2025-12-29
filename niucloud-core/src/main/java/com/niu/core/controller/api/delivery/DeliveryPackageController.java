package com.niu.core.controller.api.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.service.api.delivery.IDeliveryPackageService;
import com.niu.core.service.api.delivery.IOrderService;
import com.niu.core.service.api.delivery.param.OrderNotifyParam;
import com.niu.core.service.api.delivery.param.TradePackageSearchParam;
import com.niu.core.service.api.delivery.vo.DeliveryPackageInfoVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 配送方式控制器
 */
@RestController
@RequestMapping("api/delivery/")
public class DeliveryPackageController {

    @Resource
    IDeliveryPackageService deliveryPackageService;
    @Resource
    IOrderService orderService;

    /**
     * 业务包裹
     */
    @GetMapping("/delivery_package/{trade_type}")
    public Result<DeliveryPackageInfoVo> getDeliveryPackageList(@PathVariable("trade_type") String tradeType, @Validated TradePackageSearchParam searchParam) {
        return Result.success(deliveryPackageService.getDeliveryPackage(searchParam, tradeType));
    }

    /**
     * 第三方配送
     */
    @RequestMapping("/notify/{site_id}/{type}/{order_no}/{action}")
    public Result<Boolean> notify(@PathVariable("site_id") Integer siteId, @PathVariable("type") String type, @PathVariable("order_no") String orderNo, @PathVariable("action") String action, @RequestBody Map<String, String> allParams) {
        OrderNotifyParam data = new OrderNotifyParam();
        data.setSiteId(siteId);
        data.setType(type);
        data.setOrderNo(orderNo);
        data.setAction(action);
        orderService.notify(data, allParams);
        return Result.success(true);
    }
}
