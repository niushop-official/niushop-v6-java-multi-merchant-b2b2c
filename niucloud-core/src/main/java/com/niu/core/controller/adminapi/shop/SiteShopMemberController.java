package com.niu.core.controller.adminapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.site.shop.ISiteShopMemberService;
import com.niu.core.service.site.shop.param.SiteShopMemberParam;
import com.niu.core.service.site.shop.param.SiteShopMemberSearchParam;
import com.niu.core.service.site.shop.vo.SiteShopMemberInfoVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺会员表控制器
 */

@RestController
@RequestMapping("adminapi/shop/admin/site_shop_member")
public class SiteShopMemberController {

    @Resource
    ISiteShopMemberService siteShopMemberService;

    /**
     * 店铺会员表分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteShopMemberListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SiteShopMemberListVo>> page(@Validated PageParam pageParam, @Validated SiteShopMemberSearchParam searchParam) {
        PageResult<SiteShopMemberListVo> list = siteShopMemberService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 店铺会员表详情
     *
     * @param id 主键ID
     * @return Result<SiteShopMemberInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SiteShopMemberInfoVo> info(@PathVariable("id") Integer id) {
        SiteShopMemberInfoVo info = siteShopMemberService.info(id, null);
        return Result.success(info);
    }

    /**
     * 店铺会员表添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteShopMemberParam addParam) {
        siteShopMemberService.add(addParam);
        return Result.success();
    }

    /**
     * 店铺会员表编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopMemberParam editParam) {
        siteShopMemberService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 店铺会员表删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        siteShopMemberService.del(id);
        return Result.success();
    }

}
