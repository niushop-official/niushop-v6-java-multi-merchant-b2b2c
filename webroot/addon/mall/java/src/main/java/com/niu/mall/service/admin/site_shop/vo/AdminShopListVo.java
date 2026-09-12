package com.niu.mall.service.admin.site_shop.vo;

import lombok.Data;

@Data
public class AdminShopListVo {
    private Integer siteId;
    private Integer isSelf;
    private Integer businessStatus;
    private Integer groupId;
    private Integer categoryId;
    private String siteName;
    private String frontEndName;
    private String frontEndLogo;
    private String logo;
    private String icon;
    private Integer status;
    private Long createTime;
    private String statusName;
    private String appName;
    private String businessStatusName;
    private String categoryName;
    private String groupName;
}