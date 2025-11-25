package com.niu.mall.service.admin.goods.param;

import lombok.Data;

import java.util.List;

@Data
public class MallGoodsRankBatchDeleteParam {
    private List<Integer> rankId;
}
