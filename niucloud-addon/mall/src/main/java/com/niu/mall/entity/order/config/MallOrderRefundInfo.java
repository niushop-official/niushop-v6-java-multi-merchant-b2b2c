package com.niu.mall.entity.order.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单退款配置
 */
@Data
public class MallOrderRefundInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer refundAuditBeforeDelivery; //是否开启快捷退款

}