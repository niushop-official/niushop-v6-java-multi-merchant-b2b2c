package com.niu.core.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShopStatVo {
    private Integer orderNum;//订单数
    private String saleMoney;//销售额
    private String refundMoney;//退款金额
    private String orderReceivableMoney; // 订单应收
    private String orderActualMoney;   // 订单实收
    private String orderRefundMoney;   // 订单应退
    private String orderActualRefundMoney; // 订单实退
    private String serviceMoney;       // 支付手续费
    private String refundServiceMoney; // 退还手续费
}
