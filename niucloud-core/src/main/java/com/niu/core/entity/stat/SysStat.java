package com.niu.core.entity.stat;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SysStat  {

    private static final long serialVersionUID = 1L;


    /** 访问数 */
    private Integer accessSum;

    /** 日期 */
    private String date;

    /** 时间戳 */
    private Long dateTime;

    /** 支付运费 */
    private BigDecimal deliveryMoney;

    /**  */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 平台优惠补贴 */
    private BigDecimal mallCouponMoney;

    /** 返还平台优惠券补贴 */
    private BigDecimal mallRefundCouponMoney;

    /** 退还手续费 */
    private BigDecimal mallRefundServiceMoney;

    /** 支付手续费 */
    private BigDecimal mallServiceMoney;

    /** 月 */
    private String month;

    /** 订单实收 */
    private BigDecimal orderActualMoney;

    /** 订单实退 */
    private BigDecimal orderActualRefundMoney;

    /** 订单总数 */
    private Integer orderNum;

    /** 订单应收 */
    private BigDecimal orderReceivableMoney;

    /** 订单应退 */
    private BigDecimal orderRefundMoney;

    /** 充值金额 */
    private BigDecimal rechargeMoney;

    /** 充值消费金额 */
    private BigDecimal rechargePayMoney;

    /** 退还运费 */
    private BigDecimal refundDeliveryMoney;

    /** 退款总额 */
    private BigDecimal refundMoney;

    /** 销售总额 */
    private BigDecimal saleMoney;

    /** 支付佣金 */
    private BigDecimal shopCommission;

    /** 退还佣金 */
    private BigDecimal shopRefundCommission;

    /** 商家退款分账 */
    private BigDecimal shopRefundSeparateMoney;

    /** 商家支付分账 */
    private BigDecimal shopSeparateMoney;

    /** 站点id */
    private Integer siteId;

    /** 供应商支付分账 */
    private BigDecimal supplySeparateMoney;

    /** 供应商退款分账 */
    private BigDecimal supplyRefundSeparateMoney;

    /** 支付供应商货款 */
    private BigDecimal supplyMoney;

    /** 退还供应商货款 */
    private BigDecimal supplyRefundMoney;
}
