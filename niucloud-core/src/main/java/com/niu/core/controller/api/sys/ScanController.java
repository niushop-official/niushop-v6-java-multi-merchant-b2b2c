package com.niu.core.controller.api.sys;

import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.system.CaptchaUtils;
import com.niu.core.service.api.sys.IScanService;
import com.niu.core.service.api.sys.param.SysCheckScanParam;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ScanController {

    @Resource
    private IScanService scanService;
    @GetMapping("/checkscan")
    public Result<?> checkScan(SysCheckScanParam param) {
        return Result.success(scanService.checkScan(param));
    }

}
