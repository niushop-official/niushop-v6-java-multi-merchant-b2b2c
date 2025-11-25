package com.niu.mall.service.core.order;

import com.niu.mall.service.core.order.param.OrderPayParam;

public interface ICoreOrderPayService {


    /**
     * 订单支付成功
     *
     * @param param
     */
    void pay(OrderPayParam param);

    /**
     * 订单支付失败
     *
     * @param param
     */
    void payOfGroup(OrderPayParam param);
}
