package com.niu.core.listener.recharge.pay;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.refund.RefundSuccessEvent;
import com.niu.core.service.core.recharge.refund.ICoreRechargeRefundService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 退款成功
 */
@EventListen("core")
@Component
public class RefundSuccessListener extends AbstractListener {

    @Resource
    ICoreRechargeRefundService coreRechargeRefundService;

    public void handleEvent(RefundSuccessEvent event) {
        if (event.getTradeType().equals("recharge")) {
            coreRechargeRefundService.refundComplete(event.getRefund().getRefundNo());
        }
    }
}
