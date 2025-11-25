package com.niu.core.controller.adminapi.shop;

import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.shop.ISitePaymentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("adminapi/shop/admin/site_payment")
public class SitePaymentController {

    @Resource
    private ISitePaymentService sitePaymentService;

    /**
     * 获取平台分账账户列表
     * @return
     */
    @GetMapping("")
    public Result getList() {
        return Result.success(sitePaymentService.getList());
    }
}
