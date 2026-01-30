package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISysFabService;
import com.niu.core.service.admin.sys.ISysNoticeService;
import com.niu.core.service.admin.sys.param.SysFabParam;
import com.niu.core.service.admin.sys.param.SysNoticeParam;
import com.niu.core.service.admin.sys.param.SysNoticeSearchParam;
import com.niu.core.service.admin.sys.vo.SysNoticeInfoVo;
import com.niu.core.service.admin.sys.vo.SysNoticeListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 *  悬浮按钮配置
 */

@RestController
@RequestMapping("adminapi/sys/fab")
public class SysFabController {

    @Resource
    ISysFabService sysFabService;


    /**
     * 悬浮按钮配置
     */
    @GetMapping("/fabnavs")
    public Result fabnavs() {
        return Result.success(sysFabService.getFabNavs());
    }

    /**
     * 通知模型添加
     */
    @PostMapping("/fabnavs")
    public Result<Object> setFabnavs(@Validated @RequestBody SysFabParam addParam) {
        sysFabService.getFabConfig(addParam);
        return Result.success();
    }
}
