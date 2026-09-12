package com.niu.mall.service.admin.site_shop.vo;

import com.niu.core.entity.addon.Addon;
import com.niu.core.service.admin.site.vo.SiteCategoryListVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminShopInfoVo {
    private Integer siteId;
    private String siteName;
    private String frontEndName;
    private String frontEndLogo;
    private String appType;
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
    private BigDecimal shopDeposit;
    private BigDecimal money;
    private BigDecimal moneyGet;
    private BigDecimal moneyCashOuting;
    private Integer isRecommend;
    private Integer isSelf;
    private Integer categoryId;
    private Integer childCategoryId;
    private Integer circleId;
    private BigDecimal serviceRate;
    private List<Addon> siteAddons;
    private BigDecimal withdrawalMoney;
    private List<SiteCategoryListVo> childCategoryList;
    // 关联分类名称（通过with）
    private String categoryName;
    private String childCategoryName;
    private String groupName;
}