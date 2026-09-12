package com.niu.mall.service.admin.site_shop.param;

import lombok.Data;

import java.util.List;

@Data
public class AdminShopPageQueryParam {
    private String keyword;
    private Integer status;
    private Integer groupId;
    private List<Long> createTime;   // [start, end] 时间戳
    private List<Long> expireTime;
    private Integer isSelf;
    private Integer categoryId;
    private String app;
    private List<Integer> circleIds; // 商圈过滤（内部使用）
}