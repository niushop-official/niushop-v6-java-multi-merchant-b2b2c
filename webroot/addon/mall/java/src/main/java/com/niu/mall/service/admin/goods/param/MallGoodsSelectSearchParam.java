package com.niu.mall.service.admin.goods.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品参数
 */
@Data
public class MallGoodsSelectSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String keyword;

    //商户ID
    private Integer siteId;

    private Integer goodsCategory;

    //商品分类
    private Integer goodsMallCategory;

    private String goodsType;

    private String selectType = "all";

    private List<Integer> goodsIds;

    private List<Integer> skuIds;

    private List<Integer> verifyGoodsIds;

    private List<Integer> verifySkuIds;

    private Integer isGift;
}
