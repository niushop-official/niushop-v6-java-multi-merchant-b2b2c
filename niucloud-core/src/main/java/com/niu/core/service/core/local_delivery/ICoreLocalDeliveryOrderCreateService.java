
package com.niu.core.service.core.local_delivery;

import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCreateParam;

/**
 * 同城配送事件服务接口
 */
public interface ICoreLocalDeliveryOrderCreateService {
    
    /**
     * 订单创建
     */
    void create(LocalDeliveryOrderCreateParam param);
}
