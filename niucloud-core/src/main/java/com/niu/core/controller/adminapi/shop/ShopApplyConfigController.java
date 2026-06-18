package com.niu.core.controller.adminapi.shop;

import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.shop.IShopConfigService;
import com.niu.core.service.core.shop.param.ShopApplyConfigParam;
import com.niu.core.service.core.shop.vo.ShopApplyConfigInfoVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("adminapi/shop/admin/shop/apply_config")
public class ShopApplyConfigController {


    @Resource
    private IShopConfigService shopConfigService;

    @GetMapping("")
    public Result<ShopApplyConfigInfoVo> getShopApplyConfig() {
        var info = shopConfigService.getShopApplyConfig();
        return Result.success(info);
    }

    @PostMapping("")
    public Result<Object> setShopApplyConfig(@Validated @RequestBody ShopApplyConfigParam addParam) {
        shopConfigService.setShopApplyConfig(addParam);
        return Result.success();
    }
}
