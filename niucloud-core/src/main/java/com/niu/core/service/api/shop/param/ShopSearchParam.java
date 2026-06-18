package com.niu.core.service.api.shop.param;


import cn.hutool.json.JSONArray;
import lombok.Data;

import java.util.List;

@Data
public class ShopSearchParam {

    // 页码
    private Integer page = 1;
    // 每页数量
    private Integer limit = 10;
    private Integer num=0;
    private Object siteIds;
    private String keyword;
    private String order;
    private Integer groupId;
    private List< Integer> categoryId;
    private Integer isSelf;
    private Integer isLocalDelivery;
    private Integer isLocation;
    private List<String> deliveryType;
    private Position position;
    @Data
    public static class Position {
        protected String latitude;
        protected String longitude;
        protected String provinceId;
        protected String districtId;
        protected String cityId;

    }
}
