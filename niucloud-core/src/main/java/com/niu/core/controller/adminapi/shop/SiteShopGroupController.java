package com.niu.core.controller.adminapi.shop;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.service.admin.shop.IShopSiteGroupService;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.param.SiteGroupAddParam;
import com.niu.core.service.admin.site.param.SiteGroupParam;
import com.niu.core.service.admin.site.param.SiteGroupSearchParam;
import com.niu.core.service.admin.site.vo.SiteGroupListVo;
import com.niu.core.service.core.addon.CoreAddonBaseService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺分组（分组权限）控制器
 */

@RestController
@RequestMapping("adminapi/shop/admin/shop_group")
@SaCheckLogin
public class SiteShopGroupController {

    @Resource
    IShopSiteGroupService siteGroupService;

    /**
     * 店铺分组（分组权限）列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteGroupListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SiteGroupListVo>> list(@Validated PageParam pageParam,
                                                     @Validated SiteGroupSearchParam searchParam) {
        PageResult<SiteGroupListVo> list = siteGroupService.list(pageParam, searchParam);
        return Result.success(list);
    }

    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteGroupAddParam siteGroupAddParam){
        siteGroupService.add(siteGroupAddParam);
        return Result.success();
    }

    /**
     * 店铺分组（分组权限）详情
     * @param id 主键ID
     * @return Result<SiteGroupInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SiteGroup> info(@Validated @PathVariable("id") Integer id) {
        SiteGroup info = siteGroupService.info(id);
        return Result.success(info);
    }

    /**
     * 店铺分组（分组权限）编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody SiteGroupParam editParam) {
        siteGroupService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 店铺分组（分组权限）删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        siteGroupService.del(id);
        return Result.success();
    }

    /**
     *获取所有分组
     * @return
     */
    @GetMapping("/all")
    public Result<List<SiteGroup>> all(){
        return Result.success(siteGroupService.getAll());
    }





}
