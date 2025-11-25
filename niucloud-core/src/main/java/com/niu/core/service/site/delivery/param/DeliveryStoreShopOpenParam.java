package com.niu.core.service.site.delivery.param;

import lombok.Data;

import java.util.Map;

/**
 * 配送服务商开通门店参数
 */
@Data
public class DeliveryStoreShopOpenParam {
    
    /**
     * 配送类型
     */
    private String deliveryType;
    
    /**
     * 业务类型
     */
    private String business;
    
    /**
     * 扩展数据
     */
    private Map<String, Object> extendData;
}
