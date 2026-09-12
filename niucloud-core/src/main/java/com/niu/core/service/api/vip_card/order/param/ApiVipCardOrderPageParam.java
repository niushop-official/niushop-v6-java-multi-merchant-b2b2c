package com.niu.core.service.api.vip_card.order.param;

import lombok.Data;

import java.util.List;

@Data
public class ApiVipCardOrderPageParam {
    /** 订单状态，空表示全部 */
    private String status;
    /** 创建时间区间 [start, end]，日期字符串 */
    private List<String> createTime;
}
