package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.site.ISiteQrcodeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 店铺二维码
 * 对应 PHP: app\adminapi\controller\site\SiteQrcode
 */
@RestController
@RequestMapping("adminapi/site/qrcode")
@SaCheckLogin
public class SiteQrcodeController {

    @Resource
    private ISiteQrcodeService siteQrcodeService;

    @GetMapping("/list")
    public Result<List<Map<String, String>>> lists() {
        return Result.success(siteQrcodeService.getList());
    }

    @GetMapping("/config")
    public Result<Map<String, Object>> getSiteQrcodeConfig(@RequestParam(required = false) String type) {
        return Result.success(siteQrcodeService.getSiteQrcodeConfig(type));
    }

    @PutMapping("/config")
    public Result<?> setSiteQrcodeConfig(@RequestBody Map<String, Object> body) {
        Object value = body.get("value");
        String type = body.get("type") != null ? body.get("type").toString() : "";
        @SuppressWarnings("unchecked")
        Map<String, Object> configValue = value instanceof Map ? (Map<String, Object>) value : Map.of();
        siteQrcodeService.setSiteQrcodeConfig(configValue, type);
        return Result.success();
    }
}
