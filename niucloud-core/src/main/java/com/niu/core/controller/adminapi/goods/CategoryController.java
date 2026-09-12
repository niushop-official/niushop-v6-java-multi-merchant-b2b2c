package com.niu.core.controller.adminapi.goods;

import cn.hutool.json.JSONArray;
import com.niu.core.common.annotation.AdminLog;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.trade.TradeTypeDict;
import com.niu.core.service.admin.goods.IGoodsCategoryService;
import com.niu.core.service.admin.goods.param.EditGoodsCategorySortParam;
import com.niu.core.service.admin.goods.param.GoodsCategoryParam;
import com.niu.core.service.admin.goods.param.GoodsCategorySearchParam;
import com.niu.core.service.admin.goods.vo.GoodsCategoryInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsCategoryListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品分类控制器
 * 对齐 PHP goods.admin.Category
 */
@Tag(name = "平台端商品分类")
@RestController
@RequestMapping("adminapi/basic/admin/goods")
public class CategoryController {

    @Resource
    IGoodsCategoryService categoryService;

    /**
     * 商品分类树列表
     * 对齐 PHP: GET admin/goods/tree
     */
    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public Result<JSONArray> tree(@Validated GoodsCategorySearchParam searchParam) {
        return Result.success(categoryService.tree(searchParam));
    }

    /**
     * 商品分类列表
     * 对齐 PHP: GET admin/goods/category
     */
    @Operation(summary = "查询分类列表")
    @GetMapping("/category")
    public Result<List<GoodsCategoryListVo>> list(@Validated GoodsCategorySearchParam searchParam) {
        List<GoodsCategoryListVo> list = categoryService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 获取商品分类树结构供弹框调用
     * 对齐 PHP: GET admin/goods/category/components
     */
    @Operation(summary = "获取组件分类树")
    @GetMapping("/category/components")
    public Result<JSONArray> components(@Validated GoodsCategorySearchParam searchParam) {
        return Result.success(categoryService.components(searchParam));
    }

    /**
     * 获取商品分类业务类型
     * 对齐 PHP: GET admin/goods/category/trade_type
     * 须放在 /category/{id} 之前
     */
    @Operation(summary = "获取分类业务类型")
    @GetMapping("/category/trade_type")
    public Result<Map<String, String>> getTradeType() {
        return Result.success(TradeTypeDict.getTradeType("goods_category"));
    }

    /**
     * 商品分类详情
     */
    @Operation(summary = "获取分类详情")
    @GetMapping("/category/{id}")
    public Result<GoodsCategoryInfoVo> info(@PathVariable("id") Integer id) {
        GoodsCategoryInfoVo info = categoryService.info(id);
        return Result.success(info);
    }

    /**
     * 商品分类添加
     */
    @Operation(summary = "新增分类")
    @PostMapping("/category")
    public Result<Object> add(@Validated @RequestBody GoodsCategoryParam addParam) {
        categoryService.add(addParam);
        return Result.success();
    }

    /**
     * 商品分类编辑
     */
    @Operation(summary = "编辑分类")
    @PutMapping("/category/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody GoodsCategoryParam editParam) {
        categoryService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 拖拽编辑商品分类（对齐 PHP POST category/update）
     */
    @Operation(summary = "更新分类排序")
    @PostMapping("/category/update")
    public Result<Object> update(@Validated @RequestBody EditGoodsCategorySortParam editParam) {
        categoryService.editGoodsCategorySort(editParam);
        return Result.success();
    }

    /**
     * 兼容旧 PUT 路径
     */
    @Operation(summary = "更新分类排序")
    @PutMapping("/category/update")
    public Result<Object> updateByPut(@Validated @RequestBody EditGoodsCategorySortParam editParam) {
        categoryService.editGoodsCategorySort(editParam);
        return Result.success();
    }

    /**
     * 商品分类删除
     */
    @Operation(summary = "删除分类")
    @DeleteMapping("/category/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        categoryService.del(id);
        return Result.success();
    }
}
