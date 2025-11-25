package com.niu.core.controller.siteapi.shop;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.shop.ISiteShopService;
import com.niu.core.service.admin.shop.param.*;
import com.niu.core.service.admin.shop.vo.SiteShopInShopVo;
import com.niu.core.service.admin.shop.vo.SiteShopInfoVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.site.shop.ISiteShopSettingService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("adminapi/shop/site")
@SaCheckLogin
public class SiteShopSettingController {

    @Resource
    ISiteShopService siteShopService;

    @Resource
    ISiteShopSettingService siteShopSettingService;

    /**
     * 店铺扩展表详情
     *
     * @return Result<SiteShopInfoVo>
     */
    @GetMapping("/setting")
    public Result<SiteShopInfoVo> getBasicInfo() {
        return Result.success(siteShopSettingService.getBasicInfo());
    }


    /**
     * 店铺扩展表详情
     * @return
     */
    @GetMapping("/setting/get_shop")
    public Result<Object> getShopInfo() {
        return Result.success(siteShopSettingService.getShopInfo());
    }

    @PutMapping("/setting/set")
    public Result<Object> set(@Validated @RequestBody SiteShopBasicInfoParam siteShopParam) {
        siteShopSettingService.setBasicInfo(siteShopParam);
        return Result.success();
    }

    /**
     * 店铺扩展表详情
     * @return
     */
    @GetMapping("/shop/account/info")
    public Result<SiteShopInShopVo> accountInfo() {
        return Result.success(siteShopSettingService.getShopAccountInfo());
    }


    @PutMapping("/shop/account/set")
    public Result<Object> editShopAccount(@Validated @RequestBody SiteShopAccountInfoParam siteShopAccountInfoParam) {
        siteShopSettingService.editShopAccount(siteShopAccountInfoParam);
        return Result.success();
    }



}
