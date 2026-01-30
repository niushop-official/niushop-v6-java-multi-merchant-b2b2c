package com.niu.core.service.core.recharge.order;

/**
 * 充值订单流程
 */
public interface ICoreOrderPayService {

    /**
     *  订单支付
     *
     * @param tradeId 订单id
     * @param outTradeNo 外部订单号
     */
    void pay(Integer tradeId,String outTradeNo);

}

