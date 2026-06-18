package com.niu.mall.controller.adminapi.stat;

import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.stat.IAdminTradeStatService;
import com.niu.mall.service.core.stat.param.TradeStatParam;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("adminapi/mall/admin/trade/stat")
public class AdminTradeStatController {

    @Resource
    private IAdminTradeStatService tradeStatService;

    /**
     * 获取基础统计数据
     */
    @GetMapping("/basic")
    public Result<?> getBasicData(@Validated TradeStatParam param) {
        return Result.success(tradeStatService.getBasicData(param));
    }


    /**
     * 获取趋势分析数据
     */
    @GetMapping("/trend")
    public Result<?> getTrendData(@Validated TradeStatParam param) {
        return Result.success(tradeStatService.getTrendData(param));
    }

    /**
     * 交易转化数据
     */
    @GetMapping("/conversion")
    public Result<?> getSourceData(@Validated TradeStatParam param) {
        return Result.success(tradeStatService.getConversionData(param));
    }

    /**
     * 付款金额渠道
     */
    @GetMapping("/channel")
    public Result<?> getChannelData(@Validated TradeStatParam param) {
        return Result.success(tradeStatService.getChannelData(param));
    }

    /**
     * 成交客户构成
     */
    @GetMapping("/dealMember")
    public Result<?> getDealMemberData(@Validated TradeStatParam param) {
        return Result.success(tradeStatService.getDurationData(param));
    }
}
