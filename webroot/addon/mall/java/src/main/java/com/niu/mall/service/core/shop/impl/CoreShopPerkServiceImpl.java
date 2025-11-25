package com.niu.mall.service.core.shop.impl;

import com.niu.core.enums.shop.ShopAccountEnum;
import com.niu.core.service.core.shop.ICoreShopAccountService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.service.core.shop.ICoreShopPerkService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreShopPerkServiceImpl implements ICoreShopPerkService {

    @Resource
    ICoreShopAccountService coreShopAccountService;
    @Override
    public void perk(MallOrder order) {
        coreShopAccountService.addLog(order.getSiteId(),order.getMallCouponMoney().subtract(order.getMallRefundCouponMoney()) , ShopAccountEnum.COUPON.getType(), "平台优惠券补贴", order.getOrderId().toString());
    }
}
