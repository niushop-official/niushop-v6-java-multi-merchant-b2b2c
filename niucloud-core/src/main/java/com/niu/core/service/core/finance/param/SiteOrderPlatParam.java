package com.niu.core.service.core.finance.param;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class SiteOrderPlatParam implements Serializable {

    private static final long serialVersionUID = 1L;


    private BigDecimal orderMoney;

    private BigDecimal deliveryMoney;

    private String outTradeNo;

    private Integer memberId;

    private Integer siteId;

    private Integer platSiteId;

    private Integer mallSiteId;

    private Integer orderId;

    private String orderNo;

    private  BigDecimal shopCommission;

    private BigDecimal mallMoney = BigDecimal.ZERO;

    private  BigDecimal mallCouponMoney = BigDecimal.ZERO;

    private BigDecimal platMoney = BigDecimal.ZERO;

    private BigDecimal shopSeparateMoney = BigDecimal.ZERO;

    private BigDecimal shopIncomeMoney = BigDecimal.ZERO;

    private Integer supplyId;

    private BigDecimal supplyMoney = BigDecimal.ZERO;

    private BigDecimal supplySeparateMoney = BigDecimal.ZERO;

    private BigDecimal supplyIncomeMoney = BigDecimal.ZERO;
}
