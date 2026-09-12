package com.niu.core.service.admin.vip_card.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.member.Member;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class AdminVipCardOrderDetailVo {
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

    private Member member;
    private List<OrderItemInfo> orderItem;
    private List<OrderLogInfo> orderLog = List.of();
    private CardBuyInfo cardBuy;
    private PayInfo pay;

    @Data
    public static class OrderItemInfo {
        private Integer orderItemId;
        private Integer orderId;
        private Integer cardId;
        private Integer itemId;
        private String cardName;
        private String itemName;
        private BigDecimal price;
        private Integer num;
        private BigDecimal itemMoney;
    }

    @Data
    public static class OrderLogInfo {
        private Integer orderId;
        private String mainType;
        private String mainTypeName;
        private Integer mainId;
        private String mainName;
        private String type;
        private String typeName;
        private String content;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;
    }

    @Data
    public static class CardBuyInfo {
        private Long id;
        private Integer siteId;
        private Integer orderId;
        private Integer memberId;
        private Integer cardId;
        private Integer itemId;
        private Integer isExpire;
        private Integer isTrial;
        private Integer trialDay;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long expireTime;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;
        private Map<String, Object> benefitsConfig;
        private Map<String, Object> itemConfig;
        private List<BenefitsConfigContentItem> benefitsConfigContent = List.of();
    }

    @Data
    public static class BenefitsConfigContentItem {
        private String content;
    }

    @Data
    public static class PayInfo {
        private String outTradeNo;
        private String type;
        private String typeName;
        private Integer status;
        // ...
    }
}