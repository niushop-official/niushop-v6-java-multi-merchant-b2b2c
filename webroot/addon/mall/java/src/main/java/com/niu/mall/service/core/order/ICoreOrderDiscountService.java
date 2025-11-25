package com.niu.mall.service.core.order;

import com.niu.mall.entity.order.MallOrderDiscount;

import java.util.List;

public interface ICoreOrderDiscountService {

    /**
     * 添加订单优惠
     *
     * @param list
     */
    void inserAll(List<MallOrderDiscount> list);
}
