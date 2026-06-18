package com.niu.core.controller.siteapi.shop;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.core.shop.param.ShopMemberConfigParam;
import com.niu.core.service.core.shop.vo.ShopMemberConfigVo;
import com.niu.core.service.site.shop.ISiteShopMemberConfigService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("adminapi/shop/site/member/config")
@SaCheckLogin
public class SiteShopMemberConfigController {

    @Resource
    ISiteShopMemberConfigService siteShopMemberConfigService;

    /**
     * 获取店铺会员配置
     */
    @GetMapping("")
    public Result<ShopMemberConfigVo> getMemberConfig() {
        return Result.success(siteShopMemberConfigService.getMemberConfig());
    }

    /**
     * 设置店铺会员配置
     */
    @PostMapping("")
    public Result<Object> setMemberConfig(@Validated @RequestBody ShopMemberConfigParam param) {
        siteShopMemberConfigService.setMemberConfig(param);
        return Result.success();
    }
}
