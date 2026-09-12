package com.niu.core.service.core.vip_card.order.impl;

import com.niu.core.event.vip_card.order.AfterVipCardOrderCloseEvent;
import com.niu.core.event.vip_card.order.AfterVipCardOrderCreateEvent;
import com.niu.core.event.vip_card.order.AfterVipCardOrderPayEvent;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class CoreVipCardOrderEventServiceImpl implements ICoreVipCardOrderEventService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void orderCreateAfter(AfterVipCardOrderCreateEvent data) {
        eventPublisher.publishEvent(data);
    }

    @Override
    public void orderPayAfter(AfterVipCardOrderPayEvent data) {
        eventPublisher.publishEvent(data);
    }

    @Override
    public void orderCloseAfter(AfterVipCardOrderCloseEvent data) {
        eventPublisher.publishEvent(data);
    }
}