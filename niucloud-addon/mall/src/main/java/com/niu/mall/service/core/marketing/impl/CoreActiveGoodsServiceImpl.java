package com.niu.mall.service.core.marketing.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.mall.entity.active.MallActiveGoods;
import com.niu.mall.entity.coupon.MallCoupon;
import com.niu.mall.enums.coupon.CouponReceiveTypeEnum;
import com.niu.mall.enums.coupon.CouponStatusEnum;
import com.niu.mall.mapper.active.MallActiveGoodsMapper;
import com.niu.mall.mapper.coupon.MallCouponMapper;
import com.niu.mall.service.core.goods.ICoreGoodsCouponService;
import com.niu.mall.service.core.goods.vo.CouponGoodsVo;
import com.niu.mall.service.core.marketing.ICoreActiveGoodsService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺营销活动实现
 */
@Log4j2
@Service
public class CoreActiveGoodsServiceImpl extends ServiceImpl<MallActiveGoodsMapper, MallActiveGoods> implements ICoreActiveGoodsService {

    @Resource
    private Cached cached;

    @Resource
    private MallCouponMapper couponMapper;

    @Resource
    private ICoreGoodsCouponService coreGoodsCouponService;


    @Override
    public void insertAll(List<MallActiveGoods> list) {
        super.saveOrUpdateBatch(list);
    }

    @Override
    public void couponGoodsCache() {
        log.info("优惠券适用商品信息同步");
        try {
            String cacheName = "coupon_goods_list";
            String cacheTagName = cacheName;
            cached.tag(cacheTagName).clear();

            cached.cache(cacheTagName,cacheName, uniqueKey -> {
                QueryWrapper<MallCoupon> couponWap = new QueryWrapper<>();
                couponWap.eq("site_id", 0);
                couponWap.eq("status", CouponStatusEnum.NORMAL.getStatus());
                couponWap.eq("receive_type", CouponReceiveTypeEnum.USER.getType());
                List<MallCoupon> list = couponMapper.selectList(couponWap);
                List<CouponGoodsVo> couponGoodsListAll = new ArrayList<>();
                for (MallCoupon coupon : list){
                    List<CouponGoodsVo> couponGoodsList = coreGoodsCouponService.getCouponGoodsOne(coupon.getId());
                    if(!couponGoodsList.isEmpty()){
                        couponGoodsListAll.addAll(couponGoodsList);
                    }
                }
                return couponGoodsListAll;
            });

        }catch (Exception e){
            log.error("优惠券适用商品信息同步失败",e);
        }
    }
}
