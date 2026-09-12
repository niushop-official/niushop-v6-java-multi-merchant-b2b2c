package com.niu.mall.controller.adminapi.site_shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.site.param.SiteAddParam;
import com.niu.core.service.admin.site.param.SiteEditParam;
import com.niu.mall.service.admin.site_shop.IAdminShopService;
import com.niu.mall.service.admin.site_shop.param.AdminShopListQueryParam;
import com.niu.mall.service.admin.site_shop.param.AdminShopPageQueryParam;
import com.niu.mall.service.admin.site_shop.param.AdminShopSelectQueryParam;
import com.niu.mall.service.admin.site_shop.vo.AdminShopInfoVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopListVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopPageVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopSelectVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("adminapi/mall/admin/shop")
@Api(tags = "平台端-店铺管理")
public class AdminShopController {

    @Autowired
    private IAdminShopService shopService;

    @GetMapping
    @ApiOperation("店铺分页列表")
    public Result<PageResult<AdminShopPageVo>> pages(PageParam pageParam, AdminShopPageQueryParam queryParam) {
        return Result.success(shopService.getPage(pageParam, queryParam));
    }

    @GetMapping("/list")
    @ApiOperation("店铺列表（不分页）")
    public Result<List<AdminShopListVo>> list(AdminShopListQueryParam queryParam) {
        return Result.success(shopService.getList(queryParam));
    }

    @GetMapping("/{id}")
    @ApiOperation("店铺详情")
    public Result<AdminShopInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(shopService.getInfo(id));
    }

    @PostMapping
    @ApiOperation("添加店铺")
    public Result<Map<String, Integer>> add(@Valid @RequestBody SiteAddParam param) {
        Integer siteId = shopService.add(param);
        return Result.success(Map.of("site_id", siteId));
    }

    @PutMapping("/{id}")
    @ApiOperation("编辑店铺")
    public Result edit(@PathVariable("id") Integer id, @Valid @RequestBody SiteEditParam param) {
        shopService.edit(id, param);
        return Result.success();
    }

    @GetMapping("/select")
    @ApiOperation("店铺选择列表（分页）")
    public Result<PageResult<AdminShopSelectVo>> select(PageParam pageParam, AdminShopSelectQueryParam queryParam) {
        return Result.success(shopService.getSelectPage(pageParam, queryParam));
    }
}
