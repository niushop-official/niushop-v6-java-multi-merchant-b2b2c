package com.niu.mall.service.core.stat.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConversionDataVo {
    private Integer visitNum;                // 访客数
    private Integer orderMemberCount;        // 下单人数
    private Integer orderCount;              // 下单订单数
    private Integer orderNum;                // 下单商品件数
    private BigDecimal creteOrderMoney;      // 下单金额
    private Integer payMemberCount;          // 付款人数
    private Integer payCount;                // 支付订单数
    private Integer payNum;                  // 支付件数
    private BigDecimal orderMoney;           // 支付金额
    private BigDecimal memberUnitPrice;      // 客单价
    private BigDecimal visitOrderRate;       // 访问->下单转化率
    private BigDecimal orderPayRate;         // 下单->支付转化率
    private BigDecimal visitPayRate;         // 访问->支付转化率

    // 环比数据
    private BigDecimal visitNumLoopRate;
    private BigDecimal orderMemberCountLoopRate;
    private BigDecimal orderCountLoopRate;
    private BigDecimal orderNumLoopRate;
    private BigDecimal creteOrderMoneyLoopRate;
    private BigDecimal payMemberCountLoopRate;
    private BigDecimal payCountLoopRate;
    private BigDecimal payNumLoopRate;
    private BigDecimal orderMoneyLoopRate;
    private BigDecimal memberUnitPriceLoopRate;
}