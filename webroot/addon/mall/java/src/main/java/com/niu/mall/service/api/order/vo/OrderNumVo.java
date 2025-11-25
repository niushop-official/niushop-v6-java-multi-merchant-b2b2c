package com.niu.mall.service.api.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderNumVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long waitPay;//待付款

    private Long waitShipping;

    private Long waitTake;//待收货

    private Long evaluate;

    private Long refund;

    /**
     * 514
     */
    private Long waitDelivery;//待发货

    private Long waitRefund;//待售后

    private Long waitVerify;//到店核销

    private Long waitTransfer;//待转账数量（微信零钱）
}
