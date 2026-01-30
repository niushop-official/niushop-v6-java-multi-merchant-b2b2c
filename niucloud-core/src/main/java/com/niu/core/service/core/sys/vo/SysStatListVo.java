package com.niu.core.service.core.sys.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 统计数据添加
*/
@Data
public class SysStatListVo implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 站点id */
    private Integer siteId;

    private String time;

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

    /**
     * 商家分账=支付分账-退款分账
     * @return
     */
    public BigDecimal getSeparateMoney() {
        if (shopSeparateMoney == null || shopRefundSeparateMoney == null) {
            return BigDecimal.ZERO;
        }
        return shopSeparateMoney.subtract(shopRefundSeparateMoney).setScale(2, RoundingMode.HALF_UP);
    }


    /**
     * 佣金=支付佣金-退还佣金
     * @return
     */
    public BigDecimal getCommission() {
        if (shopCommission == null || shopRefundCommission == null) {
            return BigDecimal.ZERO;
        }
        return shopCommission.subtract(shopRefundCommission).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 运费=支付运费-退还运费
     * @return
     */
    public BigDecimal getShippingFee() {
        if (deliveryMoney == null || refundDeliveryMoney == null) {
            return BigDecimal.ZERO;
        }
        return deliveryMoney.subtract(refundDeliveryMoney).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getServiceMoney() {
        if (mallServiceMoney == null || mallRefundServiceMoney == null) {
            return BigDecimal.ZERO;
        }
        return mallServiceMoney.subtract(mallRefundServiceMoney).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getIncomeMoney() {
        if (siteId == null || siteId == 0) {
            // 实际收入=订单实收-订单应退
            if (orderActualMoney == null || orderRefundMoney == null) {
                return BigDecimal.ZERO;
            }
            return orderActualMoney.subtract(orderRefundMoney);
        } else {
            // 实际收入=订单应收-订单应退
            if (orderReceivableMoney == null || orderRefundMoney == null) {
                return BigDecimal.ZERO;
            }
            return orderReceivableMoney.subtract(orderRefundMoney);
        }
    }

    public BigDecimal getOrderRefundMoney() {
        if (orderRefundMoney == null) {
            return BigDecimal.ZERO;
        }
        return orderRefundMoney.negate();
    }
    public BigDecimal getActualRefundMoney() {
        if (orderActualRefundMoney == null) {
            return BigDecimal.ZERO;
        }
        return orderActualRefundMoney.negate();
    }

    public BigDecimal getShopRefundSeparateMoney() {
        if (shopRefundSeparateMoney == null) {
            return BigDecimal.ZERO;
        }
        return shopRefundSeparateMoney.negate();
    }
    public BigDecimal getShopRefundCommission() {
        if (shopRefundCommission == null) {
            return BigDecimal.ZERO;
        }
        return shopRefundCommission.negate();
    }
    public BigDecimal getRefundDeliveryMoney() {
        if (refundDeliveryMoney == null) {
            return BigDecimal.ZERO;
        }
        return refundDeliveryMoney.negate();
    }
    public BigDecimal getMallRefundServiceMoney() {
        if (mallRefundServiceMoney == null) {
            return BigDecimal.ZERO;
        }
        return mallRefundServiceMoney.negate();
    }
    public BigDecimal getMallCouponMoney() {
        if (mallCouponMoney == null) {
            return BigDecimal.ZERO;
        }
        if (siteId != null && siteId == 0) {
            return mallCouponMoney.negate();
        } else {
            return mallCouponMoney;
        }
    }
    public BigDecimal getMallRefundCouponMoney() {
        if (mallRefundCouponMoney == null) {
            return BigDecimal.ZERO;
        }
        if (siteId != null && siteId == 0) {
            return mallRefundCouponMoney;
        } else {
            return mallRefundCouponMoney.negate();
        }
    }

    public BigDecimal getSupplyRefundMoney() {
        if (supplyRefundMoney == null) {
            return BigDecimal.ZERO;
        }
        return supplyRefundMoney.negate();
    }

    public BigDecimal getSupplyRefundSeparateMoney() {
        if (supplyRefundSeparateMoney == null) {
            return BigDecimal.ZERO;
        }
        return supplyRefundSeparateMoney.negate();
    }

    public BigDecimal getSupplyGoodsMoney() {
        if (supplyMoney == null || supplyRefundMoney == null) {
            return BigDecimal.ZERO;
        }
        return supplyMoney.subtract(supplyRefundMoney).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 供货商分账=供货商支付分账-供货商退款分账
     * @return
     */
    public BigDecimal getSupplyAllSeparateMoney() {
        if (supplySeparateMoney == null || supplyRefundSeparateMoney == null) {
            return BigDecimal.ZERO;
        }
        return supplySeparateMoney.subtract(supplyRefundSeparateMoney).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getOutlayMoney() {
        if (siteId == null || siteId == 0) {
            // 实际支出=商家分账+佣金+运费+供货商分账
            return getSeparateMoney().add(getCommission()).add(getShippingFee()).add(getSupplyAllSeparateMoney()).setScale(2, RoundingMode.HALF_UP);
        } else {
            // 实际支出=平台手续费+佣金+供货商货款
            return getServiceMoney().add(getCommission()).add(getSupplyGoodsMoney()).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public BigDecimal getBalanceMoney() {
        return getIncomeMoney().subtract(getOutlayMoney()).setScale(2, RoundingMode.HALF_UP);
    }
}
