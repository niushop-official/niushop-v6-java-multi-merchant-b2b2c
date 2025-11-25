package com.niu.core.service.admin.stat.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;
/**
* 列表
*/
@Data
public class SysStatListVo  {


        /** 访问数 */
        private Integer accessSum;

        /** 日期 */
        private String date;

        /** 日期 */
        private String time;

        /** 时间戳 */
        private Long dateTime;

        /** 支付运费 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal deliveryMoney;

        /**  */
        private Integer id;

        /** 平台优惠补贴 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal mallCouponMoney;

        /** 返还平台优惠券补贴 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal mallRefundCouponMoney;

        /** 退还手续费 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal mallRefundServiceMoney;

        /** 支付手续费 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal mallServiceMoney;

        /** 月 */
        private String month;

        /** 订单实收 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal orderActualMoney;

        /** 订单实退 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal orderActualRefundMoney;

        /** 订单总数 */
        private Integer orderNum;

        /** 订单应收 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal orderReceivableMoney;

        /** 订单应退 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal orderRefundMoney;

        /** 充值金额 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal rechargeMoney;

        /** 充值消费金额 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal rechargePayMoney;

        /** 退还运费 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal refundDeliveryMoney;

        /** 退款总额 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal refundMoney;

        /** 销售总额 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal saleMoney;

        /** 支付佣金 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal shopCommission;

        /** 退还佣金 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal shopRefundCommission;

        /** 商家退款分账 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal shopRefundSeparateMoney;

        /** 商家支付分账 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal shopSeparateMoney;

        //当日结余
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal balanceMoney;

        /** 站点id */
        private Integer siteId;
        
        /** 类型 day/month */
        private String type;
        
        /** 商家分账（计算后） */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal separateMoney;
        
        /** 运费（计算后） */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal shippingFee;
        
        /** 平台手续费（计算后） */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal serviceMoney;
        
        /** 实际收入 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal incomeMoney;
        
        /** 实际支出 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal outlayMoney;
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal supplyAllSeparateMoney;
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal supplyGoodsMoney;
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal supplyMoney;
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal supplySeparateMoney;
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal supplyRefundMoney;
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal supplyRefundSeparateMoney;


        // 格式化显示字段，用于前端展示
        private String orderRefundMoneyDisplay;
        private String orderActualRefundMoneyDisplay;
        private String shopRefundSeparateMoneyDisplay;
        private String shopRefundCommissionDisplay;
        private String refundDeliveryMoneyDisplay;
        private String mallRefundServiceMoneyDisplay;
        private String mallCouponMoneyDisplay;
        private String mallRefundCouponMoneyDisplay;
        private String supplyRefundMoneyDisplay;
}
