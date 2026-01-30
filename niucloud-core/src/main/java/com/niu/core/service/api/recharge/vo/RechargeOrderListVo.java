package com.niu.core.service.api.recharge.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.recharge.RechargeOrderItem;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.enums.recharge.RechargeRefundStatusEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RechargeOrderListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;

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
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal orderItemMoney;

    /** 充值赠送金额 */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal orderGiveMoney;
    /** 充值赠送积分 */
    private Integer orderGivePoint;
    /** 充值赠送成长值 */
    private Integer orderGiveGrowth;

    /** 订单优惠金额 */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal orderDiscountMoney;

    /** 订单金额 */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal orderMoney;

    /** 创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 订单支付时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long payTime;

    /** 订单关闭时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long closeTime;

    /** 是否删除(针对后台) */
    private Integer isDelete;

    /** 是否允许退款 */
    private Integer isEnableRefund;

    /** 商家留言 */
    private String remark;

    /** 发票id，0表示不开发票 */
    private Integer invoiceId;

    /** 关闭原因 */
    private String closeReason;


    private RechargeOrderItem item;

    private Status orderStatusInfo;


    private MemberBriefInfoVo member;

    private String orderFromName;


    @Data
    private static class Status {
        private String name;

        private Integer status;
    }

    public Status getOrderStatusInfo() {
        Status status = new Status();
        status.setStatus(this.getOrderStatus());
        status.setName(RechargeOrderStatusEnum.getNameByStatus(this.getOrderStatus()));
        return status;
    }

    public String getOrderFromName() {
        return ChannelEnum.getNameByCode(this.getOrderFrom());
    }

}
