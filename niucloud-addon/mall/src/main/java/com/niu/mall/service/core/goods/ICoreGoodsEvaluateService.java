package com.niu.mall.service.core.goods;

import com.niu.mall.entity.goods.MallGoodsEvaluate;
import com.niu.mall.service.core.goods.param.ShopScoreCalcuParam;

public interface ICoreGoodsEvaluateService {

    /**
     * 添加商品评价
     * @param mallGoodsEvaluate
     */
    Integer addEvaluate(MallGoodsEvaluate mallGoodsEvaluate);

    /**
     * 店铺评价分数计算
     * @param shopScoreCalcuParam
     */
    void shopScoreCalculate(ShopScoreCalcuParam shopScoreCalcuParam);
}
