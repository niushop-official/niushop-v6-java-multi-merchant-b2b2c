package com.niu.core.service.site.delivery.param;

import cn.hutool.json.JSONArray;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 运费模板参数
 */
@Data
public class DeliveryShippingTemplateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "template_name参数缺失")
    @NotEmpty(message = "模板名称不能为空")
    private String templateName;

    @NotNull(message = "fee_type参数缺失")
    private String feeType;

    private Integer isFreeShipping = 0;

    private Integer noDelivery = 0;

    private JSONArray area;
    private  JSONArray    supportTrade;
}