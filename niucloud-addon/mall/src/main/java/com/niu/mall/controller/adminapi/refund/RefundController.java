package com.niu.mall.controller.adminapi.refund;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.refund.IRefundActionService;
import com.niu.mall.service.core.refund.param.ActiveRefundParam;
import com.niu.mall.service.admin.refund.param.AuditApplyParam;
import com.niu.mall.service.admin.refund.param.AuditRefundGoodsParam;
import com.niu.mall.service.admin.refund.param.RefundSearchParam;
import com.niu.core.service.admin.delivery.ISiteAddressService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("adminapi/mall/admin/order/refund")
public class RefundController {

    @Resource
    IRefundActionService refundActionService;


    /**
     * 订单维权列表
     *
     * @return
     */
    @GetMapping("")
    public Result page(@Validated PageParam pageParam, @Validated RefundSearchParam searchParam) {
        return Result.success(refundActionService.page(pageParam, searchParam));
    }

    /**
     * 订单维权详情
     *
     * @return
     */
    @GetMapping("/{id}")
    public Result page(@PathVariable("id") Integer id) {
        return Result.success(refundActionService.detail(id));
    }

}
