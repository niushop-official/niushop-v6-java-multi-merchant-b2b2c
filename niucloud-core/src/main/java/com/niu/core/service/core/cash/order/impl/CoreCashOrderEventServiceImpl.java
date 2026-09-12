package com.niu.core.service.core.cash.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.event.cash.AfterCashOrderCloseEvent;
import com.niu.core.event.cash.AfterCashOrderCreateEvent;
import com.niu.core.event.cash.AfterCashOrderPayEvent;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.cash.order.ICoreCashOrderEventService;
import com.niu.core.service.core.cash.order.param.AfterCashOrderCloseParam;
import com.niu.core.service.core.cash.order.param.AfterCashOrderCreateParam;
import com.niu.core.service.core.cash.order.param.AfterCashOrderPayParam;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CoreCashOrderEventServiceImpl implements ICoreCashOrderEventService {

    @Override
    @Async
    public void orderCreateAfter(AfterCashOrderCreateParam param) {
        AfterCashOrderCreateEvent event = new AfterCashOrderCreateEvent();
        event.setName("AfterCashOrderCreate");
        event.addAppSign("core");
        event.setOrderId(param.getOrderId());
        event.setOrderData(param.getOrderData());
        event.setBasic(param.getBasic());
        event.setMainType(param.getMainType());
        event.setMainId(param.getMainId());
        event.setTime(ObjectUtil.defaultIfNull(param.getTime(), System.currentTimeMillis() / 1000));
        if (param.getSiteId() != null) {
            event.setSiteId(param.getSiteId());
        } else if (param.getOrderData() != null) {
            event.setSiteId(param.getOrderData().getSiteId());
        }
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    @Override
    @Async
    public void orderPayAfter(AfterCashOrderPayParam param) {
        AfterCashOrderPayEvent event = new AfterCashOrderPayEvent();
        event.setName("AfterCashOrderPay");
        event.addAppSign("core");
        event.setOrderId(param.getOrderId());
        event.setOrderData(param.getOrderData());
        event.setMainType(param.getMainType());
        event.setMainId(param.getMainId());
        if (param.getOrderData() != null) {
            event.setSiteId(param.getOrderData().getSiteId());
        }
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    @Override
    @Async
    public void orderCloseAfter(AfterCashOrderCloseParam param) {
        AfterCashOrderCloseEvent event = new AfterCashOrderCloseEvent();
        event.setName("AfterCashOrderClose");
        event.addAppSign("core");
        event.setOrderId(param.getOrderId());
        event.setOrderData(param.getOrderData());
        event.setMainType(param.getMainType());
        event.setMainId(param.getMainId());
        if (param.getOrderData() != null) {
            event.setSiteId(param.getOrderData().getSiteId());
        }
        EventAndSubscribeOfPublisher.publishAll(event);
    }
}
