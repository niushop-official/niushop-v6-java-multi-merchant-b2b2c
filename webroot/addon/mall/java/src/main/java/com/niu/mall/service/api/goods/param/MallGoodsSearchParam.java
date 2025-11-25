package com.niu.mall.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品参数
 */
@Data
public class MallGoodsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String keyword;


    //平台分类
    private String goodsMallCategory;

    //商品分类
    private String goodsCategory;



    private Integer brandId;

    private Integer labelIds;

    private Double startPrice = 0.00;

    private Double endPrice = 0.00;

    private String order;

    private String sort = "desc";

    private Integer couponId;

    private Integer siteId = 0;

    private Integer[] goodsIds;

    private Integer num=0;
}
