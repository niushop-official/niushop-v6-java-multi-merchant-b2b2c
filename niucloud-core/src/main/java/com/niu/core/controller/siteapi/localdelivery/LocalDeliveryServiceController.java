package com.niu.core.controller.siteapi.localdelivery;

import com.niu.core.common.domain.Result;
import com.niu.core.service.site.local_delivery.ILocalDeliveryServiceService;
import com.niu.core.service.site.local_delivery.param.LocalDeliverySearchParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryConfigVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceListVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 同城配送服务控制器
 */
@RestController
@RequestMapping("adminapi/delivery/site/local_delivery")
public class LocalDeliveryServiceController {

    @Resource
    ILocalDeliveryServiceService localDeliveryServiceService;

    /**
     * 获取已启用配送服务商列表
     * @return Result<PageResult<ShopGoodsAttrListVo>>
     */
    @GetMapping("/list")
    public Result<List<LocalDeliveryServiceListVo>> list(@Validated LocalDeliverySearchParam searchParam) {
        return Result.success(localDeliveryServiceService.list(searchParam));
    }

    @GetMapping("/service")
    public Result<List<LocalDeliveryServiceVo>> lists() {
        List<LocalDeliveryServiceVo> res = localDeliveryServiceService.getList();
        return Result.success(res);
    }

    @GetMapping("/service/{delivery_type}")
    public Result<LocalDeliveryConfigVo> lists(@PathVariable("delivery_type") String deliveryType) {
        return Result.success(localDeliveryServiceService.getConfig(deliveryType));
    }

    @PutMapping("/service/{delivery_type}")
    public Result lists(@PathVariable("delivery_type") String deliveryType, HttpServletRequest request) {
        localDeliveryServiceService.setConfig(deliveryType,request);
        return Result.success(true);
    }

}
