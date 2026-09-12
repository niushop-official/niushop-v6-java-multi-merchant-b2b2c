package com.niu.core.service.admin.vip_card.order.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminVipCardOrderPageVo {
    private Integer orderId;
    private Integer memberId;
    private String orderNo;
    private String body;
    private String orderFrom;
    private String orderFromName;
    private String outTradeNo;
    private Integer status;
    private String statusName;
    private BigDecimal orderMoney;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long payTime;
    private String remark;
    private String payTypeName;

    @JsonIgnore
    private String payType;

    private MemberInfo member;
    private List<OrderItemInfo> orderItem;
    private PayInfo pay;

    @Data
    public static class MemberInfo {
        private Integer memberId;
        private String headimg;
        private String nickname;
        private String mobile;
        private String memberNo;
    }

    @Data
    public static class OrderItemInfo {
        private Integer orderItemId;
        private Integer orderId;
        private Integer itemId;
        private String itemName;
        private BigDecimal price;
        private Integer num;
        private BigDecimal itemMoney;
    }

    @Data
    public static class PayInfo {
        private String outTradeNo;
        private String type;
        private String typeName;
        private Integer status;
        // 其他支付字段...
    }
}