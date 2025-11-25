package com.niu.mall.service.admin.goods.param;

import lombok.Data;

import java.util.List;

@Data
public class MallGoodsRankSelectPageParam {
    private String name;
    private String rankType;
    private Integer status;
    private List<Integer> verifyRankIds;
}
