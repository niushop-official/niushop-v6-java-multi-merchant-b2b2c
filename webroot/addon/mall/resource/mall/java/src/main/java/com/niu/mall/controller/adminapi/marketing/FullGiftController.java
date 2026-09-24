package com.niu.mall.controller.adminapi.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.marketing.IFullGiftService;
import com.niu.mall.service.admin.marketing.param.FullGiftSearchParam;
import com.niu.mall.service.admin.marketing.vo.FullGiftInfoVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftInitVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftListVo;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 管理后台满赠活动控制器
 * <p>
 * 提供管理后台满赠活动的查询功能
 */
@RestController
@RequestMapping("adminapi/mall/admin/full_gift")
public class FullGiftController {

    @Resource
    private IFullGiftService fullGiftService;

    /**
     * 获取满赠活动分页列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索条件（标题、状态、类型、商品类型、时间范围等）
     * @return Result<PageResult < FullGiftListVo>> 满赠活动列表
     */
    @GetMapping("/list")
    public Result<PageResult<FullGiftListVo>> list(@Validated PageParam pageParam,
                                                   @Validated FullGiftSearchParam searchParam,
                                                   @RequestParam(value = "create_time", required = false)
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime[] createTime) {
        if (createTime != null && createTime.length == 2) {
            searchParam.setStartTime(createTime[0].atZone(ZoneId.of("Asia/Shanghai")).toEpochSecond());
            searchParam.setEndTime(createTime[1].atZone(ZoneId.of("Asia/Shanghai")).toEpochSecond());
        }
        PageResult<FullGiftListVo> list = fullGiftService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取满赠活动详情
     *
     * @param id 满赠活动ID
     * @return Result<FullGiftInfoVo> 满赠活动详情信息
     */
    @GetMapping("/info/{id}")
    public Result<FullGiftInfoVo> info(@Validated @PathVariable("id") Integer id) {
        FullGiftInfoVo info = fullGiftService.info(id);
        return Result.success(info);
    }

    /**
     * 获取满赠活动初始化数据
     * <p>
     * 返回满赠活动所需的枚举数据，包括状态、商品类型、规则类型、会员类型等
     *
     * @return Result<FullGiftInitVo> 初始化数据
     */
    @GetMapping("/init")
    public Result<FullGiftInitVo> init() {
        FullGiftInitVo initVo = fullGiftService.init();
        return Result.success(initVo);
    }

}