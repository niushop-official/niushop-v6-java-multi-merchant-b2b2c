package com.niu.core.service.core.recharge.order;


/**
 * 充值订单流程
 */
public interface ICoreOrderFinishService {

    /**
     * 完成
     * @param orderId 订单ID
     */
    void finish(Integer orderId);

}
