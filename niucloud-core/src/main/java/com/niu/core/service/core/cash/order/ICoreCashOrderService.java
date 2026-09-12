package com.niu.core.service.core.cash.order;

import com.niu.core.entity.cash.CashOrder;

public interface ICoreCashOrderService {

    /**
     * 获取订单信息
     * @param orderId 订单ID
     * @return 订单信息
     */
    CashOrder orderInfo(Integer orderId);

    /**
     * 通过支付流水号获取订单信息
     * @param outTradeNo 支付流水号
     * @return 订单信息
     */
    CashOrder orderInfoByOutTradeNo(String outTradeNo);
}
