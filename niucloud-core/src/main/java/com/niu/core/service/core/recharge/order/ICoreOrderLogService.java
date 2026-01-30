package com.niu.core.service.core.recharge.order;

import com.niu.core.service.core.recharge.order.param.RechargeOrderLogParam;

import java.util.List;

/**
 * 订单日志服务层
 */
public interface ICoreOrderLogService {

    /**
     * 订单日志
     *
     * @param param
     */
    void add(RechargeOrderLogParam param);

    /**
     * 批量订单日志
     *
     * @param param
     */
    void addAll(List<RechargeOrderLogParam> param);

}

