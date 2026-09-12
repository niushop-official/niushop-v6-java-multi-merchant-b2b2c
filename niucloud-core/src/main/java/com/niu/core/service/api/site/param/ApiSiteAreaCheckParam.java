package com.niu.core.service.api.site.param;

import lombok.Data;

/**
 * 检查店铺是否在配送范围内
 * 对齐 PHP SiteService::checkSiteInArea
 */
@Data
public class ApiSiteAreaCheckParam {

    private Integer siteId;

    private ApiSitePositionParam position;
}
