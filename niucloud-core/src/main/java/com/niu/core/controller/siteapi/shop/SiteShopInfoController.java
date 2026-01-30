package com.niu.core.controller.siteapi.shop;

import com.niu.core.common.domain.Result;
import com.niu.core.service.site.shop.ISiteShopAccountLogService;
import com.niu.core.service.site.shop.ISiteShopInfoService;
import com.niu.core.service.site.shop.param.SiteShopBusinessStatusParam;
import com.niu.core.service.site.shop.vo.SiteShopInfoVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺扩展表控制器
 */

@RestController
@RequestMapping("adminapi/shop/site/shop")
public class SiteShopInfoController {

    @Resource
    ISiteShopInfoService siteShopInfoService;
    @Resource
    ISiteShopAccountLogService siteShopAccountLogService;

    /**
     * 店铺信息
     *
     * @param id 主键ID
     * @return Result<SiteShopInfoVo>
     */
    @GetMapping("/info/{id}")
    public Result<SiteShopInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(siteShopInfoService.getShopInfo(id));
    }

    /**
     * 店铺扩展表编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/business_status")
    public Result<Object> edit(@Validated @RequestBody SiteShopBusinessStatusParam editParam) {
        siteShopInfoService.editBusinessStatus(editParam.getBusinessStatus());
        return Result.success();
    }

    /**
     * 店铺扩展表编辑
     *
     * @return Result<Object>
     */
    @GetMapping("/income_of_range/{range}")
    public Result edit(@PathVariable("range") String range) {
        return Result.success(siteShopAccountLogService.getIncomeByDateRange(range));
    }


}
