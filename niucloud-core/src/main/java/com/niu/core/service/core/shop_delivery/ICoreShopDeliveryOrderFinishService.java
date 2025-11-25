package com.niu.core.service.core.shop_delivery;

import com.niu.core.common.loader.local_delivery.param.CloseShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.FinishShopDeliveryOrderParam;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrder;

public interface ICoreShopDeliveryOrderFinishService {

    /**
     * 完成订单
     * @param param
     */
    DeliveryShopDeliveryOrder finishOrder(FinishShopDeliveryOrderParam param);
}
