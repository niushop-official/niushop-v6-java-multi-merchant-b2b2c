package com.niu.mall.service.core.goods.param;

import lombok.Data;

@Data
public class ShopGoodsRankConfigParam {
    private String rankImages;
    private String noColor;
    private String selectColor;
    private String selectBgColorStart;
    private String selectBgColorEnd;
    private Integer searchStyleType;
    private Integer siteId;
}
