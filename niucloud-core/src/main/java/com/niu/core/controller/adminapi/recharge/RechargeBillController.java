package com.niu.core.controller.adminapi.recharge;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.service.admin.recharge.IRechargeBillService;
import com.niu.core.service.admin.recharge.IRechargeOrderService;
import com.niu.core.service.admin.recharge.param.RechargeBillParam;
import com.niu.core.service.admin.recharge.param.RechargeOrderSearchParam;
import jakarta.annotation.Resource;
import jdk.jfr.Description;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("adminapi/recharge/bill")
@Description("账单统计")
public class RechargeBillController {

    @Resource
    IRechargeBillService rechargeBillService;

    @GetMapping("/total")
    @Description("账单总数统计")
    public Result total(@Validated RechargeBillParam searchParam) {
        return Result.success(rechargeBillService.getBillTotal(searchParam));
    }

    @GetMapping("/list")
    @Description("账单列表统计")
    public Result detail(@Validated PageParam pageParam,@Validated RechargeBillParam searchParam) {
        return Result.success(rechargeBillService.getBillList(pageParam, searchParam));
    }
}
