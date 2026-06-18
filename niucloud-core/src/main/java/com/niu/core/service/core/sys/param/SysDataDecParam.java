package com.niu.core.service.core.sys.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统计数据扣减，对齐 PHP CoreStatService::decStat
 */
@Data
public class SysDataDecParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 站点id */
    private Integer siteId;

    /** 业务时间戳（秒），用于定位统计日期 */
    private Long time;

    private Integer orderNum;
    private BigDecimal saleMoney;
    private BigDecimal refundMoney;
    private Integer accessSum;
    private Integer visitNum;
    private BigDecimal orderReceivableMoney;
    private BigDecimal orderActualMoney;
    private BigDecimal mallCouponMoney;
    private BigDecimal mallRefundCouponMoney;
    private BigDecimal orderRefundMoney;
    private BigDecimal orderActualRefundMoney;
    private BigDecimal shopSeparateMoney;
    private BigDecimal shopRefundSeparateMoney;
    private BigDecimal shopCommission;
    private BigDecimal shopRefundCommission;
    private BigDecimal deliveryMoney;
    private BigDecimal refundDeliveryMoney;
    private BigDecimal mallServiceMoney;
    private BigDecimal mallRefundServiceMoney;
    private BigDecimal rechargeMoney;
    private BigDecimal rechargePayMoney;
    private BigDecimal rechargeOrderMoney;
    private BigDecimal rechargeGiveMoney;
    private BigDecimal supplyMoney;
    private BigDecimal supplyRefundMoney;
    private BigDecimal supplySeparateMoney;
    private BigDecimal supplyRefundSeparateMoney;
}
