package com.niu.core.controller.api.addon;

import com.niu.core.common.domain.Result;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/addon")
public class AddonController {

    @Resource
    ICoreAddonService coreAddonService;

    @GetMapping("/list/install")
    public Result<Map<String, InstallAddonListVo>> getInstallList() {
        return Result.success(coreAddonService.getInstallAddonList());
    }

    /**
     * 获取插件缓存
     *
     * @return
     */
    @GetMapping("/cache")
    public Result<?> cache() {
        return Result.success(coreAddonService.getAddonCache());
    }
}
