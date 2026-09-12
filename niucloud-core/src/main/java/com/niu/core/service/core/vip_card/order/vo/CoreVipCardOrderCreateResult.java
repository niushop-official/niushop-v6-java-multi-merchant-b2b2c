package com.niu.core.service.core.vip_card.order.vo;

import lombok.Data;

@Data
public class CoreVipCardOrderCreateResult {
    private String tradeType;  // 固定 "vip_card"
    private Integer tradeId;   // 订单ID
    private String orderNo;
}