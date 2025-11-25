package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISystemService;
import com.niu.core.service.admin.sys.param.SysQrcodeSearchParam;
import com.niu.core.service.admin.sys.vo.SysQrcodeVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统控制器
 */

@RestController
@RequestMapping("adminapi/sys")
@SaCheckLogin
public class SystemController {

    @Resource
    ISystemService systemService;

    /**
     * 清理缓存
     * @return
     */
    @PostMapping("/cache/clear")
    public Result<?> clearCache() {
        systemService.clearCache();
        return Result.success();
    }

    /**
     * 获取二维码
     * @return
     */
    @PostMapping("/qrcode")
    public Result<SysQrcodeVo> qrcode(@Validated @RequestBody SysQrcodeSearchParam params) {
        return Result.success(systemService.getQrcode(params.getFolder(), params.getPage(),params.getParams()));
    }

}