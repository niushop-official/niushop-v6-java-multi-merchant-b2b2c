package com.niu.mall.service.admin.site_shop.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminShopSelectVo {
    private Integer siteId;
    private String siteName;
    private String logo;
    private Integer status;
    private Long createTime;
    private Integer groupId;
    private Integer isRecommend;
    private Integer isSelf;
    private Integer categoryId;
    private String contactName;
    private String contactMobile;
    private String statusName;
    // 扩展校验字段
    private List<Integer> verifySiteIds;
}