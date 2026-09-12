package com.niu.core.service.api.site.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * C端店铺信息
 */
@Data
public class ApiSiteInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String siteName;

    private String frontEndLogo;

    private String latitude;

    private String longitude;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    private String address;

    private String fullAddress;

    private BigDecimal distance;

    private Map<String, Object> qrcodeCashConfig;
}
