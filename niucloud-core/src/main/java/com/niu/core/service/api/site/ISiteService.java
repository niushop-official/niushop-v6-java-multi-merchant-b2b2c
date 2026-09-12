package com.niu.core.service.api.site;

import com.niu.core.service.api.site.param.ApiSiteAreaCheckParam;
import com.niu.core.service.api.site.param.ApiSiteCircleListParam;
import com.niu.core.service.api.site.param.ApiSiteInfoParam;
import com.niu.core.service.api.site.param.ApiSiteNearestFrontWarehouseParam;
import com.niu.core.service.api.site.vo.ApiSiteAreaCheckVo;
import com.niu.core.service.api.site.vo.ApiSiteInfoVo;
import com.niu.core.service.api.site.vo.ApiSiteNearestFrontWarehouseVo;

import java.util.List;

/**
 * C端站点服务接口
 */
public interface ISiteService {

    /**
     * 获取商圈列表（不分页），对齐 PHP api SiteService::getCircleList
     */
    List<?> getCircleList(ApiSiteCircleListParam param);

    /**
     * 店铺信息，对齐 PHP api SiteService::getInfo
     */
    ApiSiteInfoVo getInfo(ApiSiteInfoParam param);

    /**
     * 检查店铺是否在配送范围内，对齐 PHP SiteService::checkSiteInArea
     */
    ApiSiteAreaCheckVo checkSiteInArea(ApiSiteAreaCheckParam param);

    /**
     * 附近前置仓，对齐 PHP SiteService::getNearestFrontWarehouse
     */
    ApiSiteNearestFrontWarehouseVo getNearestFrontWarehouse(ApiSiteNearestFrontWarehouseParam param);
}
