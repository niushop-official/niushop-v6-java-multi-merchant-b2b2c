package com.niu.core.service.site.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺扩展表参数
 */
@Data
public class SiteShopInfoParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteName参数缺失")
    private String siteName;

    @NotNull(message = "groupId参数缺失")
    private Integer groupId;
    private String expireTime;

    /**
     * 站点域名
     */
    private String siteDomain = "";

    /**
     * 营业执照
     */
    private String businessLicense = "";

    /** 商家分类id */
    @NotNull(message = "categoryId参数缺失")
    private Integer categoryId;

    /**
     * 手机号码
     */
    private String phone;

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

    /**
     * 服务费率
     */
    private double supplyServiceRate;

    /**
     * 是否自营
     */
    private Integer isSelf = 0;
}