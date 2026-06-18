package com.niu.core.controller.adminapi.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.member.IMemberImportService;
import com.niu.core.service.admin.member.param.MemberImportAddParam;
import com.niu.core.service.admin.member.param.MemberImportSearchParam;
import com.niu.core.service.admin.member.vo.MemberImportListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("adminapi/member/member/import")
public class MemberImportController {

    @Resource
    private IMemberImportService memberImportService;

    @GetMapping("")
    @SaCheckLogin
    public Result<PageResult<MemberImportListVo>> getMemberImportPage(
            @Validated PageParam pageParam,
            @Validated MemberImportSearchParam searchParam) {
        PageResult<MemberImportListVo> result = memberImportService.getPage(pageParam, searchParam);
        return Result.success(result);
    }

    @PostMapping("")
    @SaCheckLogin
    public Result<Object> memberImport(@Validated @RequestBody MemberImportAddParam addParam) {
        memberImportService.memberImport(addParam.getData());
        return Result.success();
    }
}
