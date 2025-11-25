package com.niu.mall.service.core.goods;


public interface ICoreGoodsCartNumService {

    /**
     * 增加商品数量
     * @param goodsId 商品id
     * @param cartNum 数量
     */
    void inc(Integer goodsId, Integer cartNum);

    /**
     * 减少商品数量
     * @param goodsId 商品id
     * @param cartNum 数量
     */
    void dec(Integer goodsId, Integer cartNum);
}
