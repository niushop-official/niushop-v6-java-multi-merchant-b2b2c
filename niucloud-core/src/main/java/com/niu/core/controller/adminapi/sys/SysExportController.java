package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.convert.Convert;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.exception.CommonException;
import com.niu.core.enums.sys.ExportEnum;
import com.niu.core.service.admin.sys.ISysExportService;
import com.niu.core.service.admin.sys.param.SysExportSearchParam;
import com.niu.core.service.admin.sys.vo.SysExportListVo;
import com.niu.core.service.admin.sys.vo.SysExportVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 导出报控制器
 */

@RestController
@RequestMapping("adminapi/sys/export")
@SaCheckLogin
public class SysExportController {

    @Resource
    ISysExportService sysExportService;

    /**
     * 导出报列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < SysExportListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SysExportListVo>> list(@Validated PageParam pageParam,
                                                    @Validated SysExportSearchParam searchParam) {
        PageResult<SysExportListVo> list = sysExportService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取导出状态列表
     */
    @GetMapping("/status")
    public Result<Object> getExportStatus() {
        return Result.success(ExportEnum.getStatus());
    }

    /**
     * 报表导出类型
     */
    @GetMapping("/type/admin")
    public Result<Map<String, String>> getExportDataType() {
        return Result.success(sysExportService.getExportDataType("admin"));
    }

    /**
     * 报表导出类型
     */
    @GetMapping("/type/site")
    public Result<Map<String, String>> getExportDataTypeSite() {
        return Result.success(sysExportService.getExportDataType("site"));
    }

    /**
     * 报表导出数据检查
     */
    @GetMapping("/check/{type}")
    public Result<Object> check(@Validated @PathVariable("type") String type, @RequestParam Map<String, Object> objectMap, HttpServletRequest request) {
        return Result.success("", sysExportService.checkExportData(type, objectMap, request));
    }

    /**
     * 报表导出
     */
    @PostMapping("/{type}")
    public Result<Object> export(@Validated @PathVariable("type") String type, @RequestBody SysExportVo params, HttpServletRequest request) {
        sysExportService.exportData(type, params.getParams(), request);
        return Result.success();
    }

    /**
     * 获取导出数据列
     */
    @GetMapping("/column/{type}")
    public Result<Object> getExportDataColumn(@Validated @PathVariable("type") String type, @RequestParam Map<String, Object> whereMap, HttpServletRequest request) {
        return Result.success(sysExportService.getExportDataColumn(type, whereMap, request));
    }

    /**
     * 导出报删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        sysExportService.del(id);
        return Result.success();
    }

    /**
     * 重新执行导出
     * 前端 axios 全局 Content-Type 为 application/json，以 JSON body {"id": x} 传参，
     * 故从请求体读取 id（对齐 PHP sys.Export/againExportData）
     *
     * @param body 请求体，包含 id
     * @return Result<Object>
     */
    @PostMapping("/again_export")
    public Result<Object> againExportData(@RequestBody Map<String, Object> body) {
        Integer id = Convert.toInt(body != null ? body.get("id") : null, null);
        if (id == null) {
            throw new CommonException("缺少id参数");
        }
        sysExportService.againExportData(id);
        return Result.success();
    }

}
