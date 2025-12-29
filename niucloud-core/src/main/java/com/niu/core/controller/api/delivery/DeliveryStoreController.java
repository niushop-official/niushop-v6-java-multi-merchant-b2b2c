package com.niu.core.controller.api.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.service.api.delivery.IDeliveryStoreService;
import com.niu.core.service.api.delivery.param.DeliveryStoreParam;
import com.niu.core.service.api.delivery.vo.DeliveryStoreListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 配送方式控制器
 */
@RestController
@RequestMapping("api/delivery/")
public class DeliveryStoreController {

    @Resource
    IDeliveryStoreService deliveryStoreService;

    /**
     * 配送门店列表
     */
    @GetMapping("/delivery_store/list")
    public Result<List<DeliveryStoreListVo>> getDeliveryPackageList(@Validated DeliveryStoreParam searchParam) {
        return Result.success(deliveryStoreService.getLists(searchParam));
    }
}
