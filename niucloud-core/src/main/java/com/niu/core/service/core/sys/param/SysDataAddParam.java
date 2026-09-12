package com.niu.core.service.core.sys.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统计数据添加
*/
@Data
public class SysDataAddParam implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 站点id */
    private Integer siteId;

    /** 订单数 */
    private Integer orderNum = 0;

    /** 销售金额 */
    private BigDecimal saleMoney = BigDecimal.ZERO;

    /** 退款金额 */
    private BigDecimal refundMoney = BigDecimal.ZERO;

    /** 访问量 */
    private Integer accessSum = 0;
    /** 访客数 */
    private Integer visitNum = 0;

    /** 订单应收 */
    private BigDecimal orderReceivableMoney = BigDecimal.ZERO;

    /** 订单实收 */
    private BigDecimal orderActualMoney = BigDecimal.ZERO;

    /** 平台优惠券补贴 */
    private BigDecimal mallCouponMoney = BigDecimal.ZERO;

    /** 平台退款优惠券补贴 */
    private BigDecimal mallRefundCouponMoney = BigDecimal.ZERO;

    /** 订单应退 */
    private BigDecimal orderRefundMoney = BigDecimal.ZERO;

    /** 订单实退 */
    private BigDecimal orderActualRefundMoney = BigDecimal.ZERO;

    /** 店铺分润 */
    private BigDecimal shopSeparateMoney = BigDecimal.ZERO;

    /** 店铺退款分润 */
    private BigDecimal shopRefundSeparateMoney = BigDecimal.ZERO;

    /** 店铺佣金 */
    private BigDecimal shopCommission = BigDecimal.ZERO;

    /** 店铺退款佣金 */
    private BigDecimal shopRefundCommission = BigDecimal.ZERO;

    /** 物流费用 */
    private BigDecimal deliveryMoney = BigDecimal.ZERO;

    /** 退还物流费用 */
    private BigDecimal refundDeliveryMoney = BigDecimal.ZERO;

    /** 平台服务费 */
    private BigDecimal mallServiceMoney = BigDecimal.ZERO;

    /** 平台退款服务费 */
    private BigDecimal mallRefundServiceMoney = BigDecimal.ZERO;

    /** 充值金额 */
    private BigDecimal rechargeMoney = BigDecimal.ZERO;

    /** 充值支付金额 */
    private BigDecimal rechargePayMoney = BigDecimal.ZERO;

    /** 充值订单金额 */
    private BigDecimal rechargeOrderMoney = BigDecimal.ZERO;

    /** 充值赠送金额 */
    private BigDecimal rechargeGiveMoney = BigDecimal.ZERO;

    /** 供货金额 */
    private BigDecimal supplyMoney = BigDecimal.ZERO;

    /** 供货分账金额 */
    private BigDecimal supplySeparateMoney = BigDecimal.ZERO;

    /** 供货退款金额 */
    private BigDecimal supplyRefundMoney = BigDecimal.ZERO;

    /** 供应商退款分账金额 */
    private BigDecimal supplyRefundSeparateMoney = BigDecimal.ZERO;

    /** 商圈佣金 */
    private BigDecimal circleCommission = BigDecimal.ZERO;

    /** 商圈退还佣金 */
    private BigDecimal circleRefundCommission = BigDecimal.ZERO;
}
