package com.niu.mall.service.admin.site_shop.param;

import lombok.Data;

import java.util.List;

@Data
public class AdminShopApplyPageQueryParam {
    private Integer status;
    private String[] createTime;
    private Integer categoryId;
    private Integer groupId;
    private String keywords;
    private List<Integer> circleIds;
}
