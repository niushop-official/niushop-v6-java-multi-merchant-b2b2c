package com.niu.core.controller.api.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.service.api.delivery.IDeliveryPackageService;
import com.niu.core.service.api.delivery.param.TradePackageSearchParam;
import com.niu.core.service.api.delivery.vo.DeliveryPackageInfoVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 配送方式控制器
 */
@RestController
@RequestMapping("api/delivery/")
public class DeliveryPackageController {

    @Resource
    IDeliveryPackageService deliveryPackageService;

    /**
     * 业务包裹
     */
    @GetMapping("/delivery_package/{trade_type}")
    public Result<DeliveryPackageInfoVo> getDeliveryPackageList(@PathVariable("trade_type") String tradeType, @Validated TradePackageSearchParam searchParam) {
        return Result.success(deliveryPackageService.getDeliveryPackage(searchParam, tradeType));
    }
}
