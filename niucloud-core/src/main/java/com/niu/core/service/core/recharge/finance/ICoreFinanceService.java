package com.niu.core.service.core.recharge.finance;


import com.niu.core.entity.recharge.RechargeOrder;

/**
 * 充值资金流水服务层
 */
public interface ICoreFinanceService {

    /**
     * 订单完成
     *
     * @param order 订单
     * @return 订单关闭结果
     */
    Boolean orderPay(RechargeOrder order);

    /**
     * 订单完成
     *
     * @param order 订单
     * @return 订单关闭结果
     */
    Boolean orderFinish(RechargeOrder order);

}
