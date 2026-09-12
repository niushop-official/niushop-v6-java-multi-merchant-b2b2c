package com.niu.core.service.admin.marketing.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.entity.coupon.MallCouponGoods;
import com.niu.core.mapper.coupon.MallCouponGoodsMapper;
import com.niu.core.service.admin.marketing.IMallCouponGoodsService;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * 优惠券商品或品类关联实现
 */
@Service
public class MallCouponGoodsServiceImpl extends ServiceImpl<MallCouponGoodsMapper, MallCouponGoods> implements IMallCouponGoodsService {


    @Override
    public void insertAll(List<MallCouponGoods> list) {
        super.saveOrUpdateBatch(list);
    }
}
