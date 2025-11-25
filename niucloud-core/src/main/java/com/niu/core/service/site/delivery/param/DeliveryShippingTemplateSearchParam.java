package com.niu.core.service.site.delivery.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 运费模板参数
 */
@Data
public class DeliveryShippingTemplateSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String templateName;

}
