package com.niu.core.service.api.site.param;

import lombok.Data;

/**
 * C端检查配送范围 / 附近前置仓等位置参数
 * 对齐 PHP position 数组（latitude/longitude/province_id/city_id）
 */
@Data
public class ApiSitePositionParam {

    private String latitude;

    private String longitude;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    public boolean hasLatLng() {
        return latitude != null && !latitude.isBlank()
                && longitude != null && !longitude.isBlank();
    }
}
