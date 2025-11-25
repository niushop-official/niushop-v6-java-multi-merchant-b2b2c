package com.niu.core.controller.siteapi.shop;


import com.niu.core.common.domain.Result;
import com.niu.core.service.site.shop.ISiteShopPaymentService;
import com.niu.core.service.site.shop.param.SiteShopPaymentParam;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("adminapi/shop/site/site_payment")
public class SitePaymentController {
    @Resource
    ISiteShopPaymentService   siteShopPaymentService;
    @GetMapping("")
    public Result getList() {
        return Result.success(siteShopPaymentService.getList());
    }

    @PutMapping("status/{id}")
    public Result<Object> changeStatus( @PathVariable("id") Integer id, @Validated @RequestBody SiteShopPaymentParam sitePaymentParam) {
        siteShopPaymentService.editStatus(id,sitePaymentParam);
    return Result.success();
    }

}
