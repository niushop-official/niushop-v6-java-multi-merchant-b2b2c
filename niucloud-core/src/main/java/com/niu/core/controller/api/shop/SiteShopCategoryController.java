package com.niu.core.controller.api.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.shop.ISiteShopCategoryService;
import com.niu.core.service.admin.shop.ISiteShopService;
import com.niu.core.service.admin.shop.param.SiteShopSearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopCategoryListVo;
import com.niu.core.service.admin.shop.vo.SiteShopListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站点分类控制器
 */

@RestController
@RequestMapping("api/shop")
public class SiteShopCategoryController {

    @Resource
    ISiteShopCategoryService siteShopCategoryService;

    @Resource
    ISiteShopService siteShopService;


    /**
     *获取所有分类
     * @return
     */
    @GetMapping("/web/shop_category")
    public Result<List<SiteShopCategoryListVo>> all(){
        return Result.success(siteShopCategoryService.all());
    }

    /**
     * 店铺扩展表分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteShopListVo>>
     */
    @GetMapping("/web/shop")
    public Result<PageResult<com.niu.core.service.admin.shop.vo.SiteShopListVo>> page(@Validated PageParam pageParam, @Validated SiteShopSearchParam searchParam) {
        PageResult<SiteShopListVo> list = siteShopService.page(pageParam, searchParam);
        return Result.success(list);
    }

}
