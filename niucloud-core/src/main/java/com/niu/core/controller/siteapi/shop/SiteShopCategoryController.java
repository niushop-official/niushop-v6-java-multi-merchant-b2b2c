package com.niu.core.controller.siteapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.shop.ISiteShopCategoryService;
import com.niu.core.service.admin.shop.param.SiteShopCategoryParam;
import com.niu.core.service.admin.shop.param.SiteShopCategorySearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopCategoryInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopCategoryListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 站点分类控制器
 */

@RestController
@RequestMapping("adminapi/shop/site/site_shop_category")
public class SiteShopCategoryController {

    @Resource
    ISiteShopCategoryService siteShopCategoryService;

    /**
     * 站点分类分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteShopCategoryListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SiteShopCategoryListVo>> page(@Validated PageParam pageParam, @Validated SiteShopCategorySearchParam searchParam) {
        PageResult<SiteShopCategoryListVo> list = siteShopCategoryService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 站点分类详情
     *
     * @param id 主键ID
     * @return Result<SiteShopCategoryInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SiteShopCategoryInfoVo> info(@PathVariable("id") Integer id) {
        SiteShopCategoryInfoVo info = siteShopCategoryService.info(id);
        return Result.success(info);
    }

    /**
     * 站点分类添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteShopCategoryParam addParam) {
        siteShopCategoryService.add(addParam);
        return Result.success();
    }

    /**
     * 站点分类编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopCategoryParam editParam) {
        siteShopCategoryService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 站点分类删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        siteShopCategoryService.del(id);
        return Result.success();
    }

}
