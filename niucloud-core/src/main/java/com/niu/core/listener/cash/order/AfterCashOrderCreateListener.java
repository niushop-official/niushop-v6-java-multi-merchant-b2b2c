package com.niu.core.listener.cash.order;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.enums.cash.CashOrderActionEnum;
import com.niu.core.enums.cash.CashOrderLogEnum;
import com.niu.core.enums.cash.CashOrderStatusEnum;
import com.niu.core.event.cash.AfterCashOrderCreateEvent;
import com.niu.core.service.core.cash.order.ICoreCashOrderLogService;
import com.niu.core.service.core.cash.order.param.CashOrderLogParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@EventListen("core")
@Component
public class AfterCashOrderCreateListener extends AbstractListener {

    @Resource
    ICoreCashOrderLogService coreCashOrderLogService;

    public void handleEvent(AfterCashOrderCreateEvent event) {
        log.info("订单AfterCashOrderCreate: {}", event);
        try {
            if (event.getOrderData() == null) {
                return;
            }
            CashOrderLogParam logParam = new CashOrderLogParam();
            logParam.setOrderId(event.getOrderId());
            logParam.setStatus(CashOrderStatusEnum.WAIT_PAY.getStatus());
            logParam.setMainType(ObjectUtil.defaultIfNull(event.getMainType(), CashOrderLogEnum.MEMBER.getCode()));
            logParam.setMainId(ObjectUtil.defaultIfNull(event.getMainId(), event.getOrderData().getMemberId()));
            logParam.setType(CashOrderActionEnum.ORDER_CREATE_ACTION.getAction());
            logParam.setContent("");
            coreCashOrderLogService.add(logParam);
        } catch (Exception e) {
            log.error("订单AfterCashOrderCreate失败", e);
        }
    }
}
