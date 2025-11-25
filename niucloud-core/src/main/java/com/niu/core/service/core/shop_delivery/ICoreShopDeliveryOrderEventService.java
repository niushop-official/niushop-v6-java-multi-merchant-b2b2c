package com.niu.core.service.core.shop_delivery;

import com.niu.core.service.core.shop_delivery.param.EventShopDeliveryOrderParam;

public interface ICoreShopDeliveryOrderEventService {

    /**
     * 骑手已取货
     * @param param
     */
    Boolean riderPickedUp(EventShopDeliveryOrderParam param,String deliveryNo);

    /**
     * 取消订单
     * @param param
     */
    Boolean cancel(EventShopDeliveryOrderParam param,String deliveryNo);

    /**
     * 订单完成
     * @param param
     */
    Boolean finish(EventShopDeliveryOrderParam param,String deliveryNo);
}
