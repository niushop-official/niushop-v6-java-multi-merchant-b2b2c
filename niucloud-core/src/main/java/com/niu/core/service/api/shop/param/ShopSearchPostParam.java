package com.niu.core.service.api.shop.param;


import lombok.Data;

import java.util.List;

@Data
public class ShopSearchPostParam {

    // 页码
    private Integer page = 1;
    // 每页数量
    private Integer limit = 10;
    private Integer num=0;
    private String[] siteIds;
    private String keyword;
    private String order;
    private String sort = "desc";
    private Integer isOnlyArea = 0;
    private Integer groupId;
    private Object categoryId;
    private Integer isSelf;
    private Integer isLocalDelivery;
    private Integer isLocation;
    private Object deliveryType;

    private PositionInfo position;
    private RegionInfo region;

    @Data
    public static class PositionInfo{
        private String latitude;
        private String longitude;
        private Integer cityId;
        private Integer provinceId;
    }

    @Data
    public static class RegionInfo{
        private Integer cityId;
        private Integer districtId;
        private Integer provinceId;
    }
}
