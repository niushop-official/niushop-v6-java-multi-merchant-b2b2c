package com.niu.core.controller.api.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.service.api.delivery.IConfigService;
import com.niu.core.service.api.delivery.IOrderService;
import com.niu.core.service.api.delivery.param.OrderServiceParam;
import com.niu.core.service.api.delivery.param.TrackOfLocalServiceParam;
import com.niu.core.service.api.delivery.vo.TrackOfLocalServiceVo;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 配送方式控制器
 */
@RestController
@RequestMapping("api/basic/delivery")
public class DeliveryController {

    @Resource
    IConfigService configService;
    @Resource
    IOrderService orderService;

    /**
     * 获取接口配置
     */
    @GetMapping("/config")
    public Result<Object> getAdminDeliveryConfig() {
        return Result.success(configService.getAdminDeliveryConfig());
    }

    /**
     * 获取配送方式
     * @return Result
     */
    @GetMapping("/delivery_type")
    public Result<Object> getDeliveryType() {
        return Result.success(DeliveryTypeEnum.getMap());
    }

    /**
     * 获取配送方式
     * @return Result
     */
    @GetMapping("/local")
    public Result<Object> getLocal(@Validated @RequestParam("site_id") Integer id) {
        return Result.success(configService.getLocal(id));
    }

    /**
     * 获取配送轨迹
     * @return Result
     */
    @GetMapping("/track")
    public Result<Object> getDeliveryTrack(@Validated OrderServiceParam param) {
        return Result.success(orderService.getDeliveryTrack(param));
    }

    /**
     * 获取同城配送轨迹
     * @return Result
     */
    @GetMapping("/track_of_local")
    public Result<Object> getDeliveryTrackOfLocal(@Validated TrackOfLocalServiceParam param) {
        return Result.success(orderService.getTrackOfLocalDeliveryOrder(param));
    }

}
