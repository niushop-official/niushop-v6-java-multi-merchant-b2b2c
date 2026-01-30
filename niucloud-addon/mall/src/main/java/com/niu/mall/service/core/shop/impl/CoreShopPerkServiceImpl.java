package com.niu.mall.service.core.shop.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.enums.shop.ShopAccountEnum;
import com.niu.core.service.core.shop.ICoreShopAccountService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.shop.ICoreShopPerkService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CoreShopPerkServiceImpl implements ICoreShopPerkService {

    @Resource
    ICoreShopAccountService coreShopAccountService;
    @Resource
    MallOrderMapper mallOrderMapper;
    @Override
    public void perk(MallOrder order) {
        BigDecimal realMallCouponMoney = order.getMallCouponMoney().subtract(order.getMallRefundCouponMoney());
        if(realMallCouponMoney.compareTo(BigDecimal.ZERO) > 0){
            coreShopAccountService.addLog(order.getSiteId(),order.getMallCouponMoney().subtract(order.getMallRefundCouponMoney()) , ShopAccountEnum.COUPON.getType(), "平台优惠券补贴", order.getOrderId().toString());
        }
        mallOrderMapper.update(new MallOrder(), new UpdateWrapper<MallOrder>().eq("order_id", order.getOrderId())
                .set("perk_status", 3).set("perk_time", System.currentTimeMillis()/1000));

    }
}
