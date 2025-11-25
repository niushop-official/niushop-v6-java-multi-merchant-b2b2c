package com.niu.mall.controller.adminapi.order;

import com.niu.core.common.domain.Result;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.service.admin.order.IMallOrderDeliveryService;
import com.niu.mall.service.admin.order.vo.MallOrderDeliveryInfoVo;
import com.niu.mall.service.admin.order.vo.MallOrderDeliveryListVo;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 订单配送相关控制器
 */

@RestController
@RequestMapping("adminapi/mall/admin/order")
public class OrderDeliveryController {

    @Resource
    IMallOrderDeliveryService mallOrderDeliveryService;

    @PutMapping("/delivery")
    public Result<String> orderDelivery(@Validated @RequestBody OrderDeliveryParam orderDeliveryParam) {
        mallOrderDeliveryService.delivery(orderDeliveryParam);
        return Result.success("发货成功");
    }

    @GetMapping("/delivery_type")
    public Result<Map<String, String>> getDeliveryType(@RequestParam(name = "delivery_type", defaultValue = "") String deliveryType){
        return Result.success(OrderDeliveryTypeEnum.getMapByType(deliveryType));
    }

    @GetMapping("/delivery/package")
    public Result<MallOrderDeliveryInfoVo> getOrderPackage(@RequestParam(name = "id") Integer id, @RequestParam(name = "mobile") String mobile) {
        return Result.success(mallOrderDeliveryService.getDeliveryPackage(id, mobile));
    }

    @GetMapping("'delivery/package/list")
    public Result<List<MallOrderDeliveryListVo>> getDeliveryPackageList(@RequestParam(name = "order_id")  Integer orderId) {

        return Result.success(mallOrderDeliveryService.getDeliveryPackageList(orderId));
    }




}