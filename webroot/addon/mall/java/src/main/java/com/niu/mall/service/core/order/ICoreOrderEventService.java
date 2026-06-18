package com.niu.mall.service.core.order;

import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.service.core.order.param.*;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;

/**
 * 订单事件
 */
public interface ICoreOrderEventService {

    /**
     * 订单关闭之后
     *
     * @param order
     * @param param
     */
    void orderCloseAfter(MallOrder order, OrderCloseParam param);

    /**
     * 订单创建之后
     *
     * @param vo
     */
    void orderCreateAfter(OrderCreateDataVo vo);

    /**
     * 订单支付之后
     *
     * @param order
     * @param param
     */
    void orderPayAfter(MallOrder order, OrderPayParam param);

    /**
     * 虚拟商品支付后自动发货
     */
    void handleVirtualOrderDelivery(MallOrder order);

    /**
     * 订单发货之后
     *
     * @param orderDeliveryDataParam
     * @return
     */
    void orderDeliveryAfter(MallOrder order, OrderDeliveryParam orderDeliveryDataParam);

    /**
     * 订单完成之后
     *
     * @param order
     */
    void orderFinishAfter(MallOrder order, OrderFinishParam orderFinishParam);

    /**
     * 订单结算
     *
     * @param order 订单信息
     */
    void orderSettlement(MallOrder order);

    /**
     * 订单结算完成
     *
     * @param order 订单信息
     */
    void orderSettlementFinish(MallOrder order);
}
