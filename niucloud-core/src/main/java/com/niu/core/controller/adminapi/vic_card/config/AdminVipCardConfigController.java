package com.niu.core.controller.adminapi.vic_card.config;

import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.vip_card.config.IAdminVipCardConfigService;
import com.niu.core.service.core.vip_card.config.param.VipCardConfigParam;
import com.niu.core.service.core.vip_card.config.vo.VipCardConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("adminapi/vip_card/config")
@Api(tags = "会员卡系统配置")
public class AdminVipCardConfigController {

    @Autowired
    private IAdminVipCardConfigService configService;

    @GetMapping
    @ApiOperation("获取会员卡配置")
    public Result<VipCardConfigVo> getVipCardConfig() {
        return Result.success(configService.getVipCardConfig());
    }

    @PutMapping
    @ApiOperation("设置会员卡配置")
    public Result setVipCardConfig(@Valid @RequestBody VipCardConfigParam param) {
        configService.setVipCardConfig(param);
        return Result.success();
    }
}