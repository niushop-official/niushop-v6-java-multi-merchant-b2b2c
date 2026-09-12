package com.niu.core.listener.vip_card.order;

import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.enums.vip_card.VipCardOrderActionEnum;
import com.niu.core.enums.vip_card.VipCardOrderLogEnum;
import com.niu.core.enums.vip_card.VipCardOrderStatusEnum;
import com.niu.core.event.vip_card.order.AfterVipCardOrderCloseEvent;
import com.niu.core.service.core.recharge.order.ICoreOrderLogService;
import com.niu.core.service.core.recharge.order.param.RechargeOrderLogParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AfterVipCardOrderCloseListener {

    @Autowired
    private ICoreOrderLogService coreOrderLogService;

    @EventListener
    public void handle(AfterVipCardOrderCloseEvent event) {
        log.info("订单AfterVipCardOrderClose: {}", event);
        try {
            Integer orderId = event.getOrderId();
            MemberVipCardOrder orderData = event.getOrderData();

            // 默认主类型为 SYSTEM，main_id 为 0
            String mainType = event.getMainType() != null ? event.getMainType() : VipCardOrderLogEnum.SYSTEM.getCode();
            Integer mainId = event.getMainId() != null ? event.getMainId() : 0;

            // 记录订单日志
            RechargeOrderLogParam logEntry = new RechargeOrderLogParam();
            logEntry.setOrderId(orderId);
            logEntry.setStatus(VipCardOrderStatusEnum.FINISH.getCode());
            logEntry.setMainType(mainType);
            logEntry.setMainId(mainId);
            logEntry.setType(VipCardOrderActionEnum.ORDER_PAY_ACTION.getCode());
            logEntry.setContent("");
            coreOrderLogService.add(logEntry);
        } catch (Exception e) {
            log.error("订单AfterVipCardOrderClose失败", e);
        }
    }
}