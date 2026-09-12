package com.niu.core.listener.vip_card.pay;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.enums.vip_card.VipCardOrderLogEnum;
import com.niu.core.enums.vip_card.VipCardOrderStatusEnum;
import com.niu.core.event.pay.PayCreateEventDefiner;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 支付成功
 */
@EventListen("core")
@Component
public class VipCardPayCreatedListener extends PayCreateEventDefiner {

    @Resource
    ICoreVipCardOrderService orderService;

    @Override
    public PayCreateEventResult handleCallback(PayCreateEvent event) {
        if (!"vip_card".equals(event.getTradeType())) {
            return new PayCreateEventResult();
        }
        MemberVipCardOrder order = orderService.orderInfo(event.getTradeId());
        if (!VipCardOrderStatusEnum.WAIT_PAY.getCode().equals(order.getStatus())) {
            throw new CommonException("ONLY_PAYING_CAN_PAY");
        }
        PayCreateEventDefiner.PayCreateEventResult result = new PayCreateEventDefiner.PayCreateEventResult();
        result.setTradeId(event.getTradeId());
        result.setTradeType(event.getTradeType());
        result.setMainType(VipCardOrderLogEnum.MEMBER.getCode());
        result.setMainId(order.getMemberId());
        result.setMoney(order.getOrderMoney());
        result.setBody("付费会员卡订单");
        result.setSiteId(order.getSiteId());

        return result;
    }
}
