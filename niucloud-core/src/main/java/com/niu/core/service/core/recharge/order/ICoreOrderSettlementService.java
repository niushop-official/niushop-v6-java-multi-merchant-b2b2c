package com.niu.core.service.core.recharge.order;


import com.niu.core.entity.recharge.RechargeOrder;

/**
 * 充值订单结算服务层
 */
public interface ICoreOrderSettlementService {

    /**
     * 充值订单结算
     *
     * @param orderId 订单ID
     */
    void settlement(Integer orderId);

    void success(RechargeOrder orderId);

}
