package com.niu.core.controller.adminapi.verify;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.verify.IVerifyService;
import com.niu.core.service.admin.verify.param.VerifySearchParam;
import com.niu.core.service.admin.verify.vo.VerifyInfoVo;
import com.niu.core.service.admin.verify.vo.VerifyListVo;
import com.niu.core.service.api.sys.ISysVerifyService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 核销记录控制器
 */

@RestController
@RequestMapping("adminapi/verify")
@SaCheckLogin
public class VerifyController {

    @Resource
    IVerifyService verifyService;
    @Resource
    ISysVerifyService sysVerifyService;

    /**
     * 核销记录列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<VerifyListVo>>
     */
    @GetMapping("/verify/record")
    public Result<PageResult<VerifyListVo>> list(@Validated PageParam pageParam, @Validated VerifySearchParam searchParam) {
        PageResult<VerifyListVo> list = verifyService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 核销记录详情
     */
    @GetMapping("/verify/{verify_code}")
    public Result<VerifyInfoVo> info(@PathVariable("verify_code") String code) {
        VerifyInfoVo info = verifyService.detail(code);
        return Result.success(info);
    }
    /**
     * @框架核销
     * @description 核销
     */
    @PostMapping("/verify/{verify_code}")
    public Result<?> verifyCode(@PathVariable("verify_code") String code) {
    return Result.success("核销成功",verifyService.verify(code));
    }

    @GetMapping("/detail/{verify_code}")
    public Result<?> getInfoByCode (@PathVariable("verify_code") String code){
        return Result.success(verifyService.getInfoByCode(code));
    }
    /**
     * @获取核销员类型列表
     * @description 获取核销员类型列表
     */
    @GetMapping("verifier/type/list")
    public Result<?> getVerifierTypeList() {
        return Result.success(verifyService.getVerifierTypeList());
    }

}
