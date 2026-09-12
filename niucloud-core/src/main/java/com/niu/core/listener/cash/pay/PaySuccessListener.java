package com.niu.core.listener.cash.pay;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.pay.PaySuccessEvent;
import com.niu.core.service.core.cash.order.ICoreCashOrderPayService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 买单支付成功回调
 */
@EventListen("core")
@Component
public class PaySuccessListener extends AbstractListener {

    @Resource
    ICoreCashOrderPayService coreCashOrderPayService;

    public void handleEvent(PaySuccessEvent event) {
        if ("cash".equals(event.getTradeType())) {
            coreCashOrderPayService.pay(event.getTradeId(), event.getPay().getOutTradeNo());
        }
    }
}
