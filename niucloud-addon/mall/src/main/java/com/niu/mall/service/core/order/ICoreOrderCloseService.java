package com.niu.mall.service.core.order;

import com.niu.mall.service.core.order.param.OrderCloseParam;
public interface ICoreOrderCloseService {

    /**
     * 订单关闭
     *
     * @param param
     */
    void close(OrderCloseParam param);

    /**
     * 校验是否全部退款
     *
     * @param orderId
     */
    void checkAllClose(Integer orderId);

    /**
     * 设置订单关闭超时时间
     *
     * @param orderId
     */
    void setOrderCloseTimeout(Integer orderId, Long timeout);

}
