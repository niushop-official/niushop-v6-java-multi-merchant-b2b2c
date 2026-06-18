package com.niu.mall.service.core.order;

import com.niu.mall.service.core.order.param.OrderDeliveryContentParam;

/**
 * 订单发货接口
 */
public interface ICoreOrderDeliveryContentService {

    /**
     * 订单发货
     */
    void add(OrderDeliveryContentParam param);


}
