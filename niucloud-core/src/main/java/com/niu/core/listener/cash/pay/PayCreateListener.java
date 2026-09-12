package com.niu.core.listener.cash.pay;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.cash.CashOrder;
import com.niu.core.enums.cash.CashOrderStatusEnum;
import com.niu.core.event.pay.PayCreateEventDefiner;
import com.niu.core.service.core.cash.order.ICoreCashOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 买单支付单据创建
 */
@EventCallback("core")
@Component
public class PayCreateListener extends PayCreateEventDefiner {

    @Resource
    ICoreCashOrderService coreCashOrderService;

    @Override
    public PayCreateEventResult handleCallback(PayCreateEvent event) {
        if (!"cash".equals(event.getTradeType())) {
            return null;
        }
        CashOrder order = coreCashOrderService.orderInfo(event.getTradeId());
        if (order == null) {
            throw new CommonException("ORDER_NOT_EXIST");
        }
        if (!CashOrderStatusEnum.WAIT_PAY.getStatus().equals(order.getStatus())) {
            throw new CommonException("ONLY_PAYING_CAN_PAY");
        }

        PayCreateEventResult result = new PayCreateEventResult();
        result.setMainType("member");
        result.setMainId(order.getMemberId());
        result.setMoney(order.getOrderMoney());
        result.setTradeType("cash");
        result.setTradeId(event.getTradeId());
        result.setBody("买单订单");
        result.setSiteId(order.getSiteId());
        return result;
    }
}
