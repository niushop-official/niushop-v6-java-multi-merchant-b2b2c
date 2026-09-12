package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISystemService;
import com.niu.core.service.admin.sys.param.SysQrcodeSearchParam;
import com.niu.core.service.admin.sys.vo.SysQrcodeVo;
import com.niu.core.service.admin.sys.vo.SysSystemInfoVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
     * 清理表缓存
     * @return
     */
    @PostMapping("/schema/clear")
    public Result<?> schemaCache() {
        systemService.schemaCache();
        return Result.success();
    }

    /**
     * 获取二维码
     * @return
     */
    @GetMapping("/qrcode")
    public Result<SysQrcodeVo> qrcode(@Validated SysQrcodeSearchParam params,@RequestParam Map<String, String> allParams) {
        return Result.success(systemService.getQrcode(params.getFolder(), params.getPage(),allParams));
    }

    /**
     * 校验消息队列是否正常运行
     */
    @GetMapping("/job")
    public Result<Boolean> checkJob() {
        return Result.success(systemService.checkJob());
    }

    /**
     * 校验计划任务是否正常运行
     */
    @GetMapping("/schedule")
    public Result<Boolean> checkSchedule() {
        return Result.success(systemService.checkSchedule());
    }

    /**
     * 获取系统环境信息
     */
    @GetMapping("/system")
    public Result<SysSystemInfoVo> getSystemInfo() {
        return Result.success(systemService.getSystemInfo());
    }

    /**
     * 获取环境变量
     */
    @GetMapping("/env")
    public Result<Map<String, Object>> getEnvInfo() {
        return Result.success(systemService.getEnvInfo());
    }

    /**
     * 获取布局列表
     */
    @GetMapping("/layout")
    public Result<JSONArray> getLayout() {
        return Result.success(systemService.getLayoutList());
    }

    /**
     * 设置布局
     */
    @PutMapping("/layout")
    public Result<Boolean> setLayout(@RequestParam("key") String key) {
        return Result.success(systemService.setLayout(key));
    }

}