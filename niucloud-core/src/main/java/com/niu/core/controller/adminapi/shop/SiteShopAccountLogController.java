package com.niu.core.controller.adminapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.shop.ISiteShopAccountLogService;
import com.niu.core.service.admin.shop.param.SiteShopAccountLogParam;
import com.niu.core.service.admin.shop.param.SiteShopAccountLogSearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopAccountLogInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopAccountLogListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 商家结算账单记录表控制器
 */

@RestController
@RequestMapping("adminapi/shop/admin/site_shop_account_log")
public class SiteShopAccountLogController {

    @Resource
    ISiteShopAccountLogService siteShopAccountLogService;

    /**
     * 商家结算账单记录表分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteShopAccountLogListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SiteShopAccountLogListVo>> page(@Validated PageParam pageParam, @Validated SiteShopAccountLogSearchParam searchParam) {
        PageResult<SiteShopAccountLogListVo> list = siteShopAccountLogService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商家结算账单记录表详情
     *
     * @param id 主键ID
     * @return Result<SiteShopAccountLogInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SiteShopAccountLogInfoVo> info(@PathVariable("id") Integer id) {
        SiteShopAccountLogInfoVo info = siteShopAccountLogService.info(id);
        return Result.success(info);
    }

    /**
     * 商家结算账单记录表添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteShopAccountLogParam addParam) {
        siteShopAccountLogService.add(addParam);
        return Result.success();
    }

    /**
     * 商家结算账单记录表编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopAccountLogParam editParam) {
        siteShopAccountLogService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商家结算账单记录表删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        siteShopAccountLogService.del(id);
        return Result.success();
    }

}
