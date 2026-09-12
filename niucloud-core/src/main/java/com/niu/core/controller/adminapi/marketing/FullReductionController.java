package com.niu.core.controller.adminapi.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.marketing.IFullReductionService;
import com.niu.core.service.admin.marketing.param.FullReductionSearchParam;
import com.niu.core.service.admin.marketing.vo.FullReductionInfoVo;
import com.niu.core.service.admin.marketing.vo.FullReductionInitVo;
import com.niu.core.service.admin.marketing.vo.FullReductionListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 平台端满减活动控制器
 * <p>
 * 路径对齐 PHP：adminapi/marketing/admin/full_reduction
 * （由 mall/admin 迁至 marketing/admin）
 */
@RestController
@RequestMapping("adminapi/marketing/admin/full_reduction")
public class FullReductionController {

    @Resource
    private IFullReductionService fullReductionService;

    /**
     * 获取满减活动分页列表
     * GET marketing/admin/full_reduction/list
     */
    @GetMapping("/list")
    public Result<PageResult<FullReductionListVo>> list(@Validated PageParam pageParam,
                                                        @Validated FullReductionSearchParam searchParam) {
        PageResult<FullReductionListVo> list = fullReductionService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取满减活动详情
     * GET marketing/admin/full_reduction/info/{id}
     */
    @GetMapping("/info/{id}")
    public Result<FullReductionInfoVo> info(@Validated @PathVariable("id") Integer id) {
        FullReductionInfoVo info = fullReductionService.info(id);
        return Result.success(info);
    }

    /**
     * 获取满减活动初始化数据
     * GET marketing/admin/full_reduction/init
     */
    @GetMapping("/init")
    public Result<FullReductionInitVo> init() {
        FullReductionInitVo initVo = fullReductionService.init();
        return Result.success(initVo);
    }
}
