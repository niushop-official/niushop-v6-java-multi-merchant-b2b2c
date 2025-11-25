package com.niu.mall.listener.pay;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.refund.RefundSuccessEvent;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.refund.ICoreRefundService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 退款成功
 */
@EventListen("mall")
@Component
public class RefundSuccessListener extends AbstractListener {

    @Resource
    ICoreRefundService coreRefundService;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    public void handleEvent(RefundSuccessEvent event) {
        if (event.getTradeType().equals("mall")) {
            coreRefundService.refundTransferSuccess(event.getRefund());
        }
    }
}
