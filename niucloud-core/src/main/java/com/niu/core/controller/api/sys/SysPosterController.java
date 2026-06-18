package com.niu.core.controller.api.sys;

import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestParamUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.service.core.poster.param.GetPosterParam;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 海报控制器
 */
@RestController
@RequestMapping("api/poster")
public class SysPosterController {

    @Resource
    private ICorePosterService corePosterService;

    @GetMapping("")
    public Result<?> poster(HttpServletRequest request) {
        GetPosterParam param = RequestParamUtils.buildGetPosterParam(request);
        param.setSiteId(RequestUtils.siteId());
        param.setChannel(RequestUtils.channel());
        return Result.success("", corePosterService.get(param));
    }

}
