package com.niu.core.controller.api.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.service.api.delivery.IConfigService;
import com.niu.core.service.api.delivery.IOrderService;
import com.niu.core.service.api.delivery.param.OrderServiceParam;
import com.niu.core.service.api.delivery.param.TrackOfLocalServiceParam;
import com.niu.core.service.api.delivery.vo.RiderLocationVo;
import com.niu.core.service.api.delivery.vo.TrackOfLocalServiceVo;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 配送相关接口
 * 对应 PHP: basic 路由下 delivery.Config / delivery.Order
 */
@RestController
@RequestMapping("api/basic/delivery")
public class DeliveryController {

    @Resource
    IConfigService configService;
    @Resource
    IOrderService orderService;

    /**
     * 配送通用配置（对齐 PHP Config@getConfig）
     */
    @GetMapping("/config")
    public Result<Object> getAdminDeliveryConfig() {
        return Result.success(configService.getAdminDeliveryConfig());
    }

    /**
     * 配送类型（对齐 PHP Config@getDeliveryType）
     */
    @GetMapping("/delivery_type")
    public Result<Object> getDeliveryType() {
        return Result.success(DeliveryTypeEnum.getMap());
    }

    /**
     * 同城配送配置（对齐 PHP Config@getLocalDeliveryConfig）
     */
    @GetMapping("/local")
    public Result<Object> getLocal(@Validated @RequestParam("site_id") Integer id) {
        return Result.success(configService.getLocal(id));
    }

    /**
     * 获取配送轨迹（扩展接口）
     */
    @GetMapping("/track")
    public Result<Object> getDeliveryTrack(@Validated OrderServiceParam param) {
        return Result.success(orderService.getDeliveryTrack(param));
    }

    /**
     * 同城配送轨迹（对齐 PHP Order@getTrackOfLocalDeliveryOrder）
     */
    @GetMapping("/track_of_local")
    public Result<TrackOfLocalServiceVo> getDeliveryTrackOfLocal(@Validated TrackOfLocalServiceParam param) {
        return Result.success(orderService.getTrackOfLocalDeliveryOrder(param));
    }

    /**
     * 骑手位置（对齐 PHP Order@getRiderLocation）
     */
    @GetMapping("/rider_location")
    public Result<RiderLocationVo> getRiderLocation(@Validated TrackOfLocalServiceParam param) {
        return Result.success(orderService.getRiderLocation(param));
    }

}
