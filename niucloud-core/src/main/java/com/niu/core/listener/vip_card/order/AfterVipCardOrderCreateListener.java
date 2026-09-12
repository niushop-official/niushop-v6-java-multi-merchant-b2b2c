package com.niu.core.listener.vip_card.order;

import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.enums.vip_card.VipCardOrderActionEnum;
import com.niu.core.enums.vip_card.VipCardOrderLogEnum;
import com.niu.core.enums.vip_card.VipCardOrderStatusEnum;
import com.niu.core.event.vip_card.order.AfterVipCardOrderCreateEvent;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import com.niu.core.service.core.recharge.order.ICoreOrderLogService;
import com.niu.core.service.core.recharge.order.param.RechargeOrderLogParam;
import com.niu.core.service.core.vip_card.config.ICoreVipCardConfigService;
import com.niu.core.service.core.vip_card.config.vo.VipCardConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AfterVipCardOrderCreateListener {

    @Autowired
    private VipCardOrderMapper orderMapper;

    @Autowired
    private ICoreVipCardConfigService coreVipCardConfigService;

    @Autowired
    private ICoreOrderLogService coreOrderLogService;

    @EventListener
    public void handle(AfterVipCardOrderCreateEvent event) {
        log.info("订单AfterVipCardOrderCreate: {}", event);
        try {
            MemberVipCardOrder orderData = event.getOrderData();
            Integer orderId = event.getOrderId();

            // 1. 设置超时时间
            VipCardConfigVo config = coreVipCardConfigService.getVipCardConfig();
            Integer closeLength = config.getCloseLength() != null ? config.getCloseLength() : 0;
            if (closeLength > 0) {
                Long timeout = event.getTime() + (long) closeLength * 60;
                MemberVipCardOrder updateOrder = new MemberVipCardOrder();
                updateOrder.setOrderId(Long.valueOf(orderId));
                updateOrder.setTimeout(Math.toIntExact(timeout));
                orderMapper.updateById(updateOrder);
            }

            // 2. 添加订单日志
            String mainType = event.getMainType() != null ? event.getMainType() : VipCardOrderLogEnum.MEMBER.getCode();
            Integer mainId = event.getMainId() != null ? event.getMainId() : orderData.getMemberId();

            RechargeOrderLogParam logEntry = new RechargeOrderLogParam();
            logEntry.setOrderId(orderId);
            logEntry.setStatus(VipCardOrderStatusEnum.WAIT_PAY.getCode());
            logEntry.setMainType(mainType);
            logEntry.setMainId(mainId);
            logEntry.setType(VipCardOrderActionEnum.ORDER_CREATE_ACTION.getCode());
            logEntry.setContent("");
            coreOrderLogService.add(logEntry);

        } catch (Exception e) {
            log.error("订单AfterVipCardOrderCreate失败", e);
        }
    }
}