package com.niu.core.listener.cash.order;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.enums.cash.CashOrderActionEnum;
import com.niu.core.enums.cash.CashOrderLogEnum;
import com.niu.core.enums.cash.CashOrderStatusEnum;
import com.niu.core.event.cash.AfterCashOrderCloseEvent;
import com.niu.core.service.core.cash.order.ICoreCashOrderLogService;
import com.niu.core.service.core.cash.order.param.CashOrderLogParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@EventListen("core")
@Component
public class AfterCashOrderCloseListener extends AbstractListener {

    @Resource
    ICoreCashOrderLogService coreCashOrderLogService;

    public void handleEvent(AfterCashOrderCloseEvent event) {
        log.info("订单AfterCashOrderClose: {}", event);
        try {
            if (event.getOrderData() == null) {
                return;
            }
            CashOrderLogParam logParam = new CashOrderLogParam();
            logParam.setOrderId(event.getOrderData().getOrderId());
            logParam.setStatus(CashOrderStatusEnum.CLOSE.getStatus());
            logParam.setMainType(ObjectUtil.defaultIfNull(event.getMainType(), CashOrderLogEnum.SYSTEM.getCode()));
            logParam.setMainId(ObjectUtil.defaultIfNull(event.getMainId(), 0));
            logParam.setType(CashOrderActionEnum.ORDER_CLOSE_ACTION.getAction());
            logParam.setContent("");
            coreCashOrderLogService.add(logParam);
        } catch (Exception e) {
            log.error("订单AfterCashOrderClose失败", e);
        }
    }
}
