package com.niu.core.entity.vip_card;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 付费会员卡订单表 实体类
 */
@Data
//@TableName("ns_member_vip_card_order")
public class MemberVipCardOrder {

    /**
     * 订单ID
     */
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单内容
     */
    private String body;

    /**
     * 订单来源
     */
    private String orderFrom;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 支付流水号
     */
    private String outTradeNo;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 退款状态
     */
    private Integer refundStatus;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 会员ip
     */
    private String ip;

    /**
     * 订单优惠金额
     */
    private BigDecimal orderDiscountMoney;

    /**
     * 订单金额
     */
    private BigDecimal orderMoney;

    /**
     * 创建时间（时间戳，Long类型）
     */
    private Long createTime;

    /**
     * 订单支付时间（时间戳）
     */
    private Long payTime;

    /**
     * 订单关闭时间（时间戳）
     */
    private Long closeTime;

    /**
     * 是否删除(针对后台)
     */
    private Integer isDelete;

    /**
     * 是否允许退款
     */
    private Integer isEnableRefund;

    /**
     * 商家留言
     */
    private String remark;

    /**
     * 通用业务超时时间记录
     */
    private Integer timeout;

    /**
     * 发票id，0表示不开发票
     */
    private Integer invoiceId;

    /**
     * 关闭原因
     */
    private String closeReason;

    /**
     * 结算状态
     */
    private String settlementStatus;

    /**
     * 小程序类型：developer开发版；trial体验版；formal正式版；默认为正式版
     */
    private String weappVersion;
}