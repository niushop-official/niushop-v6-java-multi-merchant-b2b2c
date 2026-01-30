package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.site.SiteFinanceLogEnum;
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

    /**
     * 获取最近10条的资金流水
     * @return Result
     */
    @GetMapping("latest")
    public Result<?> latest() {
        return Result.success(siteFinanceLogService.getLatest());
    }

    /**
     * 流水类型
     * @return Result
     */
    @GetMapping("type")
    public Result<?> type() {
        return Result.success(SiteFinanceLogEnum.getType());
    }

    /**
     * 业务类型
     * @return Result
     */
    @GetMapping("trade_type")
    public Result<?> tradeType() {
        return Result.success(SiteFinanceLogEnum.getTradeType());
    }

    /**
     * 收支类型
     * @return Result
     */
    @GetMapping("raeType")
    public Result<?> raeType() {
        return Result.success(SiteFinanceLogEnum.getRaeType());
    }
}
