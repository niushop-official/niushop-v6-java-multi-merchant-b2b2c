package com.niu.core.service.core.recharge.finance;

import com.niu.core.service.core.recharge.finance.param.OrderSeparateParam;
import com.niu.core.service.core.recharge.finance.param.OrderPlatParam;
/**
 * 充值资金流水服务层
 */
public interface ICorePlatService {

    /**
     * 平台收入（订单分账结算）
     * @param orderSeparateParam
     */
    void platIncomeOrderSeparate(OrderSeparateParam orderSeparateParam);

    /**
     * 平台收入订单支付
     * @param orderPlatParam
     */
    void platIncomeOrderPay(OrderPlatParam orderPlatParam);

}
