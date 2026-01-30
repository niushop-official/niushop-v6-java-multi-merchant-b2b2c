package com.niu.core.controller.api.web;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.web.IWebConfigService;
import com.niu.core.service.admin.web.IWebNavService;
import com.niu.core.service.admin.web.param.WebNavParam;
import com.niu.core.service.admin.web.param.WebNavSearchParam;
import com.niu.core.service.admin.web.param.WebNavSortParam;
import com.niu.core.service.admin.web.vo.WebNavInfoVo;
import com.niu.core.service.admin.web.vo.WebNavListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PC导航管理控制器
 */

@RestController
@RequestMapping("api/web")
public class WebConfigController {

    @Resource
    IWebConfigService webConfigService;

    /**
     * 配置信息
     *
     */
    @GetMapping("/config")
    public Result config() {
        return Result.success(webConfigService.getWebConfig());
    }
}
