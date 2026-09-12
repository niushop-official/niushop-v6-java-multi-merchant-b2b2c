package com.niu.mall.service.site.shop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopSettingInfoVo {
    private Integer siteId;
    private String siteName;
    private String frontEndName;
    private String frontEndLogo;
    private String keywords;
    private String logo;
    private String icon;
    private String desc;
    private Integer status;
    private String latitude;
    private String longitude;
    private Integer provinceId;
    private Integer cityId;
    private Integer districtId;
    private String address;
    private String fullAddress;
    private String phone;
    private String businessHours;
    private Integer groupId;
    private String app;
    private String addons;
    private String siteDomain;
    private String businessLicense;
    private String contactName;
    private Integer isRecommend;
    private Integer isSelf;
    private Integer categoryId;
    private Integer childCategoryId;
    private BigDecimal serviceRate;
    private Integer businessStatus;
    private String backgroundH5;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    // 关联分类名称
    private String categoryName;
    private String childCategoryName;
    private String groupName;
}