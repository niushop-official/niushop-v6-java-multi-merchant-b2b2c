package com.niu.core.service.api.vip_card.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ApiVipCardOrderDetailVo {
    private Integer orderId;
    private String orderNo;
    private String orderFrom;
    private String orderType;
    private String outTradeNo;
    private Integer status;
    private String refundStatus;
    private Integer memberId;
    private String ip;
    private BigDecimal orderDiscountMoney;
    private BigDecimal orderMoney;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long payTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long closeTime;
    private Integer isDelete;
    private Integer isEnableRefund;
    private String remark;
    private Integer invoiceId;
    private String closeReason;
    private String orderFromName;
    private Map<String, Object> statusInfo;
    /** 订单项（与 PHP hasOne 一致，单对象） */
    private ApiVipCardOrderPageVo.VipCardOrderItemVo orderItem;
}
