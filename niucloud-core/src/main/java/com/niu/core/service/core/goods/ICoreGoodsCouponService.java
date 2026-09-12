package com.niu.core.service.core.goods;

import com.niu.core.service.core.goods.param.GoodsStatusEditParam;
import com.niu.core.service.core.goods.vo.CouponGoodsVo;

import java.util.List;

public interface ICoreGoodsCouponService {

    /**
     * 获取优惠券适用商品（单个）
     */
    List<CouponGoodsVo> getCouponGoodsOne(Integer couponId);
}
