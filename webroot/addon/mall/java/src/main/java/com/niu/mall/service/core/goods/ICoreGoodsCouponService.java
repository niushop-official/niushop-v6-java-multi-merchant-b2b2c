package com.niu.mall.service.core.goods;

import com.niu.mall.service.core.goods.param.GoodsStatusEditParam;
import com.niu.mall.service.core.goods.vo.CouponGoodsVo;

import java.util.List;

public interface ICoreGoodsCouponService {

    /**
     * 获取优惠券适用商品（单个）
     */
    List<CouponGoodsVo> getCouponGoodsOne(Integer couponId);
}
