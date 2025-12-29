package com.niu.mall.controller.siteapi.refund;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.mall.enums.order.OrderRefundStatusEnum;
import com.niu.mall.service.site.refund.IRefundActionService;
import com.niu.mall.service.core.refund.param.ActiveRefundParam;
import com.niu.mall.service.site.refund.param.AuditApplyParam;
import com.niu.mall.service.site.refund.param.AuditRefundGoodsParam;
import com.niu.mall.service.site.refund.param.RefundSearchParam;
import com.niu.core.service.site.site.ISiteAddressService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("adminapi/mall/site/order/refund")
public class RefundController {

    @Resource
    IRefundActionService refundActionService;

    @Resource
    ISiteAddressService shopAddressService;

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

    /**
     * 订单维权审核
     *
     * @param orderRefundNo
     * @return
     */
    @PutMapping("/audit/{order_refund_no}")
    public Result auditApply(@PathVariable(name = "order_refund_no") String orderRefundNo, @Validated @RequestBody AuditApplyParam param) {
        param.setOrderRefundNo(orderRefundNo);
        refundActionService.auditApply(param);
        return Result.success();
    }

    /**
     * 订单退货审核
     *
     * @param orderRefundNo
     * @return
     */
    @PutMapping("/delivery/{order_refund_no}")
    public Result auditRefundGoods(@PathVariable(name = "order_refund_no") String orderRefundNo, @Validated @RequestBody AuditRefundGoodsParam param) {
        param.setOrderRefundNo(orderRefundNo);
        refundActionService.auditRefundGoods(param);
        return Result.success();
    }

    /**
     * 退款地址列表
     *
     * @return
     */
    @GetMapping("/address")
    public Result refundAddress() {
        return Result.success(shopAddressService.refundAddress());
    }

    /**
     * 商家主动退款
     * @param param
     * @return
     */
    @PostMapping("/active")
    public Result active(@RequestBody ActiveRefundParam param) {
        refundActionService.shopActiveRefund(param);
        return Result.success();
    }

    /**
     * 退款地址列表
     *
     * @return
     */
    @GetMapping("/status")
    public Result status() {
        return Result.success(OrderRefundStatusEnum.getRefundShowStatus());
    }
}
