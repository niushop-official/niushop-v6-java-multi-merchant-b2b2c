package com.niu.mall.service.site.shop.param;

import lombok.Data;

@Data
public class ShopSettingParam {
    private String siteName;
    private String phone;
    private String logo;
    private String icon;
    private String keywords;
    private String desc;
    private String frontEndName;
    private String frontEndLogo;
    private String latitude;
    private String longitude;
    private Integer provinceId;
    private Integer cityId;
    private Integer districtId;
    private String address;
    private String fullAddress;
    private String businessHours;
    private Integer businessStatus = 1;
    private String backgroundH5;
    private String contactName;
}
