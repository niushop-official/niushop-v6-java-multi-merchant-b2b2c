package com.niu.core.service.admin.vip_card.order.param;

import lombok.Data;

import java.util.List;

@Data
public class AdminVipCardOrderPageParam {
    private String keyword;              // 搜索关键词（会员昵称/手机号/会员号）
    private String payType;              // 支付类型（all 或其他）
    private String searchType;           // 搜索类型（order_no / out_trade_no / card_name）
    private String searchName;           // 搜索内容
    private String orderFrom;            // 订单来源
    private String status;               // 订单状态
    private List<Long> createTime;     // 创建时间区间
    private List<Long> payTime;        // 支付时间区间
}