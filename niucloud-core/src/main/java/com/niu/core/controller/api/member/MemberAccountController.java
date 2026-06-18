package com.niu.core.controller.api.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.object.ObjectUtils;
import com.niu.core.service.api.member.IMemberAccountService;
import com.niu.core.service.api.member.param.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 会员积分流水
 */
@RestController
@RequestMapping("/api/member/account")
@Slf4j
@SaCheckLogin
public class MemberAccountController {

    @Autowired
    private IMemberAccountService memberAccountService;

    /**
     * 积分流水
     *
     * @param pageParam
     * @param param
     * @return
     */
    @GetMapping("/point")
    public Result<?> point(@Validated PageParam pageParam, @Validated AccountPointFlowParam param) {
        // 参考mall模块写法：直接使用Param对象接收参数，Spring Boot会自动将create_time[0]和create_time[1]绑定到createTime数组
        return Result.success(memberAccountService.pointFlow(pageParam, param));
    }

    /**
     * 余额流水
     *
     * @param param
     * @return
     */
    @GetMapping("/balance")
    public Result<?> balance(@Validated PageParam pageParam, @Validated AccountBalanceFlowParam param, @RequestParam(name = "create_time[0]", defaultValue = "") String startTime, @RequestParam(name = "create_time[1]", defaultValue = "") String endTime) {
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            param.setCreateTime(new String[]{startTime, endTime});
        }
        return Result.success(memberAccountService.balanceFlow(pageParam, param));
    }

    /**
     * 余额流水(新)
     *
     * @param param
     * @return
     */
    @GetMapping("/balance_list")
    public Result<?> balanceList(@Validated PageParam pageParam, @Validated AccountBalanceListParam param, @RequestParam(name = "create_time[0]", defaultValue = "") String startTime, @RequestParam(name = "create_time[1]", defaultValue = "") String endTime) {
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            param.setCreateTime(new String[]{startTime, endTime});
        }
        return Result.success(memberAccountService.balanceList(pageParam, param));
    }

    /**
     * 零钱流水
     *
     * @param param
     * @return
     */
    @GetMapping("/money")
    public Result<?> money(@Validated PageParam pageParam, @Validated AccountMoneyFlowParam param, @RequestParam(name = "create_time[0]", defaultValue = "") String startTime, @RequestParam(name = "create_time[1]", defaultValue = "") String endTime) {

        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            param.setCreateTime(new String[]{startTime, endTime});
        }
        return Result.success(memberAccountService.moneyFlow(pageParam, param));
    }

    /**
     * 账户记录数量
     *
     * @param param
     * @return
     */
    @GetMapping("/count")
    public Result<?> count(@Validated AccountRecordsParam param) {
        return Result.success(memberAccountService.accountRecords(param));
    }

    /**
     * 佣金流水
     *
     * @param param
     * @return
     */
    @GetMapping("/commission")
    public Result<?> commission(@Validated PageParam pageParam, @Validated AccountCommissionFlowParam param, @RequestParam(name = "create_time[0]", defaultValue = "") String startTime, @RequestParam(name = "create_time[1]", defaultValue = "") String endTime) {

        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            param.setCreateTime(new String[]{startTime, endTime});
        }
        return Result.success(memberAccountService.commissionFlow(pageParam, param));
    }

    /**
     * 账户来源
     *
     * @param accountType
     * @return
     */
    @GetMapping("/fromtype/{accountType}")
    public Result<?> getFromType(@PathVariable("accountType") String accountType) {
        AccountAccountSourceParam param = ObjectUtils.create(AccountAccountSourceParam.class);
        param.setAccountType(accountType);
        return Result.success(memberAccountService.getAccountSource(param));
    }

    /**
     * 积分数量
     *
     * @return
     */
    @GetMapping("/pointcount")
    public Result<?> pointCount() {
        AccountPointCountParam param = ObjectUtils.create(AccountPointCountParam.class);
        return Result.success(memberAccountService.pointCount(param));
    }

}
