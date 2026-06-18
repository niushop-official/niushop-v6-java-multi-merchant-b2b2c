package com.niu.mall.controller.siteapi.stat;

import com.niu.core.common.domain.Result;
import com.niu.mall.service.core.stat.param.TradeStatParam;
import com.niu.mall.service.core.stat.vo.BasicDataVo;
import com.niu.mall.service.core.stat.vo.ConversionDataVo;
import com.niu.mall.service.core.stat.vo.DealMemberDataVo;
import com.niu.mall.service.core.stat.vo.TrendDataVo;
import com.niu.mall.service.site.stat.ISiteTradeStatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("adminapi/mall/site/trade/stat")
@Api(tags = "站点交易统计")
public class SiteTradeStatController {

    @Autowired
    private ISiteTradeStatService tradeStatService;

    @GetMapping("/basic")
    @ApiOperation("基础统计数据")
    public Result<BasicDataVo> getBasicData(TradeStatParam param) {
        return Result.success(tradeStatService.getBasicData(param));
    }

    @GetMapping("/trend")
    @ApiOperation("趋势分析数据")
    public Result<TrendDataVo> getTrendData(TradeStatParam param) {
        return Result.success(tradeStatService.getTrendData(param));
    }

    @GetMapping("/conversion")
    @ApiOperation("交易转化数据")
    public Result<ConversionDataVo> getConversionData(TradeStatParam param) {
        return Result.success(tradeStatService.getConversionData(param));
    }

    @GetMapping("/channel")
    @ApiOperation("付款金额渠道分析")
    public Result<List<Map<String, Object>>> getChannelData(TradeStatParam param) {
        return Result.success(tradeStatService.getChannelData(param));
    }

    @GetMapping("/dealMember")
    @ApiOperation("成交客户构成")
    public Result<List<DealMemberDataVo>> getDealMemberData(TradeStatParam param) {
        return Result.success(tradeStatService.getDealMemberData(param));
    }
}