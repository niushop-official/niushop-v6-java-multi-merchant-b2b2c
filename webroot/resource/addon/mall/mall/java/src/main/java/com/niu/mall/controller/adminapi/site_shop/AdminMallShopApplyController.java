package com.niu.mall.controller.adminapi.site_shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.site_shop.IAdminShopApplyService;
import com.niu.mall.service.admin.site_shop.param.AdminShopApplyPageQueryParam;
import com.niu.mall.service.admin.site_shop.param.AdminShopApplyVerifyParam;
import com.niu.mall.service.admin.site_shop.vo.AdminShopApplyInfoVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopApplyPageVo;
import com.niu.mall.service.core.shop.vo.ShopApplyConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("adminapi/mall/admin/shop_apply")
@Api(tags = "平台端-店铺入驻申请")
public class AdminMallShopApplyController {

    @Autowired
    private IAdminShopApplyService shopApplyService;

    @GetMapping
    @ApiOperation("店铺申请分页列表")
    public Result<PageResult<AdminShopApplyPageVo>> pages(PageParam pageParam, AdminShopApplyPageQueryParam queryParam) {
        return Result.success(shopApplyService.getPage(pageParam, queryParam));
    }

    @GetMapping("/{id}")
    @ApiOperation("店铺申请详情")
    public Result<AdminShopApplyInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(shopApplyService.getInfo(id));
    }

    @PutMapping("/verify/{id}")
    @ApiOperation("店铺申请审核")
    public Result verify(@PathVariable("id") Integer id, @Valid @RequestBody AdminShopApplyVerifyParam param) {
        shopApplyService.verify(id, param);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除店铺申请")
    public Result del(@PathVariable("id") Integer id) {
        shopApplyService.del(id);
        return Result.success();
    }

    @GetMapping("/statuslist")
    @ApiOperation("店铺申请状态列表")
    public Result<Map<String, String>> getStatusList() {
        return Result.success(shopApplyService.getStatusList());
    }

    @GetMapping("/config")
    @ApiOperation("获取店铺入驻配置")
    public Result<ShopApplyConfigVo> getShopApplyConfig() {
        return Result.success(shopApplyService.getShopApplyConfig());
    }

    @PostMapping("/config")
    @ApiOperation("更新店铺入驻配置")
    public Result setShopApplyConfig(@Valid @RequestBody ShopApplyConfigVo param) {
        shopApplyService.setShopApplyConfig(param);
        return Result.success();
    }
}
