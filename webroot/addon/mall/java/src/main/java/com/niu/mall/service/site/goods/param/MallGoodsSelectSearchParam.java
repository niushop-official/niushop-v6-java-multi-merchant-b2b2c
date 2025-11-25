package com.niu.mall.service.site.goods.param;

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

    private Integer goodsCategory;

    private String goodsType;

    private String selectType = "all";

    private List<Integer> goodsIds;

    private List<Integer> skuIds;

    private List<Integer> verifyGoodsIds;

    private List<Integer> verifySkuIds;

    private Integer isGift;
}
