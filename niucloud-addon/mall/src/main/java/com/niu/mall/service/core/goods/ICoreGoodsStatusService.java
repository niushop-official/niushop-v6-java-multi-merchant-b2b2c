package com.niu.mall.service.core.goods;

import com.niu.core.service.core.goods.param.GoodsStatusEditParam;

import java.util.List;

public interface ICoreGoodsStatusService {

    /**
     * 修改商品上下架状态
     */
    void editStatus(List<GoodsStatusEditParam> params);
}
