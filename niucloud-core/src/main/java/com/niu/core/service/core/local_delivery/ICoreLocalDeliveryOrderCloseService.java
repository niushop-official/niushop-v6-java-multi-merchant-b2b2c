
package com.niu.core.service.core.local_delivery;

import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCloseParam;

/**
 * 同城配送事件服务接口
 */
public interface ICoreLocalDeliveryOrderCloseService {
    
    /**
     * 取消订单
     */
    void closeOrder(LocalDeliveryOrderCloseParam param);
}
