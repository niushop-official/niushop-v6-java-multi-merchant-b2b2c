package com.niu.mall.service.core.order.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 订单发货参数
 */
@Data
public class OrderDeliveryContentParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer orderId;

    private Integer siteId;
    private Integer tradeId;
    private String tradeType;
    private String tradeNo;
    private String content;
    private Long createTime;

}
