package com.niu.core.listener.recharge.pay;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.event.pay.PayCreateEventDefiner;
import com.niu.core.service.core.recharge.order.ICoreOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 获取支付数据
 */
@EventCallback("core")
@Component
public class PayCreateListener extends PayCreateEventDefiner {

    @Resource
    ICoreOrderService coreOrderService;

    @Override
    public PayCreateEventResult handleCallback(PayCreateEvent event) {
        if (!event.getTradeType().equals("recharge")) return null;

        RechargeOrder order = coreOrderService.orderInfo(event.getTradeId());
        if (order == null) throw new CommonException("订单不存在");
        if (!order.getOrderStatus().equals(RechargeOrderStatusEnum.WAIT_PAY.getStatus())) throw new CommonException("只有待支付的订单可以支付");

        //添加订单支付表
        PayCreateEventResult result = new PayCreateEventResult();
        result.setTradeId(event.getTradeId());
        result.setTradeType("recharge");
        result.setMainType("member");
        result.setMainId(order.getMemberId());
        result.setMoney(order.getOrderMoney());
        result.setBody("充值订单");
        return result;
    }

}
