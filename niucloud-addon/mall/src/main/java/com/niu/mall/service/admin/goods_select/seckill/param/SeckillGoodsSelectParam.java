package com.niu.mall.service.admin.goods_select.seckill.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SeckillGoodsSelectParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("goods_name")
    private String goodsName;

    @JsonProperty("goods_mall_category")
    private List<Integer> goodsMallCategory;

    @JsonProperty("goods_category")
    private String goodsCategory;

    @JsonProperty("goods_type")
    private String goodsType;

    @JsonProperty("brand_id")
    private Integer brandId;

    @JsonProperty("label_ids")
    private List<Integer> labelIds;
}
