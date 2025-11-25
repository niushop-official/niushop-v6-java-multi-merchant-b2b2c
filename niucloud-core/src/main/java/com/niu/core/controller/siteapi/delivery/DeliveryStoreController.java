package com.niu.core.controller.siteapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.service.site.delivery.IDeliveryStoreService;
import com.niu.core.service.core.delivery_store.param.DeliveryStoreShopEditParam;
import com.niu.core.service.core.delivery_store.param.DeliveryShopOpenParam;
import com.niu.core.service.core.delivery_store.vo.DeliveryStoreListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配送门店控制器
 */
@RestController
@RequestMapping("adminapi/delivery/site/delivery_store")
public class DeliveryStoreController {
    
    @Resource
    private IDeliveryStoreService deliveryStoreService;

    /**
     * 获取配送服务商列表
     * @param id 地址ID
     * @return 配送服务商列表
     */
    @GetMapping("/delivery_service_list/{id}")
    public Result<List<DeliveryStoreListVo>> deliveryServiceList(@PathVariable("id") Integer id) {
        return Result.success(deliveryStoreService.deliveryServiceList(id));
    }

    /**
     * 服务商开通门店
     * @param id 地址ID
     * @param param 开通参数
     * @return 操作结果
     */
    @PutMapping("/delivery_shop_open/{id}")
    public Result<String> deliveryShopOpen(@PathVariable("id") Integer id, @RequestBody DeliveryShopOpenParam param) {
        deliveryStoreService.deliveryShopOpen(id, param);
        return Result.success("SUCCESS");
    }

    /**
     * 服务商门店品类修改
     * @param id 地址ID
     * @param param 修改参数
     * @return 操作结果
     */
    @PutMapping("/delivery_shop_edit/{id}")
    public Result<String> deliveryShopEdit(@PathVariable("id") Integer id, @RequestBody DeliveryStoreShopEditParam param) {
        deliveryStoreService.deliveryShopEdit(id, param);
        return Result.success("SUCCESS");
    }
}
