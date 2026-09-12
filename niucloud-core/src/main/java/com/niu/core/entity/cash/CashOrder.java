package com.niu.core.entity.cash;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 买单订单
 */
@Data
public class CashOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId;

    private Integer siteId;

    private String siteName;

    private String orderNo;

    private String orderFrom;

    private String outTradeNo;

    private Integer status;

    private Integer memberId;

    private String ip;

    private String tradeType;

    private BigDecimal totalMoney;

    private BigDecimal noDiscountMoney;

    private BigDecimal discountableMoney;

    private BigDecimal platformCouponMoney;

    private Integer platformCouponId;

    private BigDecimal siteCouponMoney;

    private Integer siteCouponId;

    private BigDecimal pointDeductMoney;

    private Integer point;

    private BigDecimal pointMoney;

    private BigDecimal discountMoney;

    private BigDecimal platDiscountMoney;

    private BigDecimal orderMoney;

    private String memberRemark;

    private String shopRemark;

    private Integer refundStatus;

    private BigDecimal refundMoney;

    private Integer isEvaluate;

    private Long createTime;

    private Long payTime;

    private Long finishTime;

    private Long closeTime;

    private Long deleteTime;
}
