package com.niu.core.service.api.site.param;

import lombok.Data;

/**
 * 附近前置仓查询参数
 * 对齐 PHP SiteService::getNearestFrontWarehouse
 */
@Data
public class ApiSiteNearestFrontWarehouseParam {

    private ApiSitePositionParam position;
}
