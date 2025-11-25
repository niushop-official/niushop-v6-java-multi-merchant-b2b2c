package com.niu.mall.controller.siteapi.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.mall.enums.order.OrderBatchDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderBatchDeliveryTypeEnum;
import com.niu.mall.service.site.order.IMallOrderBatchDeliveryService;
import com.niu.mall.service.site.order.param.MallOrderAddBatchDeliveryParam;
import com.niu.mall.service.site.order.param.MallOrderBatchDeliverySearchParam;
import com.niu.mall.service.site.order.vo.MallOrderBatchDeliveryInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/adminapi/mall/site/order_batch_delivery")
public class SiteOrderBatchDeliveryController {

    private final IMallOrderBatchDeliveryService iMallOrderBatchDeliveryService;

    @GetMapping
    public Result<?> list(@Validated PageParam pageParam,
                          @Validated MallOrderBatchDeliverySearchParam searchParam) {
        return Result.success(iMallOrderBatchDeliveryService.list(pageParam, searchParam));
    }

    @GetMapping("/{id}")
    public Result<MallOrderBatchDeliveryInfoVo> info(@Validated @PathVariable("id") Integer id) {
        return Result.success(iMallOrderBatchDeliveryService.info(id));
    }

    @PutMapping("/add_batch_order_delivery")
    public Result<?> addBatchOrderDelivery(@Validated @RequestBody MallOrderAddBatchDeliveryParam addParam) {
        iMallOrderBatchDeliveryService.addBatchOrderDelivery(addParam);
        return Result.success();
    }

    @GetMapping("/get_type")
    public Result<?> getBatchType() {
        Map<String, String> result = Arrays.stream(OrderBatchDeliveryTypeEnum.values())
                .collect(Collectors.toMap(OrderBatchDeliveryTypeEnum::getValue, OrderBatchDeliveryTypeEnum::getName));
        return Result.success(result);
    }

    @GetMapping("/get_status")
    public Result<?> getBatchStatus() {
        Map<Integer, String> result = Arrays.stream(OrderBatchDeliveryStatusEnum.values())
                .collect(Collectors.toMap(OrderBatchDeliveryStatusEnum::getValue, OrderBatchDeliveryStatusEnum::getName));
        return Result.success(result);
    }
}
