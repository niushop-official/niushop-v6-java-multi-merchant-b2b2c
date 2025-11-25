package com.niu.core.controller.adminapi.localdelivery;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.service.admin.local_delivery.ILocalDeliveryOrderService;
import com.niu.core.service.admin.local_delivery.param.LocalDeliveryOrderSearchParam;
import com.niu.core.service.admin.local_delivery.vo.LocalDeliveryOrderListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 同城配送控制器
 */
@RestController
@RequestMapping("adminapi/delivery/admin/local_delivery/order")
public class LocalDeliveryOrderController {

    @Resource
    ILocalDeliveryOrderService localDeliveryOrderService;

    /**
     * 查询本地配送订单列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<LocalDeliveryOrderListVo>>
     */
    @GetMapping("")
    public Result<PageResult<LocalDeliveryOrderListVo>> page(@Validated PageParam pageParam, @Validated LocalDeliveryOrderSearchParam searchParam) {
        PageResult<LocalDeliveryOrderListVo> list = localDeliveryOrderService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 查询本地配送订单详情
     * @param id 主键ID
     * @return Result<LocalDeliveryOrderInfoVo>
     */
    @GetMapping("/{id}")
    public Result info(@Validated @PathVariable("id") Integer id) {
        return Result.success(localDeliveryOrderService.info(id));
    }

    /**
     * 同城配送订单状态列表
     */
    @GetMapping("/status")
    public Result status() {
        return Result.success(LocalDeliveryStatusEnum.getMap());
    }
}
