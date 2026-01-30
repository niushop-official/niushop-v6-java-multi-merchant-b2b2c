package com.niu.core.controller.api.sys;

import com.niu.core.common.domain.Result;
import com.niu.core.service.api.sys.ISysAreaService;
import com.niu.core.service.api.sys.ISysFabService;
import com.niu.core.service.api.sys.param.SysAreaAddressByLatlngParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fab")
@Slf4j
public class SysFabController {

    @Resource
    private ISysFabService sysFabService;

    /**
     * 获取悬浮按钮配置
     * @return
     */
    @GetMapping("/navs")
    public Result<?> getFabNavs() {
        return Result.success(sysFabService.getFabNavs());
    }
}
