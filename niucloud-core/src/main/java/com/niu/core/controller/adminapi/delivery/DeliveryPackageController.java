package com.niu.core.controller.adminapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.service.admin.delivery.IDeliveryPackageService;
import com.niu.core.service.admin.delivery.IDeliveryService;
import com.niu.core.service.admin.delivery.param.DeliveryCompanySearchParam;
import com.niu.core.service.admin.delivery.param.TradePackageInfoSearchParam;
import com.niu.core.service.admin.delivery.param.TradePackageSearchParam;
import com.niu.core.service.admin.delivery.vo.DeliveryPackageInfoVo;
import com.niu.core.service.admin.delivery.vo.DeliveryPackageListVo;
import com.niu.core.service.core.delivery.param.DeliverySearchConfigParam;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配送方式控制器
 */
@RestController
@RequestMapping("adminapi/delivery/admin/delivery_package")
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
