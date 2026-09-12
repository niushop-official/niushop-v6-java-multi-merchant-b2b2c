package com.niu.core.service.core.vip_card.order;

import java.util.List;

public interface ICoreVipCardOrderCloseService {

    /**
     * 关闭单个订单
     *
     * @param orderId 订单ID
     */
    void close(Integer orderId);

    /**
     * 批量关闭订单
     *
     * @param orderIds 订单ID列表
     */
    void batchClose(List<Integer> orderIds);
}