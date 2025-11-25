package com.niu.core.service.admin.stat;

import java.math.BigDecimal;
import java.util.Map;

public interface IFinanceStatService {
    /**
     * 获取收款统计数据
     * @return 统计数据
     */
    Map<String, BigDecimal> getPayStat();

    /**
     * 获取结算统计数据
     * @return 统计数据
     */
    Map<String, BigDecimal> getSettlementStat();

    /**
     * 获取会员统计数据
     * @return 统计数据
     */
    Map<String, BigDecimal> getMemberStat();

    /**
     * 账单总数统计
     * @param where 查询条件
     * @return 统计数据
     */
    Map<String, BigDecimal> getAccountsTotal(Map<String, Object> where);

    /**
     * 账单列表统计
     * @param where 查询条件
     * @return 统计数据
     */
    Map<String, BigDecimal> getAccountsList(Map<String, Object> where);
}