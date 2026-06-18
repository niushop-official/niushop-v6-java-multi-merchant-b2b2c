package com.niu.mall.service.site.order.param;

import com.niu.mall.service.core.order.model.OrderBatchDeliveryData;
import lombok.Data;

@Data
public class MallOrderAddBatchDeliveryParam {
    private OrderBatchDeliveryData data;
    private Integer siteId;
    //    private OrderBatchDeliveryTypeEnum type;
    private String type;
    private Integer mainId;
}
