package com.niu.core.service.core.marketing;

import com.niu.core.entity.active.MallActiveGoods;

import java.util.List;

/**
 * 营销活动关联商品
 */
public interface ICoreActiveGoodsService {

    void insertAll(List<MallActiveGoods> list);

    /**
     * 优惠券适用商品信息缓存
     */
    void couponGoodsCache();
}
