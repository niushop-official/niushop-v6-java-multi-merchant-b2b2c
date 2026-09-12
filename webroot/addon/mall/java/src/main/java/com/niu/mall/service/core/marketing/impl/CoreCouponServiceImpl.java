package com.niu.mall.service.core.marketing.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.coupon.MallCoupon;
import com.niu.core.enums.coupon.CouponStatusEnum;
import com.niu.core.mapper.coupon.MallCouponMapper;
import com.niu.mall.service.core.marketing.ICoreCouponService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class CoreCouponServiceImpl implements ICoreCouponService {

    @Resource
    MallCouponMapper mallCouponMapper;


    @Override
    public void couponNormal(List<Integer> couponIds) {

        MallCoupon mallCoupon = new MallCoupon();
        mallCoupon.setStatus(CouponStatusEnum.NORMAL.getStatus());
        mallCouponMapper.update(mallCoupon, new QueryWrapper<MallCoupon>()
                .eq("status", CouponStatusEnum.WAIT_START.getStatus())
                .in("id", couponIds));
    }

    @Override
    public void couponExpire(List<Integer> couponIds) {

        MallCoupon mallCoupon = new MallCoupon();
        mallCoupon.setStatus(CouponStatusEnum.EXPIRE.getStatus());
        mallCouponMapper.update(mallCoupon, new QueryWrapper<MallCoupon>()
                .eq("status", CouponStatusEnum.NORMAL.getStatus())
                .in("id", couponIds));
    }
}
