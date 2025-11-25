package com.niu.mall.service.core.goods;

import com.niu.mall.service.core.goods.param.ShopGoodsRankConfigParam;
import com.niu.mall.service.core.goods.vo.ShopGoodsRankConfigVo;

public interface ICoreGoodsRankService {
    void setGoodsRankConfig(ShopGoodsRankConfigParam param);

    ShopGoodsRankConfigVo getGoodsRankConfig();
}
