package com.niu.core.controller.api.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.web.IWebNavService;
import com.niu.core.service.admin.web.param.WebNavParam;
import com.niu.core.service.admin.web.param.WebNavSearchParam;
import com.niu.core.service.admin.web.param.WebNavSortParam;
import com.niu.core.service.admin.web.vo.WebNavInfoVo;
import com.niu.core.service.admin.web.vo.WebNavListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PC导航管理控制器
 */

@RestController
@RequestMapping("api/web/nav")
public class WebNavController {

    @Resource
    IWebNavService webNavService;

    /**
     * PC导航管理分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<WebNavListVo>>
     */
    @GetMapping("/page")
    public Result<PageResult<WebNavListVo>> page(@Validated PageParam pageParam, @Validated WebNavSearchParam searchParam) {
        PageResult<WebNavListVo> list = webNavService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * PC导航管理列表
     * @param searchParam
     * @return
     */
    @GetMapping("")
    public Result<List<WebNavListVo>> list(@Validated WebNavSearchParam searchParam) {
        List<WebNavListVo> list = webNavService.list(searchParam);
        return Result.success(list);
    }

    /**
     * PC导航管理详情
     *
     * @param id 主键ID
     * @return Result<WebNavInfoVo>
     */
    @GetMapping("/{id}")
    public Result<WebNavInfoVo> info(@PathVariable("id") Integer id) {
        WebNavInfoVo info = webNavService.info(id);
        return Result.success(info);
    }

    /**
     * PC导航管理添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody WebNavParam addParam) {
        webNavService.add(addParam);
        return Result.success();
    }

    /**
     * PC导航管理编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody WebNavParam editParam) {
        webNavService.edit(id, editParam);
        return Result.success();
    }

    /**
     * PC导航管理删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        webNavService.del(id);
        return Result.success();
    }

    @PutMapping("/sort")
    public Result<Object> sort(@Validated @RequestBody WebNavSortParam sortParam) {
        webNavService.editSort(sortParam);
        return Result.success();
    }

}
