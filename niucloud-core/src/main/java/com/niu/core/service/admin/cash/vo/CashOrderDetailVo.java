package com.niu.core.service.admin.cash.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class CashOrderDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;

    private Integer siteId;

    private String siteName;

    private String orderNo;

    private String orderFrom;

    private String orderFromName;

    private String outTradeNo;

    private Integer status;

    private Map<String, Object> statusInfo;

    private Integer refundStatus;

    private Integer memberId;

    private BigDecimal totalMoney;

    private BigDecimal noDiscountMoney;

    private BigDecimal discountableMoney;

    private BigDecimal orderMoney;

    private BigDecimal discountMoney;

    private BigDecimal platDiscountMoney;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long payTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long closeTime;

    private String shopRemark;

    private String memberRemark;

    private String payTypeName;

    private MemberBriefInfoVo member;
}
