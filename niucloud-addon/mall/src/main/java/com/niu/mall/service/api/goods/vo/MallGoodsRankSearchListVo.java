package com.niu.mall.service.api.goods.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MallGoodsRankSearchListVo {
    private Integer rankId;
    private String name;
    private JSONObject config;
    @JsonProperty("goods_list")
    private List<MallGoodsRankGoodsListInfo> goodsList;
}
