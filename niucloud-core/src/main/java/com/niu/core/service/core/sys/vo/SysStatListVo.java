package com.niu.core.service.core.sys.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统计数据列表
 */
@Data
public class SysStatListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 站点id */
    private Integer siteId;

    private String time;

    /** 账单类型 day/month */
    private String type;

    /** 订单数 */
    private Integer orderNum;

    /** 销售金额 */
    private BigDecimal saleMoney;

    /** 退款金额 */
    private BigDecimal refundMoney;

    /** 访问量 */
    private Integer accessNum;

    /** 订单应收 */
    private BigDecimal orderReceivableMoney;

    /** 订单实收 */
    private BigDecimal orderActualMoney;

    /** 平台优惠券补贴 */
    private BigDecimal mallCouponMoney;

    /** 平台退款优惠券补贴 */
    private BigDecimal mallRefundCouponMoney;

    /** 订单应退 */
    private BigDecimal orderRefundMoney;

    /** 订单实退 */
    private BigDecimal orderActualRefundMoney;

    /** 店铺分润 */
    private BigDecimal shopSeparateMoney;

    /** 店铺退款分润 */
    private BigDecimal shopRefundSeparateMoney;

    /** 店铺佣金 */
    private BigDecimal shopCommission;

    /** 店铺退款佣金 */
    private BigDecimal shopRefundCommission;

    /** 物流费用 */
    private BigDecimal deliveryMoney;

    /** 退还物流费用 */
    private BigDecimal refundDeliveryMoney;

    /** 平台服务费 */
    private BigDecimal mallServiceMoney;

    /** 平台退款服务费 */
    private BigDecimal mallRefundServiceMoney;

    /** 充值金额 */
    private BigDecimal rechargeMoney;

    /** 充值支付金额 */
    private BigDecimal rechargePayMoney;

    /** 供货金额 */
    private BigDecimal supplyMoney;

    /** 供货退款金额 */
    private BigDecimal supplyRefundMoney;

    /** 供货商支付分账 */
    private BigDecimal supplySeparateMoney;

    /** 供货商退款分账 */
    private BigDecimal supplyRefundSeparateMoney;

    /** 充值订单金额 */
    private BigDecimal rechargeOrderMoney;

    /** 充值赠送金额 */
    private BigDecimal rechargeGiveMoney;

    /** 商家分账=支付分账-退款分账 */
    private BigDecimal separateMoney;

    /** 佣金=支付佣金-退还佣金 */
    private BigDecimal commission;

    /** 运费=支付运费-退还运费 */
    private BigDecimal shippingFee;

    /** 平台手续费=支付手续费-退还手续费 */
    private BigDecimal serviceMoney;

    /** 实际收入 */
    private BigDecimal incomeMoney;

    /** 实际支出 */
    private BigDecimal outlayMoney;

    /** 当日/月结余 */
    private BigDecimal balanceMoney;

    /** 供货商货款=支付供货商货款-退还供货商货款 */
    private BigDecimal supplyGoodsMoney;

    /** 供货商分账=供货商支付分账-供货商退款分账 */
    private BigDecimal supplyAllSeparateMoney;
}
