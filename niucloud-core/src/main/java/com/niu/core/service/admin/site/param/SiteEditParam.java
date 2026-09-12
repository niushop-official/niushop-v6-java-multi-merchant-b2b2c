package com.niu.core.service.admin.site.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 编辑站点参数
 */
@Data
public class SiteEditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteName参数缺失")
    private String siteName;

    private Integer groupId;

    private String expireTime;

    private String siteDomain;

    private Integer isSelf;

    //@NotNull(message = "categoryId参数缺失")
    private Integer categoryId;

    private Integer childCategoryId;
    private Integer circleId;

    @NotNull(message = "phone参数缺失")
    private String phone;
    private String frontEndLogo;
    private String businessLicense;
    /** 联系人姓名 */
    private String contactName;
    private String latitude;
    private String longitude;
    private Integer provinceId;
    private Integer cityId;
    private Integer districtId;
    private String address;
    private String fullAddress;
    private BigDecimal serviceRate;
}
