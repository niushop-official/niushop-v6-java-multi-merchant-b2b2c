package com.niu.core.service.admin.stat;

import com.niu.core.service.admin.stat.vo.StartChartVo;

import java.util.Map;

/**
 * 站点统计服务接口
 */
public interface ISiteStatService {
    /**
     * 获取站点统计数据
     * @return 统计数据
     */
    Map<String, Object> getIndexData();

    /**
     * 订单金额
     * @param start_time 开始时间
     * @param end_time 结束时间
     * @return 订单金额
     */
    double orderMoney(long start_time, long end_time);

    /**
     * 订单数量
     * @param start_time 开始时间
     * @param end_time 结束时间
     * @return 订单数量
     * @throws Exception 数据库异常
     */
    int orderCount(long start_time, long end_time) throws Exception;

    /**
     * 待转账数量（微信零钱）
     * @return 待转账数量
     * @throws Exception 数据库异常
     */
    Map<String, Object> getTransferCount();

    /**
     * 站点已安装应用数量
     * @return 已安装应用数量
     */
    Map<String, Object> getAddonCount();

    /**
     * 首页获取数据集
     * @return
     */
    StartChartVo getStatData(Integer siteId, String startDate, String endDate);
}