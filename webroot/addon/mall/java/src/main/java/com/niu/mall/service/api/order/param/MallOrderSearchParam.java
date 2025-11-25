package com.niu.mall.service.api.order.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单参数
 */
@Data
public class MallOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String status;

    private String activityType;

}
