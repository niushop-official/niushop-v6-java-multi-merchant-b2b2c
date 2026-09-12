package com.niu.core.service.admin.marketing;

import com.niu.core.entity.coupon.MallCouponGoods;

import java.util.List;

/**
 * 优惠券商品或品类关联服务接口
 */
public interface IMallCouponGoodsService {

    void insertAll(List<MallCouponGoods> list);
}
