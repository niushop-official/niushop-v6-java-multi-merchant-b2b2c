package com.niu.mall.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品评价查询参数
 */
@Data
public class MallGoodsEvaluateSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsId;
    private List<String> scores;

}
