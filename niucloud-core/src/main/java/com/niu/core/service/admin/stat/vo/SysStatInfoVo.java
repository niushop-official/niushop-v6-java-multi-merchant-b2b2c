package com.niu.core.service.admin.stat.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;

/**
* 详情
*/
@Data
public class SysStatInfoVo implements Serializable {

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

}