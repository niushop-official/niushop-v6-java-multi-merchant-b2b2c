package com.niu.core.controller.siteapi.site;

import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.site.vo.SiteCategoryInfoVo;
import com.niu.core.service.admin.site.vo.SiteCategoryListVo;
import com.niu.core.service.core.site.ICoreSiteCategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行业类目API控制器（面向移动端）
 */
@RestController
@RequestMapping("api/site/category")
public class SiteCategoryApiController {

    @Resource
    private ICoreSiteCategoryService siteCategoryService;

    /**
     * 获取行业类目树（移动端接口）
     *
     * @param name  分类名称
     * @param addon 归属应用
     * @return Result<List<SiteCategoryListVo>>
     */
    @GetMapping("/tree")
    public Result<List<SiteCategoryListVo>> tree(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String addon) {
        List<SiteCategoryListVo> tree = siteCategoryService.tree(name, addon);
        return Result.success(tree);
    }

    /**
     * 获取行业类目详情（移动端接口）
     *
     * @param categoryId 类目ID
     * @return Result<SiteCategoryInfoVo>
     */
    @GetMapping("/info/{categoryId}")
    public Result<SiteCategoryInfoVo> info(@PathVariable("categoryId") Integer categoryId) {
        SiteCategoryInfoVo siteCategory = siteCategoryService.info(categoryId);
        return Result.success(siteCategory);
    }

    /**
     * 兼容 PHP GET category/:id
     */
    @GetMapping("/{categoryId}")
    public Result<SiteCategoryInfoVo> infoAlias(@PathVariable("categoryId") Integer categoryId) {
        return info(categoryId);
    }
}