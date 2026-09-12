package com.niu.core.service.admin.stat.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 统计
 */
@Data
@Builder
public class AddStatVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderNum; // 订单总数
    private BigDecimal saleMoney; // 销售总额
    private BigDecimal refundMoney; // 退款总额
    private Integer accessSum; // 访问数
}

