package com.niu.core.event.vip_card.order;

import com.niu.core.entity.vip_card.MemberVipCardOrder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AfterVipCardOrderCreateEvent extends ApplicationEvent {
    private final MemberVipCardOrder orderData;
    private final Integer orderId;
    private final Long time;
    private final String mainType;
    private final Integer mainId;

    public AfterVipCardOrderCreateEvent(Object source, MemberVipCardOrder orderData, Integer orderId,
                                        Long time, String mainType, Integer mainId) {
        super(source);
        this.orderData = orderData;
        this.orderId = orderId;
        this.time = time;
        this.mainType = mainType;
        this.mainId = mainId;
    }
}