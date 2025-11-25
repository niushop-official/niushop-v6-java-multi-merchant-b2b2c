package com.niu.core.service.core.shop_delivery;

import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;

public interface ICoreShopDeliveryOrderMessageService {

    /**
     * 订单状态回调
     * @param param
     */
    void orderNotify(NotifyShopDeliveryOrderParam param);

}
