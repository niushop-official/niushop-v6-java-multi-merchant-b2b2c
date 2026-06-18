package com.niu.mall.service.core.order;

import com.niu.mall.entity.order.MallOrder;
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

    /**
     * 订单支付成功
     *
     */
    void orderPayEvent(MallOrder order, String status);

    /**
     * 订单支付成功（含支付后完整处理）
     */
    void orderPayEvent(MallOrder order, String status, OrderPayParam param);
}
