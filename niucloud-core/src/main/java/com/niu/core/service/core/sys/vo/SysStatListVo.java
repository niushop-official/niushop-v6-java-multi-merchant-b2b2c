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

    /**
     * 商家分账=支付分账-退款分账
     * @return
     */
    public BigDecimal getSeparateMoney() {
        return shopSeparateMoney.subtract(shopRefundSeparateMoney);
    }


    /**
     * 佣金=支付佣金-退还佣金
     * @return
     */
    public BigDecimal getCommission() {
        return shopCommission.subtract(shopRefundCommission);
    }

    /**
     * 运费=支付运费-退还运费
     * @return
     */
    public BigDecimal getShippingFee() {
        return deliveryMoney.subtract(refundDeliveryMoney);
    }

    public BigDecimal getServiceMoney() {
        return mallServiceMoney.subtract(mallRefundServiceMoney);
    }

    public BigDecimal getIncomeMoney() {
        return orderActualMoney.subtract(orderActualRefundMoney);
    }

    public BigDecimal getOrderRefundMoney() {
        return orderRefundMoney.multiply(new BigDecimal(-1));
    }
    public BigDecimal getActualRefundMoney() {
        return orderActualRefundMoney.multiply(new BigDecimal(-1));
    }

    public BigDecimal getShopRefundSeparateMoney() {
        return shopRefundSeparateMoney.multiply(new BigDecimal(-1));
    }
    public BigDecimal getShopRefundCommission() {
        return shopRefundCommission.multiply(new BigDecimal(-1));
    }
    public BigDecimal getRefundDeliveryMoney() {
        return refundDeliveryMoney.multiply(new BigDecimal(-1));
    }
    public BigDecimal getMallRefundServiceMoney() {
        return mallRefundServiceMoney.multiply(new BigDecimal(-1));
    }
    public BigDecimal getMallCouponMoney() {
        if(siteId.equals(0)) {
            return mallCouponMoney.multiply(new BigDecimal(-1));
        }else{
            return mallCouponMoney;
        }
    }
    public BigDecimal getMallRefundCouponMoney() {
        if(siteId.equals(0)) {
            return mallRefundCouponMoney;
        }else {
            return mallRefundCouponMoney.multiply(new BigDecimal(-1));
        }
    }

    public BigDecimal getSupplyRefundMoney() {
        return supplyRefundMoney.multiply(new BigDecimal(-1));
    }

    public BigDecimal getSupplyGoodsMoney() {
        return supplyMoney.subtract(supplyRefundMoney).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getOutlayMoney() {
        if(siteId.equals(0)) {
            return getSeparateMoney().add(getCommission()).add(getShippingFee()).add(getSupplyMoney());
        }else{
            return getServiceMoney().add(getCommission()).add(getSupplyGoodsMoney());
        }
    }

    public BigDecimal getBalanceMoney() {
        return getIncomeMoney().subtract(getOutlayMoney());
    }
}
