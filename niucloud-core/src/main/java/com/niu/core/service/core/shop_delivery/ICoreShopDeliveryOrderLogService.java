package com.niu.core.service.core.shop_delivery;

import com.niu.core.service.core.shop_delivery.param.AddShopDeliveryOrderLogParam;

public interface ICoreShopDeliveryOrderLogService {

    /**
     * 添加配送订单日志
     * @param param
     */
    void addLog(AddShopDeliveryOrderLogParam param);
}
