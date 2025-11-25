package com.niu.core.service.admin.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺扩展表参数
 */
@Data
public class SiteShopBasicInfoParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteName参数缺失")
    private String siteName;

    /**
     * 手机号码
     */
    private String phone;

    private String logo;

    private String icon;

    private String keywords;

    private String desc;

    private String frontEndName;

    private String frontEndLogo;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 省份ID
     */
    private Integer provinceId;

    /**
     * 城市ID
     */
    private Integer cityId;

    /**
     * 区域ID
     */
    private Integer districtId;

    /**
     * 地址
     */
    private String address;

    /**
     * 完整地址
     */
    private String fullAddress;

    private String businessHours;

    private Integer businessStatus;
}