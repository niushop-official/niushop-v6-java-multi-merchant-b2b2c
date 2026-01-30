package com.niu.mall.service.core.order;

import com.niu.mall.entity.order.MallOrder;

/**
 * 订单结算服务接口
 */
public interface ICoreOrderWeappDeliveryService {

    /**
     * 微信小程序 发货信息录入接口
     */
    String orderShippingUploadShippingInfo(Integer orderId,Integer siteId);

    /**
     * 确认收货提醒接口
     */
    String orderShippingNotifyConfirmReceive(String outTradeNo, String deliveryType);
}

