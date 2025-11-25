package com.niu.mall.service.core.goods;

import com.niu.mall.entity.goods.MallGoodsEvaluate;

public interface ICoreGoodsEvaluateService {

    /**
     * 添加商品评价
     * @param mallGoodsEvaluate
     */
    Integer addEvaluate(MallGoodsEvaluate mallGoodsEvaluate);
}
