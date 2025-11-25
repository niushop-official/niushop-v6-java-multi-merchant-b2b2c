package com.niu.core.controller.adminapi.shop;

import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.member.param.CashOutConfigParam;
import com.niu.core.service.admin.member.vo.CashOutConfigVo;
import com.niu.core.service.core.shop.ICoreShopConfigService;
import com.niu.core.service.core.shop.param.ShopCashOutConfigParam;
import com.niu.core.service.core.shop.vo.ShopCashOutConfigVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("adminapi/shop/admin/shop/config")
public class SiteShopConfigController {

    @Resource
    ICoreShopConfigService memberConfigService;

    /**
     *获取店铺提现设置
     * @return
     */
    @GetMapping("/cash_out")
    public Result<ShopCashOutConfigVo> getCashOutConfig() {
        ShopCashOutConfigVo config = memberConfigService.getCashOutConfig();
        return Result.success(config);
    }

    /**
     * 获取店铺体现设置
     * @param configParam
     * @return
     */
    @PostMapping("/cash_out")
    public Result setCashOutConfig(@Validated @RequestBody ShopCashOutConfigParam configParam) {
        memberConfigService.setCashOutConfig(configParam);
        return Result.success();
    }
}
