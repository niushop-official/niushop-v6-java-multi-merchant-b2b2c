package com.niu.core.controller.siteapi.localdelivery;

import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.site.goods.param.GoodsAttrParam;
import com.niu.core.service.site.local_delivery.ILocalDeliveryService;
import com.niu.core.service.site.local_delivery.ILocalDeliveryServiceService;
import com.niu.core.service.site.local_delivery.param.SetLocalDeliveryParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceListVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 同城配送服务控制器
 */
@RestController
@RequestMapping("adminapi/delivery/site/local")
public class LocalDeliveryController {

    @Resource
    ILocalDeliveryService localDeliveryService;

    /**
     * 获取同城配送设置
     */
    @GetMapping("")
    public Result getLocal() {
        return Result.success(localDeliveryService.getLocal(RequestUtils.siteId()));
    }

    /**
     * 设置同城配送
     */
    @PutMapping("")
    public Result setLocal(@Validated @RequestBody SetLocalDeliveryParam param) {
        localDeliveryService.setLocal(param);
        return Result.success(true);
    }

}
