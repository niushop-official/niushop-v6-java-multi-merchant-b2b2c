package com.niu.core.controller.siteapi.site;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.entity.site.SiteCategory;
import com.niu.core.service.admin.site.vo.SiteCategoryInfoVo;
import com.niu.core.service.admin.site.vo.SiteCategoryListVo;
import com.niu.core.service.core.site.ICoreSiteCategoryService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 行业类目管理控制器
 */
@RestController
@RequestMapping("adminapi/site/category")
public class SiteCategoryController {

    @Resource
    private ICoreSiteCategoryService siteCategoryService;

    /**
     * 行业类目列表--分页
     *
     * @param pageParam 分页参数
     * @param name      分类名称
     * @param addon     归属应用
     * @return Result<PageResult<SiteCategory>>
     */
    @GetMapping("")
    public Result<PageResult<SiteCategoryListVo>> page(
            @Validated PageParam pageParam,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String addon) {
        PageResult<SiteCategoryListVo> page = siteCategoryService.page(pageParam, name, addon);
        return Result.success(page);
    }

    /**
     * 获取所有行业类目--树状结构
     *
     * @param name  分类名称
     * @param addon 归属应用
     * @return Result<List<SiteCategory>>
     */
    @GetMapping("/tree")
    public Result<List<SiteCategoryListVo>> tree(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String addon) {
        List<SiteCategoryListVo> tree = siteCategoryService.tree(name, addon);
        return Result.success(tree);
    }

    /**
     * 顶级行业类目列表
     *
     * @param addon 归属应用
     * @return Result<List<SiteCategory>>
     */
    @GetMapping("/top")
    public Result<List<SiteCategoryListVo>> topList(@RequestParam(required = false) String addon) {
        List<SiteCategoryListVo> list = siteCategoryService.topList(addon);
        return Result.success(list);
    }

    /**
     * 行业类目详情
     *
     * @param id 主键ID
     * @return Result<SiteCategory>
     */
    @GetMapping("/{id}")
    public Result<SiteCategoryInfoVo> info(@PathVariable("id") Integer id) {
        SiteCategoryInfoVo info = siteCategoryService.info(id);
        return Result.success(info);
    }

    /**
     * 行业类目添加
     *
     * @param siteCategory 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteCategory siteCategory) {
        siteCategoryService.add(siteCategory);
        return Result.success();
    }

    /**
     * 行业类目编辑
     *
     * @param id           主键ID
     * @param siteCategory 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteCategory siteCategory) {
        siteCategoryService.edit(id, siteCategory);
        return Result.success();
    }

    /**
     * 行业类目删除
     *
     * @param id 主键ID
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        siteCategoryService.del(id);
        return Result.success();
    }

    /**
     * 拖拽修改分类排序
     *
     * @param siteCategoryList 分类列表
     * @return Result<Object>
     */
    @PutMapping("/sort")
    public Result<Object> updateSort(@RequestBody List<SiteCategory> siteCategoryList) {
        siteCategoryService.updateSort(siteCategoryList);
        return Result.success();
    }

    /**
     * 设置行业类目轮播图
     *
     * @param params 参数对象，包含category_id和banner_list
     * @return Result<Object>
     */
    @PutMapping("/{id}/banner")
    public Result<Object> setBanner(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, Object> params) {
        // 从前端传递的参数对象中提取banner_list
        Object bannerListObj = params.get("banner_list");
        String bannerListJson = bannerListObj != null ? bannerListObj.toString() : "[]";

        // 如果是集合或数组，转换为JSON字符串
        if (bannerListObj instanceof Collection) {
            bannerListJson = com.alibaba.fastjson2.JSON.toJSONString(bannerListObj);
        }

        siteCategoryService.setBanner(id, bannerListJson);
        return Result.success();
    }
}