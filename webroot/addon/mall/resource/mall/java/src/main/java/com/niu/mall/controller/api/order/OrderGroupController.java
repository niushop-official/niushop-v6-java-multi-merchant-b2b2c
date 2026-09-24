package com.niu.mall.controller.api.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.enums.order.OrderCloseTypeEnum;
import com.niu.mall.enums.order.OrderLogMainTypeEnum;
import com.niu.mall.service.api.order.IOrderGroupService;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/mall/order_group")
@SaCheckLogin
public class OrderGroupController {

    @Resource
    IOrderGroupService orderGroupService;


    /**
     * 获取订单组详情
     *
     * @param orderId
     * @return
     */
    @GetMapping("/get_group_by_order/{order_id}")
    public Result getGroupByOrderId(@PathVariable(name = "order_id") Integer orderId) {
        return Result.success(orderGroupService.getGroupByOrderId(orderId));
    }

    /**
     * 关闭订单
     *
     * @param id
     * @return
     */
    @PutMapping("/close/{id}")
    public Result<?> orderClose(@PathVariable("id") Integer id, @RequestBody OrderCloseParam closeParam) {
        closeParam.setCloseType(OrderCloseTypeEnum.SHOP_CLOSE.getType());
        closeParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        closeParam.setMainId(RequestUtils.memberId());
        orderGroupService.close(id, closeParam);
        return Result.success();
    }
}
