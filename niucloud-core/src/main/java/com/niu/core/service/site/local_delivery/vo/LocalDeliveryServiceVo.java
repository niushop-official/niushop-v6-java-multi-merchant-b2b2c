package com.niu.core.service.site.local_delivery.vo;

import lombok.Data;

import java.util.Map;

/**
 * LocaleDeliveryServiceListVo列表视图
 */
@Data
public class LocalDeliveryServiceVo {

    private String deliveryType;
    private String name;
    private Integer isMerchant;
    private Integer isUse;
    private String component;
    private Map<String, ParamConfig> params;
    @Data
    public static class ParamConfig {
        private String name;
        private String value;

    }


}
