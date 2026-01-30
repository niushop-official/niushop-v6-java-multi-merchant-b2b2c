package com.niu.mall.service.admin.goods.param;

import lombok.Data;

import java.util.List;

@Data
public class GoodsOutPutParam {
    private Integer status = 1;

    private String goodsName;

    private Integer siteId;

    private String goodsType;

    private Integer brandId;

    private String goodsCategory;

    private List<Integer> labelIds;

    private Integer goodsId;
}
