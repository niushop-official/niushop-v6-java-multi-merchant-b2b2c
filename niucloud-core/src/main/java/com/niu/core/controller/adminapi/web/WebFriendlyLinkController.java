package com.niu.core.controller.adminapi.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.web.IWebFriendlyLinkService;
import com.niu.core.service.admin.web.param.WebFriendlyLinkParam;
import com.niu.core.service.admin.web.param.WebFriendlyLinkSearchParam;
import com.niu.core.service.admin.web.param.WebFriendlyLinkSortParam;
import com.niu.core.service.admin.web.vo.WebFriendlyLinkInfoVo;
import com.niu.core.service.admin.web.vo.WebFriendlyLinkListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 友情链接控制器
 */

@RestController
@RequestMapping("adminapi/web/friendly_link")
@SaCheckLogin
public class WebFriendlyLinkController {

    @Resource
    IWebFriendlyLinkService webFriendlyLinkService;

    /**
     * 友情链接分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<WebFriendlyLinkListVo>>
     */
    @GetMapping("")
    public Result<PageResult<WebFriendlyLinkListVo>> page(@Validated PageParam pageParam, @Validated WebFriendlyLinkSearchParam searchParam) {
        PageResult<WebFriendlyLinkListVo> list = webFriendlyLinkService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 友情链接详情
     *
     * @param id 主键ID
     * @return Result<WebFriendlyLinkInfoVo>
     */
    @GetMapping("/{id}")
    public Result<WebFriendlyLinkInfoVo> info(@PathVariable("id") Integer id) {
        WebFriendlyLinkInfoVo info = webFriendlyLinkService.info(id);
        return Result.success(info);
    }

    /**
     * 友情链接添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody WebFriendlyLinkParam addParam) {
        webFriendlyLinkService.add(addParam);
        return Result.success();
    }

    /**
     * 友情链接编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody WebFriendlyLinkParam editParam) {
        webFriendlyLinkService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 友情链接删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        webFriendlyLinkService.del(id);
        return Result.success();
    }

    /**
     * 修改排序
     * @param sortParam
     * @return
     */
    @PutMapping("/sort")
    public Result<Object> editSort(@Validated @RequestBody WebFriendlyLinkSortParam sortParam) {
        webFriendlyLinkService.editSort(sortParam);
        return Result.success();
    }

}
