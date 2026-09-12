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
    private Integer goodsMallCategory;

    private String goodsType;

    private String selectType = "all";

    private List<Integer> goodsIds;

    private List<Integer> skuIds;

    private List<Integer> verifyGoodsIds;

    private List<Integer> verifySkuIds;

    private Integer isGift;

    /** 是否可选供货商商品，对应 need_supply_goods，默认 1 */
    private Integer needSupplyGoods = 1;

    /** 排除商品id，对应 exclude_goods_id，默认 0 */
    private Integer excludeGoodsId = 0;
}
