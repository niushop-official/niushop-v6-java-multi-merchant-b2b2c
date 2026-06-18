package com.niu.mall.service.admin.stat;

import com.niu.mall.service.core.stat.param.TradeStatParam;

import java.util.List;
import java.util.Map;

public interface IAdminTradeStatService {

    /**
     * 获取基础统计数据
     *
     * @param param 包含 start_time, end_time（可选）
     * @return 统计结果 Map
     */
    Map<String, Object> getBasicData(TradeStatParam param);

    /**
     * 获取趋势统计数据（按天）
     *
     * @param param 包含 start_time, end_time（可选）
     * @return 趋势数据
     */
    Map<String, Object> getTrendData(TradeStatParam param);

    /**
     * 获取交易转化数据（含环比）
     *
     * @param param 包含 start_time, end_time（可选）
     * @return 转化数据
     */
    Map<String, Object> getConversionData(TradeStatParam param);

    /**
     * 付款金额渠道统计
     *
     * @param param 包含 start_time, end_time（可选）
     * @return 渠道数据列表
     */
    List<Map<String, Object>> getChannelData(TradeStatParam param);

    /**
     * 成交客户构成（按会员等级）
     *
     * @param param 包含 start_time, end_time（可选）
     * @return 等级统计数据列表
     */
    List<Map<String, Object>> getDurationData(TradeStatParam param);

}
