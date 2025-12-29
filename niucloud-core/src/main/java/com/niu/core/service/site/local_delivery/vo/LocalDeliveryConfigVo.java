package com.niu.core.service.site.local_delivery.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * LocaleDeliveryServiceListVo列表视图
 */
@Data
public class LocalDeliveryConfigVo {
    private String deliveryType;
    private String name;
    private Integer isMerchant;
    private Integer isUse;
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String params;
}
