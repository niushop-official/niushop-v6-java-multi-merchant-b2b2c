package com.niu.core.controller.siteapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.service.site.delivery.IDeliveryService;
import com.niu.core.service.site.delivery.param.DeliveryConfigParam;
import com.niu.core.service.core.delivery.param.DeliverySearchConfigParam;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 配送方式控制器
 */
@RestController
@RequestMapping("adminapi/delivery/site/delivery")
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
     * 配送页信息
     * @description 获取配送页信息配置
     * @return \think\Response
     */
    @PostMapping("/search")
    public Result<Object> setDeliverySearchConfig(@Validated @RequestBody DeliverySearchConfigParam param) {
        deliveryService.setDeliverySearchConfig(param);
        return Result.success();
    }

    @GetMapping("/deliveryList")

    public Result<?> deliveryList(){
    return Result.success( deliveryService.getDeliveryList());
    }
    /**
     * 获取配送方式
     * @return \think\Response
     */

    @GetMapping("/type")
    public Result<Map<String, String>> getDeliveryList() {
        return Result.success(deliveryService.getDeliveryType());
    }


    /**
     * 设置配送配置
     * @param param 配送配置参数
     * @return Result<Object>
     */
    @PutMapping("/setConfig")
    public Result<Object> setDeliveryConfig(@Validated @RequestBody DeliveryConfigParam param) {
        deliveryService.setConfig(param);
        return Result.success();
    }
}
