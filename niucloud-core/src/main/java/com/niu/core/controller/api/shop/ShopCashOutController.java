package com.niu.core.controller.api.shop;


import com.niu.core.common.domain.Result;
import com.niu.core.service.api.shop.ISiteShopCashOutService;
import com.niu.core.service.api.shop.vo.SiteShopCashOutInfoNewVo;
import com.niu.core.service.api.shop.vo.SiteShopCashOutInfoVo;
import com.niu.core.service.core.shop.param.ShopTransferParam;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 店铺转账
 */
@RestController
@RequestMapping("api/shop/web/cash_out")
public class ShopCashOutController {
    @Resource
    private ISiteShopCashOutService siteShopCashOutService;

    /**
     * 商家站点提现详情
     * @param id 主键ID
     * @return  SiteShopCashOutInfoVo
     */
    @GetMapping("/{id}")
    public Result<SiteShopCashOutInfoVo> component(@PathVariable("id") Integer id) {
        return Result.success(siteShopCashOutService.info(id));
    }

    /**
     * 转账校验
     * @return  SiteShopCashOutInfoVo
     */
    @GetMapping("/transfer/check")
    public Result check(@RequestParam("code") String code) {
        return Result.success(siteShopCashOutService.checkTransfer(code));
    }

    /**
     * 转账
     */
    @PostMapping("/transfer/{id}")
    public Result<Boolean> transfer(@PathVariable("id") Integer id, @Validated @RequestBody ShopTransferParam param) {
        siteShopCashOutService.transfer(id,param);
        return Result.success(true);
    }
}