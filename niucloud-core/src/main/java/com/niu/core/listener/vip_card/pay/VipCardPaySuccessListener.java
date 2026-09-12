package com.niu.core.listener.vip_card.pay;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.pay.PaySuccessEvent;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderPayService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 支付成功
 */
@EventListen("core")
@Component
public class VipCardPaySuccessListener extends AbstractListener {

    @Resource
    ICoreVipCardOrderPayService payService;

    public void handleEvent(PaySuccessEvent event) {
        if ("vip_card".equals(event.getTradeType())) {
            payService.pay(event.getPay());
        }
    }
}
