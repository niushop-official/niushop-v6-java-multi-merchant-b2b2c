package com.niu.core.controller.siteapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.shop.ISiteShopService;
import com.niu.core.service.admin.shop.param.SiteShopInfoParam;
import com.niu.core.service.admin.shop.param.SiteShopParam;
import com.niu.core.service.admin.shop.param.SiteShopSearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺扩展表控制器
 */
@Tag(name = "站点端商户管理")
@RestController
@RequestMapping("adminapi/shop/site/site_shop")
public class SiteShopController {

    @Resource
    ISiteShopService siteShopService;

    /**
     * 店铺扩展表分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteShopListVo>>
     */
    @Operation(summary = "分页查询商户")
    @GetMapping("")
    public Result<PageResult<SiteShopListVo>> page(@Validated PageParam pageParam, @Validated SiteShopSearchParam searchParam) {
        PageResult<SiteShopListVo> list = siteShopService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 店铺扩展表详情
     *
     * @param id 主键ID
     * @return Result<SiteShopInfoVo>
     */
    @Operation(summary = "获取商户详情")
    @GetMapping("/{id}")
    public Result<SiteShopInfoVo> info(@PathVariable("id") Integer id) {
        SiteShopInfoVo info = siteShopService.info(id);
        return Result.success(info);
    }

    /**
     * 店铺扩展表添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @Operation(summary = "新增商户")
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteShopParam addParam) {
        siteShopService.add(addParam);
        return Result.success();
    }

    /**
     * 店铺扩展表编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @Operation(summary = "编辑商户")
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopInfoParam editParam) {
        siteShopService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 店铺扩展表删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @Operation(summary = "删除商户")
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        siteShopService.del(id);
        return Result.success();
    }

}
