
package com.niu.core.service.core.local_delivery;

import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;

/**
 * 同城配送订单日志服务层
 */
public interface ICoreLocalDeliveryOrderNotifyService {
    
    /**
     * 添加配送订单日志
     */
    void notify(Integer siteId,String type,String action, NotifyShopDeliveryOrderParam param);
}
