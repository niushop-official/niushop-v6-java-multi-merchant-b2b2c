package com.niu.core.service.core.recharge.order;

import com.niu.core.service.core.recharge.order.param.AfterOrderCloseParam;
import com.niu.core.service.core.recharge.order.param.AfterOrderCreateParam;
import com.niu.core.service.core.recharge.order.param.AfterOrderPayParam;

/**
 * 创建订单
 */
public interface ICoreOrderEventService {

    /**
     * 创建订单成功后 处理事件
     *
     * @param param
     */
    void AfterRechargeOrderCreate(AfterOrderCreateParam param);

    /**
     * 关闭订单成功后 处理事件
     *
     * @param param
     */
    void AfterRechargeOrderClose(AfterOrderCloseParam param);

    /**
     * 订单支付后操作 处理事件
     *
     * @param param
     */
    void AfterRechargeOrderPay(AfterOrderPayParam param);
}
