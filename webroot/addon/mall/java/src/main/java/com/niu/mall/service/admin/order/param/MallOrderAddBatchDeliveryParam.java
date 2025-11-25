package com.niu.mall.service.admin.order.param;

import com.niu.mall.enums.order.OrderBatchDeliveryTypeEnum;
import com.niu.mall.service.core.order.model.OrderBatchDeliveryData;
import lombok.Data;

@Data
public class MallOrderAddBatchDeliveryParam {
    private OrderBatchDeliveryData data;
    private Integer siteId;
    private OrderBatchDeliveryTypeEnum type;
    private Integer mainId;
}
