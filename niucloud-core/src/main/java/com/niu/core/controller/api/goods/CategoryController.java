package com.niu.core.controller.api.goods;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.goods.IGoodsCategoryService;
import com.niu.core.service.api.goods.param.GoodsCategorySearchParam;
import com.niu.core.service.api.goods.vo.GoodsCategoryListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 */

@RestController
@RequestMapping("api/basic/goods")
public class CategoryController {

    @Resource
    IGoodsCategoryService categoryService;

    /**
     * 商品分类树列表
     *
     * @return Result<List<ShopGoodsCategoryListVo>>
     */
    @GetMapping("/category/tree")
    public Result<JSONArray> tree(@Validated GoodsCategorySearchParam searchParam) {
        return Result.success(categoryService.tree(searchParam.getSiteId()));
    }


    /**
     * 商品分类列表
     *
     * @return Result<List<ShopGoodsCategoryListVo>>
     */
    @GetMapping("/category/list")
    public Result<List<GoodsCategoryListVo>> list(@Validated GoodsCategorySearchParam searchParam) {
        List<GoodsCategoryListVo> list = categoryService.list(searchParam);
        return Result.success(list);
    }

}
