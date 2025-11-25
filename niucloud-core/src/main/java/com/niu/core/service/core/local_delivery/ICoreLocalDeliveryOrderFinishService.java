
package com.niu.core.service.core.local_delivery;

import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderFinishParam;

/**
 * 同城配送事件服务接口
 */
public interface ICoreLocalDeliveryOrderFinishService {
    
    /**
     * 取消订单
     */
    void finishOrder(LocalDeliveryOrderFinishParam param);
}
