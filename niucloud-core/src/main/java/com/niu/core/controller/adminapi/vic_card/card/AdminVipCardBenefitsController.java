package com.niu.core.controller.adminapi.vic_card.card;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.vip_card.card.IAdminVipCardBenefitsService;
import com.niu.core.service.admin.vip_card.card.param.AdminBenefitsListParam;
import com.niu.core.service.admin.vip_card.card.param.AdminModifySortParam;
import com.niu.core.service.admin.vip_card.card.param.AdminModifyStatusParam;
import com.niu.core.service.admin.vip_card.card.param.AdminSetConfigParam;
import com.niu.core.service.admin.vip_card.card.vo.AdminBenefitsConfigVo;
import com.niu.core.service.admin.vip_card.card.vo.AdminBenefitsListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("adminapi/vip_card/benefits")
@Api(tags = "会员卡权益管理")
public class AdminVipCardBenefitsController {

    @Autowired
    private IAdminVipCardBenefitsService benefitsService;

    @GetMapping("/pages")
    @ApiOperation("会员卡权益分页列表")
    public Result<PageResult<AdminBenefitsListVo>> pages(PageParam pageParam, AdminBenefitsListParam param) {
        return Result.success(benefitsService.getPage(pageParam, param));
    }

    @GetMapping("/list")
    @ApiOperation("会员卡权益列表")
    public Result<List<AdminBenefitsListVo>> list(AdminBenefitsListParam param) {
        return Result.success(benefitsService.getList(param));
    }

    @GetMapping("/dict")
    @ApiOperation("获取会员卡权益字典")
    public Result<Map<String, Map<String, Object>>> getBenefitsDict() {
        return Result.success(benefitsService.getBenefitsDict());
    }

    @GetMapping("/config/{type}")
    @ApiOperation("获取会员卡权益配置")
    public Result<AdminBenefitsConfigVo> getConfig(@PathVariable String type) {
        return Result.success(benefitsService.getConfig(type));
    }

    @PutMapping("/config/{type}")
    @ApiOperation("编辑会员卡权益")
    public Result setConfig(@PathVariable String type, @Valid @RequestBody AdminSetConfigParam param) {
        benefitsService.setConfig(type, param);
        return Result.success();
    }

    @PutMapping("/status")
    @ApiOperation("修改会员卡权益状态")
    public Result modifyStatus(@Valid @RequestBody AdminModifyStatusParam param) {
        benefitsService.modifyStatus(param);
        return Result.success();
    }

    @PutMapping("/sort")
    @ApiOperation("修改会员卡权益排序")
    public Result modifySort(@Valid @RequestBody AdminModifySortParam param) {
        benefitsService.modifySort(param);
        return Result.success();
    }
}
