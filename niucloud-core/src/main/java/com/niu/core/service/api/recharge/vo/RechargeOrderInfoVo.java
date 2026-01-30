package com.niu.core.service.api.recharge.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.entity.recharge.RechargeOrderItem;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RechargeOrderInfoVo extends RechargeOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<RechargeOrderItem> item;

    private Status orderStatusInfo;

    /** 订单项目金额 */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal orderItemMoney;

    /** 充值赠送金额 */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal orderGiveMoney;

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
