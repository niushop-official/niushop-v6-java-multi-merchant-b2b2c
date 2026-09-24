package com.niu.mall.controller.adminapi.stat;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.stat.IMemberStatService;
import com.niu.mall.service.admin.stat.param.MemberStatParam;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("adminapi/mall/admin/member/stat")
public class MemberStatController {

    @Resource
    private IMemberStatService memberStatService;

    /**
     * 获取基础统计数据
     */
    @GetMapping("/basic")
    public Result<?> getBasicData() {
        return Result.success(memberStatService.getBasicData());
    }

    /**
     * 获取会员统计数据（按时间范围）
     */
    @GetMapping("/data")
    public Result<?> getMemberData(@Validated MemberStatParam param) {
        // 参数默认值处理（空字符串）
        Map<String, String> params = Map.of(
                "start_time", param.getStartTime() == null ? "" : param.getStartTime(),
                "end_time", param.getEndTime() == null ? "" : param.getEndTime()
        );
        return Result.success(memberStatService.getMemberData(params));
    }

    /**
     * 获取趋势分析数据
     */
    @GetMapping("/trend")
    public Result<?> getTrendData(@Validated MemberStatParam param) {
        Map<String, String> params = Map.of(
                "start_time", param.getStartTime() == null ? "" : param.getStartTime(),
                "end_time", param.getEndTime() == null ? "" : param.getEndTime()
        );
        return Result.success(memberStatService.getTrendData(params));
    }

    /**
     * 获取来源分布数据
     */
    @GetMapping("/source")
    public Result<?> getSourceData() {
        return Result.success(memberStatService.getSourceData());
    }

    /**
     * 获取时长分布数据
     */
    @GetMapping("/duration")
    public Result<?> getDurationData() {
        return Result.success(memberStatService.getDurationData());
    }

    /**
     * 获取会员排行数据
     */
    @GetMapping("/ranking")
    public Result<?> getRankingData(@Validated PageParam pageParam,
                                    @Validated MemberStatParam param) {
        Map<String, String> params = Map.of(
                "start_time", param.getStartTime() == null ? "" : param.getStartTime(),
                "end_time", param.getEndTime() == null ? "" : param.getEndTime(),
                "keyword", param.getKeyword() == null ? "" : param.getKeyword(),
                "sort_field", param.getSortField()
        );

        return Result.success(memberStatService.getRankingData(pageParam, params));
    }
}
