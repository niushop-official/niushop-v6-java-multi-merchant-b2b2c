
package com.niu.core.service.core.local_delivery;

import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCreateParam;

import java.math.BigDecimal;

/**
 * 同城配送事件服务接口
 */
public interface ICoreLocalDeliveryOrderCreateService {
    
    /**
     * 订单创建
     */
    void create(LocalDeliveryOrderCreateParam param);

    /**
     * 配送费用计算
     */
    BigDecimal getDeliveryFee(LocalDeliveryOrderCreateParam param);
}
