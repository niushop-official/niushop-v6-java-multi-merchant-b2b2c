package com.niu.mall.service.admin.site_shop.param;

import lombok.Data;

import java.util.List;

@Data
public class AdminShopListQueryParam {
    private String keyword;
    private Integer status;
    private Integer groupId;
    private List<Long> createTime;
    private List<Long> expireTime;
    private Integer isSelf;
    private Integer categoryId;
}