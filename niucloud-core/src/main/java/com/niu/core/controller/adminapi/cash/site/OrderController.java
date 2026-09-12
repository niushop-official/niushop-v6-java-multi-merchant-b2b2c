package com.niu.core.controller.adminapi.cash.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.cash.ICashOrderService;
import com.niu.core.service.admin.cash.param.CashOrderSearchParam;
import com.niu.core.service.admin.cash.vo.CashOrderDetailVo;
import com.niu.core.service.admin.cash.vo.CashOrderListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 站点端买单订单
 */
@RestController
@RequestMapping("adminapi/cash/site/order")
@SaCheckLogin
public class OrderController {

    @Resource
    ICashOrderService cashOrderService;

    @GetMapping("/list")
    public Result<PageResult<CashOrderListVo>> lists(@Validated PageParam pageParam, CashOrderSearchParam searchParam) {
        return Result.success(cashOrderService.getPage(pageParam, searchParam, RequestUtils.siteId()));
    }

    @GetMapping("/detail/{order_id}")
    public Result<CashOrderDetailVo> detail(@PathVariable("order_id") Integer orderId) {
        return Result.success(cashOrderService.getDetail(orderId, RequestUtils.siteId()));
    }

    @GetMapping("/status")
    public Result<Map<Integer, Map<String, Object>>> status() {
        return Result.success(cashOrderService.getStatus());
    }
}
