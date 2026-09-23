package com.niu.mall.controller.api.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.core.site.apply.vo.AccountCheckVo;
import com.niu.mall.service.api.shop.IApiShopApplyService;
import com.niu.mall.service.api.shop.param.AccountCheckParam;
import com.niu.mall.service.api.shop.param.ShopApplyAddParam;
import com.niu.mall.service.api.shop.param.ShopApplyListParam;
import com.niu.mall.service.api.shop.vo.ShopApplyInfoVo;
import com.niu.mall.service.api.shop.vo.ShopApplyPageVo;
import com.niu.mall.service.core.shop.vo.ShopApplyConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/mall/shop_apply")
@Api(tags = "前台-店铺入驻申请")
public class    ApiShopApplyController {

    @Autowired
    private IApiShopApplyService shopApplyService;

    @GetMapping
    @ApiOperation("店铺申请列表")
    public Result<PageResult<ShopApplyPageVo>> lists(PageParam pageParam, ShopApplyListParam param) {
        return Result.success(shopApplyService.getPage(pageParam, param));
    }

    @GetMapping("/{id}")
    @ApiOperation("店铺申请详情")
    public Result<ShopApplyInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(shopApplyService.getInfo(id));
    }

    @GetMapping("/account_check")
    @ApiOperation("店铺账号检测")
    public Result<AccountCheckVo> accountCheck(@Valid AccountCheckParam param) {
        return Result.success(shopApplyService.accountCheck(param.getAccount()));
    }

    @PostMapping
    @ApiOperation("添加店铺申请")
    public Result<Map<String, Integer>> add(@Valid @RequestBody ShopApplyAddParam param) {
        Integer applyId = shopApplyService.add(param);
        return Result.success(Map.of("id", applyId));
    }

    @PutMapping("/{id}")
    @ApiOperation("编辑店铺申请")
    public Result edit(@PathVariable("id") Integer id, @Valid @RequestBody ShopApplyAddParam param) {
        shopApplyService.edit(id, param);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("店铺申请状态列表")
    public Result<List<Map<String, String>>> statusList() {
        return Result.success(shopApplyService.getStatusList());
    }

    @GetMapping("/config")
    @ApiOperation("获取商户入驻设置")
    public Result<ShopApplyConfigVo> getShopApplyConfig() {
        return Result.success(shopApplyService.getShopApplyConfig());
    }
}
