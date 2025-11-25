package com.niu.core.controller.api.goods;


import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.goods.IGoodsCategoryService;
import com.niu.core.service.api.goods.IGoodsServiceService;
import com.niu.core.service.api.goods.vo.GoodsServiceListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/mall/goods")
public class GoodServiceAndCategoryController {


    @Resource
    IGoodsServiceService goodsServiceService;

    @Resource
    IGoodsCategoryService categoryService;

    /**
     *
     * @return  商品分类树列表
     */
    @GetMapping("/category/tree")
    public Result<JSONArray> tree() {
        return Result.success(categoryService.tree());
    }


    /**
     *
     * @return 商品服务列表
     */
    @GetMapping("/service")
    public Result<List<GoodsServiceListVo>> list() {
        List<GoodsServiceListVo> list = goodsServiceService.getAll();
        return Result.success(list);
    }
}
