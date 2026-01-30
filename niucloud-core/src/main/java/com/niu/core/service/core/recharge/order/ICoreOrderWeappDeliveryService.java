package com.niu.core.service.core.recharge.order;


/**
 * 充值订单流程
 */
public interface ICoreOrderWeappDeliveryService {

    /**
     * 微信小程序 发货信息录入接口
     *
     * @param orderId 订单ID
     * @return 微信小程序 发货信息录入接口结果
     */
    String orderShippingUploadShippingInfo(Integer orderId);

}
