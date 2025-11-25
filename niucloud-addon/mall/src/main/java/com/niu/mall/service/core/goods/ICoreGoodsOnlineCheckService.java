package com.niu.mall.service.core.goods;

import java.util.List;

public interface ICoreGoodsOnlineCheckService {

    void checkGoodsStatus(List<Integer> goodsIds);

    void checkGoodsSkuPriceBatch(List<Integer> goodsIds);
}
