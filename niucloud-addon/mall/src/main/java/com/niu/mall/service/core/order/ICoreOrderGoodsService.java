package com.niu.mall.service.core.order;

import com.niu.mall.service.api.refund.vo.RefundListVo;
import com.niu.mall.service.core.order.param.OrderGoodsDeliveryParam;

public interface ICoreOrderGoodsService {


    /**
     * 订单完成
     * @return
     */
    boolean cancelOrderGoodsDelivery(OrderGoodsDeliveryParam param);
}