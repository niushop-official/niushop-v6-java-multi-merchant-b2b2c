package com.niu.mall.service.admin.site_shop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.admin.site.vo.SiteAdminVo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminShopPageVo {
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
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    private BigDecimal serviceRate;
    private Integer childCategoryId;
    private String app;
    private String appName;
    private String statusName;
    private String businessStatusName;
    private SiteAdminVo admin;
    private String categoryName; // 关联分类名称
    private String childCategoryName;
}