package com.niu.core.service.core.local_delivery;

import com.niu.core.entity.delivery.DeliveryLocalDeliveryService;

import java.util.Map;

/**
 * 同城配送核心服务接口
 */
public interface ICoreLocalDeliveryService {

    /**
     * 获取配送配置
     *
     * @param deliveryType 配送类型
     * @return 配送配置信息
     */
    Map<String, Object> getConfig(String deliveryType, Integer siteId);

    /**
     * 设置回调地址
     *
     * @param siteId  店铺id
     * @param type    配送类型
     * @param orderNo 订单编号
     * @param action  订单编号
     * @return 配送配置信息
     */
    String buildNotifyUrl(String siteId, String type, String orderNo, String action);
}
