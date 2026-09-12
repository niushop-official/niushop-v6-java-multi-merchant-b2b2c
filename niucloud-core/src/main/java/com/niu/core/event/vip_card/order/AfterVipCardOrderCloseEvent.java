package com.niu.core.event.vip_card.order;

import com.niu.core.entity.vip_card.MemberVipCardOrder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AfterVipCardOrderCloseEvent extends ApplicationEvent {
    private final Integer orderId;
    private final MemberVipCardOrder orderData;
    private final String mainType;
    private final Integer mainId;

    public AfterVipCardOrderCloseEvent(Object source, Integer orderId, MemberVipCardOrder orderData,
                                       String mainType, Integer mainId) {
        super(source);
        this.orderId = orderId;
        this.orderData = orderData;
        this.mainType = mainType;
        this.mainId = mainId;
    }
}
