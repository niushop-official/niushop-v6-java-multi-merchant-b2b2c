package com.niu.core.service.core.finance.param;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单支付参数
 */
@Data
public class OrderPayParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal orderMoney;

    private BigDecimal deliveryMoney;

    private Integer siteId;

    private  BigDecimal shopCommission;

    private Integer supplyId;
}
