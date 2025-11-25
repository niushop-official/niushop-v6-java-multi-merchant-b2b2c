package com.niu.core.controller.api.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.web.IWebFriendlyLinkService;
import com.niu.core.service.admin.web.param.WebFriendlyLinkParam;
import com.niu.core.service.admin.web.param.WebFriendlyLinkSearchParam;
import com.niu.core.service.admin.web.param.WebFriendlyLinkSortParam;
import com.niu.core.service.admin.web.vo.WebFriendlyLinkInfoVo;
import com.niu.core.service.admin.web.vo.WebFriendlyLinkListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 友情链接控制器
 */

@RestController
@RequestMapping("api/web/friendly_link")
public class WebFriendlyLinkController {

    @Resource
    IWebFriendlyLinkService webFriendlyLinkService;


    /**
     *
     * @param searchParam 搜索条件
     * @return 友情链接列表
     */
    @GetMapping("")
    public Result<List<WebFriendlyLinkListVo>> page(@Validated WebFriendlyLinkSearchParam searchParam) {
        List<WebFriendlyLinkListVo> list = webFriendlyLinkService.list( searchParam);
        return Result.success(list);
    }

    /**
     *
     * @param id 主键ID
     * @return 友情链接详情
     */
    @GetMapping("/{id}")
    public Result<WebFriendlyLinkInfoVo> info(@PathVariable("id") Integer id) {
        WebFriendlyLinkInfoVo info = webFriendlyLinkService.info(id);
        return Result.success(info);
    }


}
