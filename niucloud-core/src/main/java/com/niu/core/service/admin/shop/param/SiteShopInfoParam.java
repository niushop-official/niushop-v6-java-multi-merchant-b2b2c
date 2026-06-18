package com.niu.core.service.admin.shop.param;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 店铺扩展表参数
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteShopInfoParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteName参数缺失")
    private String siteName;

    @NotNull(message = "groupId参数缺失")
    private Integer groupId;

    /**
     * 应用列表 - 可以是字符串或数组
     */
    private Object app;

    /**
     * 插件列表 - 可以是字符串或数组
     */
    private Object addons;

    /**
     * 业务类型，group选择套餐 diy自定义
     */
    private String selectType = "group";

    /**
     * 获取应用列表
     */
    public List<String> getAppList() {
        if (app == null || (app instanceof String && ((String) app).isEmpty())) {
            return new ArrayList<>();
        }
        if (app instanceof List) {
            return (List<String>) app;
        }
        return new ArrayList<>();
    }

    /**
     * 获取插件列表
     */
    public List<String> getAddonsList() {
        if (addons == null || (addons instanceof String && ((String) addons).isEmpty())) {
            return new ArrayList<>();
        }
        if (addons instanceof List) {
            return (List<String>) addons;
        }
        return new ArrayList<>();
    }

    private String expireTime;

    /**
     * 站点域名
     */
    private String siteDomain = "";

    /**
     * 营业执照
     */
    private String businessLicense = "";

    /**
     * 商家分类id
     */
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
     * 佣金比率
     */
    private BigDecimal commissionRate = BigDecimal.ZERO;

    /**
     * 服务费率
     */
    private String supplyServiceRate;

    /**
     * 是否自营
     */
    private Integer isSelf = 0;
}