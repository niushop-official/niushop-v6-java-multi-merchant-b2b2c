package com.niu.mall.service.admin.site_shop.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.util.Map;

@Data
public class AdminShopApplyPageVo {
    private Integer applyId;
    private Integer siteId;
    private Integer memberId;
    private String siteName;
    private String userName;
    private String userMobile;
    private Integer categoryId;
    private Integer childCategoryId;
    private String businessLicense;
    private Integer status;
    private String statusName;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long auditTime;

    private Integer openStatus;
    private String openStatusName;
    private Integer uid;
    private String applyType;
    private Integer circleId;
    private String account;
    private String password;
    private String nickname;
    @JsonProperty("username")
    private String loginUsername;
    private String headImg;
    private String categoryName;
    private String childCategoryName;
    private Map<String, Object> user;
}
