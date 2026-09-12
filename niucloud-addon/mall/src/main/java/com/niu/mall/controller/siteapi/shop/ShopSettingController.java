package com.niu.mall.controller.siteapi.shop;

import com.niu.core.common.domain.Result;
import com.niu.mall.service.site.shop.IShopSettingService;
import com.niu.mall.service.site.shop.param.ShopSettingParam;
import com.niu.mall.service.site.shop.vo.ShopSettingInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("adminapi/mall/site/shop/setting")
@Api(tags = "站点端-店铺设置")
public class ShopSettingController {

    @Autowired
    private IShopSettingService shopSettingService;

    @GetMapping
    @ApiOperation("获取店铺基本信息")
    public Result<ShopSettingInfoVo> getBasicInfo() {
        return Result.success(shopSettingService.getBasicInfo());
    }

    @PutMapping
    @ApiOperation("修改店铺基本信息")
    public Result setBasicInfo(@Valid @RequestBody ShopSettingParam param) {
        shopSettingService.setBasicInfo(param);
        return Result.success();
    }
}