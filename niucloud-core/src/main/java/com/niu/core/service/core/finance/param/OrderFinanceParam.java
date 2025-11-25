package com.niu.core.service.core.finance.param;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class OrderFinanceParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal orderMoney = BigDecimal.ZERO;

    private BigDecimal deliveryMoney = BigDecimal.ZERO;

    private String outTradeNo;

    private Integer memberId;

    private Integer siteId;

    private Integer mallSiteId;

    private Integer orderId;

    private String orderNo;

    private  BigDecimal shopCommission = BigDecimal.ZERO;

    private BigDecimal mallMoney = BigDecimal.ZERO;

    private  BigDecimal mallCouponMoney = BigDecimal.ZERO;

    private BigDecimal platMoney = BigDecimal.ZERO;

    private BigDecimal shopSeparateMoney = BigDecimal.ZERO;

    private Integer supplyId;

    private BigDecimal supplyMoney = BigDecimal.ZERO;
}
