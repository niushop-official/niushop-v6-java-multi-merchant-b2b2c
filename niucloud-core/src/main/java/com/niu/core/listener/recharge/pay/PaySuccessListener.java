package com.niu.core.listener.recharge.pay;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.pay.PaySuccessEvent;
import com.niu.core.service.core.recharge.order.ICoreOrderPayService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 支付成功
 */
@EventListen("core")
@Component
public class PaySuccessListener extends AbstractListener {

    @Resource
    ICoreOrderPayService coreOrderService;

    public void handleEvent(PaySuccessEvent event) {
        if (event.getTradeType().equals("recharge")) {
            coreOrderService.pay(event.getTradeId(), event.getPay().getOutTradeNo());
        }
    }
}
