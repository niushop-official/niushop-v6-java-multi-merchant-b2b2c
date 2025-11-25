package com.niu.mall.service.core.shop;

import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.service.core.shop.param.OrderRefundParam;

public interface ICoreShopPerkService {

    /**
     * 订单完成后补贴
     * @param order
     */
    void perk(MallOrder order);

}
