package com.niu.core.service.core.shop_delivery;

import com.niu.core.common.loader.local_delivery.param.AddShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.CloseShopDeliveryOrderParam;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrder;
import com.niu.core.service.core.shop_delivery.vo.ShopDeliveryOrderCreateVo;

public interface ICoreShopDeliveryOrderCloseService {

    /**
     * 取消订单
     * @param param
     */
    DeliveryShopDeliveryOrder closeOrder(CloseShopDeliveryOrderParam param);
}
