package com.niu.core.service.api.site.param;

import lombok.Data;

/**
 * C端店铺信息查询参数
 */
@Data
public class ApiSiteInfoParam {

    private Integer siteId;

    private ApiSitePositionParam position;
}
