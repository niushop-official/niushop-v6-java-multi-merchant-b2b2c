package com.niu.core.service.core.local_delivery;

import com.niu.core.entity.delivery.DeliveryLocalDeliveryService;

import java.util.Map;

/**
 * 同城配送核心服务接口
 */
public interface ICoreLocalDeliveryService {
    
    /**
     * 获取配送配置
     * @param deliveryType 配送类型
     * @return 配送配置信息
     */
    Map<String, Object> getConfig(String deliveryType);
}
