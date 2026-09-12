package com.niu.core.controller.siteapi.goods;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.service.site.goods.IGoodsCategoryService;
import com.niu.core.service.site.goods.param.EditGoodsCategorySortParam;
import com.niu.core.service.site.goods.param.GoodsCategoryParam;
import com.niu.core.service.site.goods.param.GoodsCategorySearchParam;
import com.niu.core.service.site.goods.vo.GoodsCategoryInfoVo;
import com.niu.core.service.site.goods.vo.GoodsCategoryListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 */

@RestController
@RequestMapping("adminapi/basic/site/goods")
public class CategoryController {

    @Resource
    IGoodsCategoryService categoryService;

    /**
     * 商品分类树列表
     * 对齐 PHP: GET site/goods/tree（支持 addon，不过滤 is_show）
     */
    @GetMapping("/tree")
    public Result<JSONArray> tree(@Validated GoodsCategorySearchParam searchParam) {
        return Result.success(categoryService.tree(searchParam));
    }

    /**
     * 商品分类树（弹框组件）
     * 对齐 PHP: GET site/goods/category/components（is_show=1，支持 addon）
     */
    @GetMapping({"/components", "/category/components"})
    public Result<JSONArray> components(@Validated GoodsCategorySearchParam searchParam) {
        if (searchParam == null) {
            searchParam = new GoodsCategorySearchParam();
        }
        searchParam.setIsShow(1);
        return Result.success(categoryService.tree(searchParam));
    }

    /**
     * 商品分类列表
     *
     * @return Result<List<GoodsCategoryListVo>>
     */
    @GetMapping("/category")
    public Result<List<GoodsCategoryListVo>> list(@Validated GoodsCategorySearchParam searchParam) {
        List<GoodsCategoryListVo> list = categoryService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 商品分类详情
     *
     * @param id 主键ID
     * @return Result<ShopGoodsCategoryInfoVo>
     */
    @GetMapping("/category/{id}")
    public Result<GoodsCategoryInfoVo> info(@PathVariable("id") Integer id) {
        GoodsCategoryInfoVo info = categoryService.info(id);
        return Result.success(info);
    }

    /**
     * 商品分类添加
     *
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("/category")
    public Result<Object> add(@Validated @RequestBody GoodsCategoryParam addParam) {
        categoryService.add(addParam);
        return Result.success();
    }

    /**
     * 商品分类编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/category/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody GoodsCategoryParam editParam) {
        categoryService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 编辑商品分类
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/category/update")
    public Result<Object> update(@Validated @RequestBody EditGoodsCategorySortParam editParam) {
        categoryService.editGoodsCategorySort(editParam);
        return Result.success();
    }

    /**
     * 商品分类删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/category/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        categoryService.del(id);
        return Result.success();
    }

}
