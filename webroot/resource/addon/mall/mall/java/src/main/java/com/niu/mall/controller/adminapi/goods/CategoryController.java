package com.niu.mall.controller.adminapi.goods;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.core.goods.vo.GoodsCategoryConfigVo;
import com.niu.mall.service.admin.goods.IGoodsCategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 新增商品分类配置-513
 */
@RestController
@RequestMapping("adminapi/mall/admin/goods/category")
public class CategoryController {

    @Resource
    IGoodsCategoryService categoryService;
    /**
     * 获取商品分类配置
     *
     * @return
     */
    @GetMapping("/config")
    public Result<GoodsCategoryConfigVo> getGoodsCategoryConfig() {
        return Result.success(categoryService.getGoodsCategoryConfig());
    }

    /**
     * 设置商品分类配置
     *
     * @return
     */
    @PostMapping("/config")
    public Result<Object> setGoodsCategoryConfig(@RequestBody JSONObject data) {
        categoryService.setGoodsCategoryConfig(data);
        return Result.success();
    }
}
