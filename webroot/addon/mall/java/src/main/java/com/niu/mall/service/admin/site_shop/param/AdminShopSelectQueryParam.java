package com.niu.mall.service.admin.site_shop.param;

import lombok.Data;

import java.util.List;

@Data
public class AdminShopSelectQueryParam {
    private String keyword;
    private Integer status;
    private Integer groupId;
    private List<Long> createTime;
    private Integer isSelf;
    private Integer categoryId;
    private List<Integer> siteIds;
    private List<Integer> verifySiteIds; // 需要校验的站点ID列表
}