package com.niu.core.service.core.sys.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * 统计数据添加
*/
@Data
public class SysDataVo implements Serializable {

    private static final long serialVersionUID = 1L;


    private List<String> time = new LinkedList<>();

    /** 订单数 */
    private List<Integer> orderNum = new LinkedList<>();

    /** 销售金额 */
    private List<BigDecimal> saleMoney = new LinkedList<>();

    /** 退款金额 */
    private List<BigDecimal> refundMoney = new LinkedList<>();

    /** 访问量 */
    private List<Integer> accessSum = new LinkedList<>();

    /** 订单应收 */
    private List<BigDecimal> orderReceivableMoney = new LinkedList<>();

    /** 订单实收 */
    private List<BigDecimal> orderActualMoney = new LinkedList<>();

    /** 平台优惠券补贴 */
    private List<BigDecimal> mallCouponMoney = new LinkedList<>();

    /** 平台退款优惠券补贴 */
    private List<BigDecimal> mallRefundCouponMoney = new LinkedList<>();

    /** 订单应退 */
    private List<BigDecimal> orderRefundMoney = new LinkedList<>();

    /** 订单实退 */
    private List<BigDecimal> orderActualRefundMoney = new LinkedList<>();

    /** 店铺分润 */
    private List<BigDecimal> shopSeparateMoney = new LinkedList<>();

    /** 店铺退款分润 */
    private List<BigDecimal> shopRefundSeparateMoney = new LinkedList<>();

    /** 店铺佣金 */
    private List<BigDecimal> shopCommission = new LinkedList<>();

    /** 店铺退款佣金 */
    private List<BigDecimal> shopRefundCommission = new LinkedList<>();

    /** 物流费用 */
    private List<BigDecimal> deliveryMoney = new LinkedList<>();

    /** 退还物流费用 */
    private List<BigDecimal> refundDeliveryMoney = new LinkedList<>();

    /** 平台服务费 */
    private List<BigDecimal> mallServiceMoney = new LinkedList<>();

    /** 平台退款服务费 */
    private List<BigDecimal> mallRefundServiceMoney = new LinkedList<>();

    /** 充值金额 */
    private List<BigDecimal> rechargeMoney = new LinkedList<>();

    /** 充值支付金额 */
    private List<BigDecimal> rechargePayMoney = new LinkedList<>();

    /** 供货金额 */
    private List<BigDecimal> supplyMoney = new LinkedList<>();

    /** 供货金额 */
    private List<BigDecimal> supplySeparateMoney = new LinkedList<>();

    /** 供货退款金额 */
    private List<BigDecimal> supplyRefundMoney = new LinkedList<>();

    /** 供货退款金额 */
    private List<BigDecimal> supplyRefundSeparateMoney = new LinkedList<>();

}
