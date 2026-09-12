package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.annotation.AdminLog;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISysUserLogService;
import com.niu.core.service.admin.sys.param.SysUserLogSearchParam;
import com.niu.core.service.admin.sys.vo.SysUserLogInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserLogListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员操作记录管理
 */
@RestController
@RequestMapping("/adminapi/site/")
@SaCheckLogin
@AdminLog("操作日志")
public class UserLogController {

    @Resource
    ISysUserLogService sysUserLogService;

    /**
     * 管理员操作记录列表
     */
    @GetMapping("/log")
    public Result<PageResult<SysUserLogListVo>> list(@Validated PageParam pageParam,
                                                     @Validated SysUserLogSearchParam searchParam) {
        PageResult<SysUserLogListVo> list = sysUserLogService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 管理员操作记录详情
     */
    @GetMapping("/log/{id}")
    public Result<SysUserLogInfoVo> info(@Validated @PathVariable("id") Integer id) {
        SysUserLogInfoVo info = sysUserLogService.info(id);
        return Result.success(info);
    }

    /**
     * 清空当前站点操作日志
     * DELETE site/log/destroy → UserLogService::destroy
     */
    @AdminLog("清空日志")
    @DeleteMapping("/log/destroy")
    public Result<?> destroy() {
        sysUserLogService.destroy();
        return Result.success();
    }
}
