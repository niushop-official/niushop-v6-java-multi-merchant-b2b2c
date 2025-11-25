package com.niu.core.service.core.sys.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

/**
 * 统计数据添加
*/
@Data
public class SysStatVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 站点id */
    private Integer siteId;

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
    /** 供货金额 */
    private BigDecimal supplySeparateMoney;

    /** 供货退款金额 */
    private BigDecimal supplyRefundSeparateMoney;
    /** 供货退款金额 */
    private BigDecimal supplyRefundMoney;

    /** 供货退款金额 */
    private BigDecimal supplyGoodsMoney;
    /** 供货退款金额 */
    private BigDecimal shippingFee;

    public BigDecimal getSupplyGoodsMoney() {
        return supplyMoney.subtract(supplyRefundMoney).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getShippingFee() {
        return deliveryMoney.subtract(refundDeliveryMoney).setScale(2, RoundingMode.HALF_UP);
    }
}
