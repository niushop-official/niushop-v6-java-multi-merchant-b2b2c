package com.niu.mall.service.api.goods.param;

import lombok.Data;

@Data
public class MallGoodsRankGoodsPageParam {
    private Integer rankId;
    private Integer page;
    private Integer limit;
}
