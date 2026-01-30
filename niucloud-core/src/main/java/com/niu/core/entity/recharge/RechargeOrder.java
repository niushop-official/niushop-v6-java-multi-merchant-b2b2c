package com.niu.core.entity.recharge;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="order_id", type= IdType.AUTO)
    private Integer orderId;

    /** 站点id */
    private Integer siteId;

    /** 订单编号 */
    private String orderNo;

    /** 订单来源 */
    private String orderFrom;

    /** 订单类型 */
    private String orderType;

    /** 支付流水号 */
    private String outTradeNo;

    /** 订单状态 */
    private Integer orderStatus;

    /** 退款状态 */
    private Integer refundStatus;

    /** 会员id */
    private Integer memberId;

    /** 会员ip */
    private String ip;

    /** 会员留言信息 */
    private String memberMessage;

    /** 订单项目金额 */
    private BigDecimal orderItemMoney;
    /** 实际充值到账金额 */
    private BigDecimal orderFaceMoney;
    /** 充值赠送金额 */
    private BigDecimal orderGiveMoney;
    /** 充值赠送积分 */
    private Integer orderGivePoint;
    /** 充值赠送成长值 */
    private Integer orderGiveGrowth;

    /** 订单优惠金额 */
    private BigDecimal orderDiscountMoney;

    /** 订单金额 */
    private BigDecimal orderMoney;

    /** 创建时间 */
    private Long createTime;

    /** 订单支付时间 */
    private Long payTime;

    /** 订单关闭时间 */
    private Long closeTime;

    /** 是否删除(针对后台) */
    private Integer isDelete;

    /** 是否允许退款 */
    private Integer isEnableRefund;

    /** 商家留言 */
    private String remark;

    /** 通用业务超时时间记录 */
    private Integer timeout;

    /** 发票id，0表示不开发票 */
    private Integer invoiceId;

    /** 关闭原因 */
    private String closeReason;

    /** 结算状态 */
    private String settlementStatus;

    /** 小程序类型，developer为开发版；trial为体验版；formal为正式版；默认为正式版 */
    private String weappVersion;

}
