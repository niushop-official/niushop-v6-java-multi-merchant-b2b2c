package com.niu.mall.service.core.shop;

import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.service.core.shop.param.OrderRefundParam;

public interface ICoreShopSettlementService {

    void orderPay(MallOrder order);

    void orderRefund(OrderRefundParam orderRefundParam);

    void settlement(MallOrder order);

}
