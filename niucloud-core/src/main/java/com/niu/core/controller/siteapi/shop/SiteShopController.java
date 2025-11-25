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
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺扩展表控制器
 */

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
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        siteShopService.del(id);
        return Result.success();
    }

}
