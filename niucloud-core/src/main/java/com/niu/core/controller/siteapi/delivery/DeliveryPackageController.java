package com.niu.core.controller.siteapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.service.site.delivery.IDeliveryPackageService;
import com.niu.core.service.site.delivery.param.TradePackageInfoSearchParam;
import com.niu.core.service.site.delivery.param.TradePackageSearchParam;
import com.niu.core.service.site.delivery.vo.DeliveryPackageInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryPackageListVo;
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
@RequestMapping("adminapi/delivery/site/delivery_package")
public class DeliveryPackageController {

    @Resource
    IDeliveryPackageService deliveryPackageService;

    /**
     * 获取订单发货包裹列表
     */
    @GetMapping("/trade_package_list/{trade_type}")
    public Result<List<DeliveryPackageListVo>> getDeliveryPackageList(@PathVariable("trade_type") String tradeType, @Validated TradePackageInfoSearchParam searchParam) {
        return Result.success(deliveryPackageService.getDeliveryPackageList(searchParam.getTradeNo(), tradeType));
    }

    /**
     * 业务包裹
     */
    @GetMapping("/trade_package/{trade_type}")
    public Result<DeliveryPackageInfoVo> getDeliveryPackageList(@PathVariable("trade_type") String tradeType, @Validated TradePackageSearchParam searchParam) {
        return Result.success(deliveryPackageService.getDeliveryPackage(searchParam, tradeType));
    }
}
