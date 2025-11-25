package com.niu.mall.service.site.order.param;

import com.niu.mall.enums.order.OrderBatchDeliveryTypeEnum;
import com.niu.mall.service.core.order.model.OrderBatchDeliveryData;
import lombok.Data;

import java.util.List;

@Data
public class MallOrderDeleteParam {
    private List<Integer> orderIds;
}
