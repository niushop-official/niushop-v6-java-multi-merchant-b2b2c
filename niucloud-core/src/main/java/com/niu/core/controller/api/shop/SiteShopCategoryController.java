package com.niu.core.controller.api.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.shop.ISiteShopCategoryService;
import com.niu.core.service.admin.shop.vo.SiteShopCategoryListVo;
import com.niu.core.service.api.shop.ISiteShopService;
import com.niu.core.service.api.shop.param.SiteShopSearchParam;
import com.niu.core.service.api.shop.vo.SiteShopListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站点分类控制器（兼容层，底层已迁移至 site_category）
 *
 * @deprecated 请使用站点行业类目相关接口
 */
@Deprecated
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
    public Result<PageResult<SiteShopListVo>> page(@Validated PageParam pageParam, @Validated SiteShopSearchParam searchParam) {
        PageResult<SiteShopListVo> list = siteShopService.webPage(pageParam, searchParam);
        return Result.success(list);
    }

}
