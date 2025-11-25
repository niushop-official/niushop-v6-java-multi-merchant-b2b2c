package com.niu.mall.service.core.order.param;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单发货参数
 */
@Data
public class OrderGoodsDeliveryParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderNo;

    private List<Integer> orderGoodsIds;

}
