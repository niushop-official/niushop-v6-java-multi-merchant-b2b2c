package com.niu.mall.service.core.order;

import com.niu.mall.service.core.order.param.OrderFinishParam;

public interface ICoreOrderFinishService {

    /**
     * 订单完成
     * @param param
     * @return
     */
    boolean finish(OrderFinishParam param);

    /**
     * 订单收货
     * @param orderId
     * @param orderGoodsIds
     * @return
     */
    boolean orderGoodsTake(Integer orderId, Integer[] orderGoodsIds);
}