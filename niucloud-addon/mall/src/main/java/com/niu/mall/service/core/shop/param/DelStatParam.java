package com.niu.mall.service.core.shop.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *统计参数
*/
@Data
public class DelStatParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderNum; // 订单总数
    private BigDecimal saleMoney; // 销售总额
    private BigDecimal refundMoney; // 退款总额
    private Integer accessSum; // 访问数

    private String date; // 时间
}
