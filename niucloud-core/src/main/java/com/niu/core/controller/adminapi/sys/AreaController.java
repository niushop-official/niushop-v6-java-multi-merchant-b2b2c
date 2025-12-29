package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.sys.ISysAreaService;
import com.niu.core.service.api.sys.param.SysAreaAddressByLatlngParam;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 地址控制器
 */

@RestController
@RequestMapping("adminapi/area")
@SaCheckLogin
public class AreaController {

    @Resource
    ISysAreaService sysAreaService;

    /**
     * 通过经纬度查询地址
     *
     * @param param
     * @return
     */
    @GetMapping("/address_by_latlng")
    public Result<?> getAddressByLatlng(SysAreaAddressByLatlngParam param) {
        return Result.success(sysAreaService.getAddressByLatlng(param));
    }
}
