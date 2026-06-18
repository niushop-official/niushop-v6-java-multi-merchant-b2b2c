package com.niu.mall.service.core.stat.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BasicDataVo {
    private BigDecimal saleMoney;          // 实际销售额
    private BigDecimal orderMoney;         // 付款金额
    private BigDecimal balancePayMoney;    // 余额支付金额
    private Integer orderCount;            // 下单订单数
    private Integer payCount;              // 付款订单数
    private Integer orderNum;              // 下单商品件数
    private Integer payNum;                // 付款商品件数
    private Integer orderMemberCount;      // 下单人数
    private Integer payMemberCount;        // 付款人数
    private Integer refundNum;             // 售后商品件数
    private BigDecimal refundMoney;        // 售后金额
    private Integer refundMemberCount;     // 售后会员数
}
