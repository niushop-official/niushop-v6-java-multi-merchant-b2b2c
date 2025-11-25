package com.niu.mall.entity.order.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单退款配置
 */
@Data
public class MallOrderRefund implements Serializable {

    private static final long serialVersionUID = 1L;
    private Boolean noAllowRefund = false; // 退款允许
    private Integer refundLength = 0; // 退款时长

}