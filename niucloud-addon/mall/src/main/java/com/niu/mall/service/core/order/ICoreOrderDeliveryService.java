package com.niu.mall.service.core.order;

import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDelivery;
import com.niu.mall.service.core.order.param.OrderDeliveryDataParam;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;

/**
 * 订单发货接口
 */
public interface ICoreOrderDeliveryService {

    /**
     * 订单发货
     */
    void delivery(OrderDeliveryParam orderDeliveryParam);

    /**
     * 虚拟发货
     */
    void virtual(OrderDeliveryDataParam orderDeliveryDataParam);

    /**
     * 包裹
     */
    Integer packages(Integer[] deliverIds,MallOrderDelivery mallOrderDelivery);

    /**
     * 物流包裹
     */
    void express(OrderDeliveryDataParam orderDeliveryDataParam);

    /**
     * 同城配送
     * @param orderDeliveryDataParam
     */
    void localDelivery(OrderDeliveryDataParam orderDeliveryDataParam);
    /**
     * 检测订单发货完成
     * @return
     */
    boolean checkFinish(OrderDeliveryParam orderDeliveryParam);

    /**
     * 订单发货完成
     * @return
     */
    boolean finish(OrderDeliveryParam orderDeliveryParam);

    /**
     * 微信小程序发货入口
     * @return
     */
    void orderShippingUploadShippingInfo(MallOrder order);

    /**
     * 确认收货提醒接口
     * @return
     */
    void orderShippingNotifyConfirmReceive(MallOrder order);
}
