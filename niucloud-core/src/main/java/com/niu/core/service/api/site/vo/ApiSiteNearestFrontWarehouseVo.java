package com.niu.core.service.api.site.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 附近前置仓
 * 对齐 PHP SiteService::getNearestFrontWarehouse 返回字段
 */
@Data
public class ApiSiteNearestFrontWarehouseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String siteName;

    private String logo;

    private String frontEndLogo;

    private String latitude;

    private String longitude;

    private String fullAddress;

    private String businessHours;

    private String keywords;

    private BigDecimal siteScore;

    private Integer saleNum;

    private Integer evaluateNum;

    /** 距离（公里），有定位时返回 */
    private BigDecimal distance;
}
