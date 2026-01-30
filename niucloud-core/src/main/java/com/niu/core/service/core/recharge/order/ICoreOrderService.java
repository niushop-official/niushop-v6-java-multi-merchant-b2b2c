package com.niu.core.service.core.recharge.order;

import com.niu.core.entity.recharge.RechargeOrder;

/**
 * 充值订单流程
 */
public interface ICoreOrderService {

    /**
     * 获取订单信息
     *
     * @param orderId 订单id
     */
    RechargeOrder orderInfo(Integer orderId);

    /**
     * 通过支付流水号获取订单信息
     *
     * @param outTradeNo 订单id
     */
    RechargeOrder orderInfoByOutTradeNo(String outTradeNo);
}

