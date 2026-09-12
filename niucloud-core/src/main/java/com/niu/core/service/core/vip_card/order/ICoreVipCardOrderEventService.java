package com.niu.core.service.core.vip_card.order;


import com.niu.core.event.vip_card.order.AfterVipCardOrderCloseEvent;
import com.niu.core.event.vip_card.order.AfterVipCardOrderCreateEvent;
import com.niu.core.event.vip_card.order.AfterVipCardOrderPayEvent;

public interface ICoreVipCardOrderEventService {

    /**
     * 订单创建后事件
     */
    void orderCreateAfter(AfterVipCardOrderCreateEvent data);

    /**
     * 订单支付后事件
     */
    void orderPayAfter(AfterVipCardOrderPayEvent data);

    /**
     * 订单关闭后事件
     */
    void orderCloseAfter(AfterVipCardOrderCloseEvent data);
}