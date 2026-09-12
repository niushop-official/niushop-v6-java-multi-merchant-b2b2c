package com.niu.core.service.admin.vip_card.card.param;

import lombok.Data;

@Data
public class AdminVipCardBuyPageParam {
    private String cardName;   // 会员卡名称（精确匹配）
    private String keyword;    // 搜索关键词（会员昵称/手机号/会员号）
    private Integer cardId;    // 会员卡ID
    private Long createTimeStart; // 开始时间戳
    private Long createTimeEnd;   // 结束时间戳
}