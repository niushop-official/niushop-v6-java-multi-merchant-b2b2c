package com.niu.core.service.core.recharge.refund;

public interface ICoreRechargeRefundService {

    /**
     * 发起退款
     *
     * @param orderId
     */
    void create(Integer orderId);

    /**
     * 退款完成
     *
     * @param refundNo
     */
    void refundComplete(String refundNo);
}
