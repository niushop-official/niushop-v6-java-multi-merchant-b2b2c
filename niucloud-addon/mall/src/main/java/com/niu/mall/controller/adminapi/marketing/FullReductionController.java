package com.niu.mall.controller.adminapi.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.marketing.IFullReductionService;
import com.niu.mall.service.admin.marketing.param.FullReductionSearchParam;
import com.niu.mall.service.admin.marketing.vo.FullReductionInfoVo;
import com.niu.mall.service.admin.marketing.vo.FullReductionInitVo;
import com.niu.mall.service.admin.marketing.vo.FullReductionListVo;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 管理后台满减活动控制器
 * <p>
 * 提供管理后台满减活动的查询功能
 */
@RestController
@RequestMapping("adminapi/mall/admin/full_reduction")
public class FullReductionController {

    @Resource
    private IFullReductionService fullReductionService;

    /**
     * 获取满减活动分页列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索条件（标题、状态、类型、商品类型、时间范围等）
     * @return Result<PageResult < FullReductionListVo>> 满减活动列表
     */
    @GetMapping("/list")
    public Result<PageResult<FullReductionListVo>> list(@Validated PageParam pageParam,
                                                        @Validated FullReductionSearchParam searchParam,
                                                        @RequestParam(value = "create_time", required = false)
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime[] createTime) {
        if (createTime != null && createTime.length == 2) {
            searchParam.setStartTime(createTime[0].atZone(ZoneId.of("Asia/Shanghai")).toEpochSecond());
            searchParam.setEndTime(createTime[1].atZone(ZoneId.of("Asia/Shanghai")).toEpochSecond());
        }
        PageResult<FullReductionListVo> list = fullReductionService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取满减活动详情
     *
     * @param id 满减活动ID
     * @return Result<FullReductionInfoVo> 满减活动详情信息
     */
    @GetMapping("/info/{id}")
    public Result<FullReductionInfoVo> info(@Validated @PathVariable("id") Integer id) {
        FullReductionInfoVo info = fullReductionService.info(id);
        return Result.success(info);
    }

    /**
     * 获取满减活动初始化数据
     * <p>
     * 返回满减活动所需的枚举数据，包括状态、类型、商品类型、规则类型、会员类型、优惠类型等
     *
     * @return Result<FullReductionInitVo> 初始化数据
     */
    @GetMapping("/init")
    public Result<FullReductionInitVo> init() {
        FullReductionInitVo initVo = fullReductionService.init();
        return Result.success(initVo);
    }

}