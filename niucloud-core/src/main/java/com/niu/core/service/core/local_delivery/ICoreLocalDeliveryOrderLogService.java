
package com.niu.core.service.core.local_delivery;

import com.niu.core.service.core.local_delivery.param.AddLocalDeliveryOrderLogParam;

/**
 * 同城配送订单日志服务层
 */
public interface ICoreLocalDeliveryOrderLogService {
    
    /**
     * 添加配送订单日志
     */
    void addLog(AddLocalDeliveryOrderLogParam param);
}
