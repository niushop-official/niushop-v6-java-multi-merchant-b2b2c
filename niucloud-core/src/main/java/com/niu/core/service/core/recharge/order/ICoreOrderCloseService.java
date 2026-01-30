package com.niu.core.service.core.recharge.order;


/**
 * 订单关闭
 */
public interface ICoreOrderCloseService {

    /**
     * 订单关闭
     *
     * @param orderId 订单ID
     * @return 订单关闭结果
     */
    Boolean close(Integer orderId);

}
