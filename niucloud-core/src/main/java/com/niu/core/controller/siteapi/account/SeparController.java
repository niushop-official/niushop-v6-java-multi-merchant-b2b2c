package com.niu.core.controller.siteapi.account;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.account.SeparStatusEnum;
import com.niu.core.service.site.account.ISeparService;
import com.niu.core.service.site.account.param.SeparSearchParam;
import com.niu.core.service.site.account.vo.SeparDetailVo;
import com.niu.core.service.site.account.vo.SeparInfoVo;
import com.niu.core.service.site.account.vo.SeparListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("adminapi/spear/site")
public class SeparController {

    @Resource
    ISeparService separService;

    /**
     * 获取分账列表
     *
     * @return
     */
    @GetMapping("")
    public Result<PageResult<SeparListVo>> list(PageParam pageParam, SeparSearchParam searchParam) {
        return Result.success(separService.page(pageParam, searchParam));
    }

    /**
     * 获取分账详情
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    public Result<List<SeparDetailVo>> detail(@PathVariable("id") Integer id) {
        return Result.success(separService.detail(id));
    }
}
