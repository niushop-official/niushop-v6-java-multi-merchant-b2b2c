package com.niu.mall.service.site.stat;

import com.niu.mall.service.core.stat.param.TradeStatParam;
import com.niu.mall.service.core.stat.vo.BasicDataVo;
import com.niu.mall.service.core.stat.vo.ConversionDataVo;
import com.niu.mall.service.core.stat.vo.DealMemberDataVo;
import com.niu.mall.service.core.stat.vo.TrendDataVo;

import java.util.List;
import java.util.Map;

public interface ISiteTradeStatService {
    BasicDataVo getBasicData(TradeStatParam param);

    TrendDataVo getTrendData(TradeStatParam param);

    ConversionDataVo getConversionData(TradeStatParam param);

    List<Map<String, Object>> getChannelData(TradeStatParam param);

    List<DealMemberDataVo> getDealMemberData(TradeStatParam param);
}