package com.niu.core.service.site.delivery.param;

import lombok.Data;

/**
 * 配送服务商门店品类修改参数
 */
@Data
public class DeliveryStoreShopEditParam {
    
    /**
     * 配送类型
     */
    private String deliveryType;
    
    /**
     * 业务类型
     */
    private String business;
}
