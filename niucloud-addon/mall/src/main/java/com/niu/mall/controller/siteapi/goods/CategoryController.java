package com.niu.mall.controller.siteapi.goods;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.core.goods.vo.ShopGoodsCategoryConfigVo;
import com.niu.mall.service.site.goods.IGoodsCategoryService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

/**
 * 商品分类控制器
 */

@RestController
@RequestMapping("adminapi/mall/site/goods")
public class CategoryController {

    @Resource
    IGoodsCategoryService categoryService;


    /**
     * 获取商品分类配置
     *
     * @return
     */
    @GetMapping("/category/config")
    public Result<ShopGoodsCategoryConfigVo> getGoodsCategoryConfig() {
        return Result.success(categoryService.getGoodsCategoryConfig());
    }

    /**
     * 设置商品分类配置
     *
     * @return
     */
    @PostMapping("/category/config")
    public Result<Object> setGoodsCategoryConfig(@RequestBody JSONObject data) {
        categoryService.setGoodsCategoryConfig(data);
        return Result.success();
    }
}
