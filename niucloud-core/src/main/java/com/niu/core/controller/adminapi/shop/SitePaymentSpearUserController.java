package com.niu.core.controller.adminapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.mall_huifu.ParamTypeEnum;
import com.niu.core.enums.site_payment.SitePaymentSettleEnum;
import com.niu.core.enums.site_payment.SitePaymentStatusEnum;
import com.niu.core.enums.sys.SeparTypeEnum;
import com.niu.core.service.admin.shop.ISitePaymentService;
import com.niu.core.service.admin.shop.param.SitePaymentAEParam;
import com.niu.core.service.admin.shop.param.SitePaymentSearchParam;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("adminapi/shop/admin/spear_user")
public class SitePaymentSpearUserController {

    @Resource
    private ISitePaymentService sitePaymentService;

    /**
     * 获取平台分账账户列表
     *
     * @return
     */
    @GetMapping("")
    public Result getList(@Validated PageParam pageParam,
                          SitePaymentSearchParam param,
                          @RequestParam(value = "create_time[]", required = false)
                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime[] allParam) {
        return Result.success(sitePaymentService.getPage(pageParam, param, allParam));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable("id") Integer id) {
        return Result.success(sitePaymentService.getDetail(id));
    }

    @PostMapping("")
    public Result<?> add(@RequestBody SitePaymentAEParam param) {
        param.setType(ParamTypeEnum.HUIFU.getStatus());
        return Result.success(sitePaymentService.addByHandle(param));
    }

    @PutMapping("")
    public Result<?> edit(@RequestBody SitePaymentAEParam param) {
        return Result.success(sitePaymentService.editByHandle(param));
    }

    @GetMapping("/status")
    public Result<?> status() {
        return Result.success(SitePaymentStatusEnum.getMap());
    }

    @GetMapping("/type")
    public Result<?> type() {
        return Result.success(SeparTypeEnum.getMap());
    }

    @GetMapping("/settle")
    public Result<?> settle() {
        return Result.success(SitePaymentSettleEnum.getStatusAndNameCMap());
    }
}
