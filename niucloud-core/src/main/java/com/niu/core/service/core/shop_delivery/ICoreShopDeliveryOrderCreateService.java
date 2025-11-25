package com.niu.core.service.core.shop_delivery;

import com.niu.core.common.loader.local_delivery.param.AddShopDeliveryOrderParam;
import com.niu.core.service.core.shop_delivery.vo.ShopDeliveryOrderCreateVo;

public interface ICoreShopDeliveryOrderCreateService {

    /**
     * 创建订单
     * @param param
     */
    ShopDeliveryOrderCreateVo createOrder(AddShopDeliveryOrderParam param);
}
