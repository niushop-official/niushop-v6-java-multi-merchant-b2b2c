package com.niu.core.controller.adminapi.marketing;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.market.IMarketService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("adminapi/basic/admin/marketing")
@SaCheckLogin
public class MarketController {

    @Resource
    private IMarketService marketService;

    /**
     * getMemberContentType
     */
    @GetMapping("/type")
    public Result<?> contentType() {
        Map<String, Object> type = marketService.getMarketingType();
        return Result.success(type);
    }
}
