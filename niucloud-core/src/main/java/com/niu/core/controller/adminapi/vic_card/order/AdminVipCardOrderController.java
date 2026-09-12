package com.niu.core.controller.adminapi.vic_card.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.vip_card.order.IAdminVipCardOrderService;
import com.niu.core.service.admin.vip_card.order.param.AdminVipCardOrderPageParam;
import com.niu.core.service.admin.vip_card.order.vo.AdminVipCardOrderDetailVo;
import com.niu.core.service.admin.vip_card.order.vo.AdminVipCardOrderPageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("adminapi/vip_card/order")
@Api(tags = "购卡订单管理")
public class AdminVipCardOrderController {

    @Autowired
    private IAdminVipCardOrderService orderService;

    @GetMapping("/pages")
    @ApiOperation("购卡订单分页列表")
    public Result<PageResult<AdminVipCardOrderPageVo>> pages(PageParam pageParam, AdminVipCardOrderPageParam param) {
        return Result.success(orderService.getPage(pageParam, param));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("购卡订单详情")
    public Result<AdminVipCardOrderDetailVo> info(@PathVariable Integer id) {
        return Result.success(orderService.getInfo(id));
    }
}