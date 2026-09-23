package com.niu.mall.controller.adminapi.finance;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.finance.SettlementOrderService;
import com.niu.mall.service.admin.finance.param.SettlementSelectParam;
import com.niu.mall.service.admin.finance.vo.SettlementDataVo;
import com.niu.mall.service.admin.finance.vo.SettlementOrderListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 结算订单控制器
 */

@RestController
@RequestMapping("adminapi/mall/admin/order/settlement")
public class SettlementController {

    @Resource
    SettlementOrderService settlementOrderService;

    /**
     * 结算记录列表
     */
    @GetMapping("")
    public Result<PageResult<SettlementOrderListVo>> getPage(@Validated PageParam pageParam,
                                                     @Validated SettlementSelectParam searchParam) {
        PageResult<SettlementOrderListVo> list = settlementOrderService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 结算记录统计
     */
    @GetMapping("/info")
    public Result<SettlementDataVo> getSettlementData() { return Result.success(settlementOrderService.getSettlementData()); }
}
