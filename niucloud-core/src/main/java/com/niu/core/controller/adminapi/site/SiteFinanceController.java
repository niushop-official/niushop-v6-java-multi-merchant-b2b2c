package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.site.ISiteFinanceLogService;
import com.niu.core.service.admin.site.param.SiteFinanceLogParam;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("adminapi/site/finance")
@SaCheckLogin
public class SiteFinanceController {

    @Resource
    private ISiteFinanceLogService siteFinanceLogService;

    /**
     * 账单列表
     * @return Result
     */
    @GetMapping("")
    public Result<?> list(@Validated PageParam pageParam, SiteFinanceLogParam siteFinanceLogParam) {
        return Result.success(siteFinanceLogService.getPage(pageParam,siteFinanceLogParam));
    }
}
