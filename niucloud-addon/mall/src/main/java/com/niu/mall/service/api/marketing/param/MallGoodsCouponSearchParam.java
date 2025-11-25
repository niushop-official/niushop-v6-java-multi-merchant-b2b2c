package com.niu.mall.service.api.marketing.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券商品搜索参数
 */
@Data
public class MallGoodsCouponSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer goodsId;

    private Integer categoryId;

    private Integer mallCategoryId;

    private Integer brandId;
    //搜索关键词
    private String keyword;
    // 平台商品分类
    private String goodsMallCategory;
    // 商品分
    private String goodsCategory;
    //商品标签
    private String labelIds;
    //价格开始区间
    private String startPrice;
    //价格结束区间
    private String endPrice;
    private String order;
    private String sort;
    // 优惠券id
    private Integer couponId;

}
