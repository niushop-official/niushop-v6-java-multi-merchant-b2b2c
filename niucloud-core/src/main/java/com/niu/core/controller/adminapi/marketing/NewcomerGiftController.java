package com.niu.core.controller.adminapi.marketing;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.marketing.INewcomerGiftService;
import com.niu.core.service.admin.marketing.param.NewcomerGiftConfigParam;
import com.niu.core.service.admin.marketing.param.NewcomerGiftRecordsSearchParam;
import com.niu.core.service.admin.marketing.vo.NewcomerGiftConfigVo;
import com.niu.core.service.admin.marketing.vo.NewcomerGiftRecordsListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 新人礼后台控制器
 */
@RestController
@RequestMapping("adminapi/marketing/admin/newcomer_gift")
@SaCheckLogin
public class NewcomerGiftController {

    @Resource
    private INewcomerGiftService newcomerGiftService;

    @GetMapping("/config")
    public Result<NewcomerGiftConfigVo> getConfig() {
        return Result.success(newcomerGiftService.getConfig());
    }

    @PostMapping("/config")
    public Result<?> setConfig(@RequestBody NewcomerGiftConfigParam param) {
        newcomerGiftService.setConfig(param);
        return Result.success();
    }

    @GetMapping("/records")
    public Result<PageResult<NewcomerGiftRecordsListVo>> records(@Validated PageParam pageParam,
                                                                 @Validated NewcomerGiftRecordsSearchParam searchParam) {
        return Result.success(newcomerGiftService.records(pageParam, searchParam));
    }

    @GetMapping("/records/count")
    public Result<Integer> count() {
        return Result.success(newcomerGiftService.count());
    }
}
