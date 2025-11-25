package com.niu.mall.service.api.goods.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ShopGoodsEvaluate列表视图
 */
@Data
public class MallGoodsEvaluateInGoodsVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long count;

    private List<MallGoodsEvaluateListVo> list;

}
