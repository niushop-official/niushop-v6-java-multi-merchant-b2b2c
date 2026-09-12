package com.niu.core.controller.api.marketing;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.marketing.INewcomerGiftRecordsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marketing/newcomer_gift")
public class NewcomerGiftRecordsController {

    @Resource
    private INewcomerGiftRecordsService newcomerGiftRecordsService;

    @SaCheckLogin
    @GetMapping("/records")
    public Result<?> myRecords() {
        return Result.success(newcomerGiftRecordsService.getMyRecords());
    }
}
