package com.niu.core.controller.adminapi.recharge;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.recharge.IRechargeConfigService;
import com.niu.core.service.admin.recharge.IRechargeService;
import com.niu.core.service.admin.recharge.param.RechargeEditSortParam;
import com.niu.core.service.admin.recharge.param.RechargeModifyStatusParam;
import com.niu.core.service.admin.recharge.param.RechargePageParam;
import com.niu.core.service.admin.recharge.param.RechargeParam;
import com.niu.core.service.admin.shop.ISitePaymentService;
import com.niu.core.service.core.recharge.config.param.RechargeSetConfigParam;
import jakarta.annotation.Resource;
import jdk.jfr.Description;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("adminapi/recharge")
@Description("充值套餐管理")
public class RechargeController {
    @Resource
    private IRechargeService rechargeService;
    @Resource
    private IRechargeConfigService rechargeConfigService;

    @PostMapping("/config")
    @Description("设置充值配置")
    Result<?> setRechargeConfig(@RequestBody RechargeSetConfigParam param) {
        rechargeConfigService.setRechargeConfig(param);
        return Result.success();
    }

    @GetMapping("/config")
    @Description("获取充值配置")
    Result<?> getRechargeConfig() {
        return Result.success(rechargeConfigService.getRechargeConfig());
    }

    @GetMapping("/package")
    @Description("充值套餐列表")
    Result<?> lists(@Validated PageParam pageParam,@Validated RechargePageParam param) {
        return Result.success(rechargeService.getPage(pageParam, param));
    }

    @PostMapping("/package")
    @Description("添加充值套餐")
    Result<?> add(@RequestBody RechargeParam param) {
        rechargeService.add(param);
        return Result.success();
    }

    @PutMapping("/package/{id}")
    @Description("编辑充值套餐")
    Result<?> edit(@PathVariable("id") Integer id, @RequestBody RechargeParam param) {
        rechargeService.edit(id, param);
        return Result.success();
    }

    @GetMapping("/package/init")
    @Description("获取充值套餐初始信息")
    Result<?> init(@RequestParam("package_id") Integer packageId) {
        return Result.success(rechargeService.getInit(packageId));
    }

    @DeleteMapping("/package/{id}")
    @Description("删除充值套餐")
    Result<?> del(@PathVariable("id") Integer id) {
        rechargeService.del(id);
        return Result.success();
    }

    @PutMapping("/package/status")
    @Description("修改充值套餐状态")
    Result<?> modifyStatus(@RequestBody RechargeModifyStatusParam param) {
        rechargeService.modifyStatus(param);
        return Result.success();
    }

    @PutMapping("/package/sort")
    @Description("编辑充值套餐排序")
    Result<?> editSort(@RequestBody RechargeEditSortParam param) {
        rechargeService.editSort(param);
        return Result.success();
    }

    @GetMapping("dict/package_gift")
    @Description("获取充值套餐礼品字典")
    Result<?> getPackageGiftDict() {
        return Result.success(rechargeService.getPackageGiftDict());
    }

}
