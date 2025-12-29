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
    private Integer groupId;
    private List< Integer> categoryId;
    private Integer isSelf;
    private Integer isLocalDelivery;
    private Integer isLocation;
    private List<String> deliveryType;

    private PositionInfo position;
    private RegionInfo region;

    @Data
    public static class PositionInfo{
        private String latitude;
        private String longitude;
    }

    @Data
    public static class RegionInfo{
        private Integer cityId;
        private Integer districtId;
        private Integer provinceId;
    }
}
