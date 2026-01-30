package com.niu.mall.service.core.order;

import com.niu.mall.entity.order.MallOrder;

/**
 * 订单结算服务接口
 */
public interface ICoreOrderSettlementService {

    /**
     * 订单结算
     * @param order 订单信息
     */
    void settlement(MallOrder order);

    /**
     * 设置结算状态为处理中
     * @param order 订单信息
     */
    void dealing(MallOrder order);

    /**
     * 设置结算状态为已结算
     * @param order 订单信息
     */
    void success(MallOrder order);
}

