package com.niu.core.controller.adminapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.service.core.delivery.param.DeliverySearchConfigParam;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.core.service.admin.delivery.IDeliveryService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配送方式控制器
 */
@RestController
@RequestMapping("adminapi/delivery/admin/delivery")
public class DeliveryController {

    @Resource
    IDeliveryService deliveryService;


    /**
     * 获取物流查询接口配置
     * @return Result<PageResult<ShopDeliveryDeliverListVo>>
     */
    @GetMapping("/search")
    public Result<DeliverySearchConfigVo> getDeliverySearchConfig() {
        return Result.success(deliveryService.getDeliverySearchConfig());
    }

    /**
     * 设置物流查询接口配置
     * @return Result
     */
    @PostMapping("/search")
    public Result<Object> setDeliverySearchConfig(@Validated @RequestBody DeliverySearchConfigParam param) {
        deliveryService.setDeliverySearchConfig(param);
        return Result.success();
    }

    /**
     * 获取接口配置
     * @return Result<PageResult<ShopDeliveryDeliverListVo>>
     */
    @GetMapping("/config")
    public Result<Object> getAdminDeliveryConfig() {
        return Result.success(deliveryService.getAdminDeliveryConfig());
    }

    /**
     * 设置配置
     * @return Result
     */
    @PutMapping("/config")
    public Result<Object> setDeliverySearchConfig(@Validated @RequestBody AdminDeliveryConfigVo param) {
        deliveryService.setAdminDeliveryConfig(param);
        return Result.success();
    }

    /**
     * 获取配送方式
     * @return Result
     */
    @GetMapping("/type")
    public Result<Object> getDeliveryType() {
        return Result.success(DeliveryTypeEnum.getMap());
    }

}
