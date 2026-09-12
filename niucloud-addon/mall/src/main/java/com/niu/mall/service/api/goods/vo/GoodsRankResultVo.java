package com.niu.mall.service.api.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoodsRankResultVo {
    private Integer rankId;
    private String name;
    @JsonProperty("rank_num")
    private Integer rankNum;
}
