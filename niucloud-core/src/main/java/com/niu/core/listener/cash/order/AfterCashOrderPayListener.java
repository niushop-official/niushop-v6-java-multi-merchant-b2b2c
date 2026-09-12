package com.niu.core.listener.cash.order;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.enums.cash.CashOrderActionEnum;
import com.niu.core.enums.cash.CashOrderLogEnum;
import com.niu.core.enums.cash.CashOrderStatusEnum;
import com.niu.core.event.cash.AfterCashOrderPayEvent;
import com.niu.core.service.core.cash.order.ICoreCashOrderLogService;
import com.niu.core.service.core.cash.order.param.CashOrderLogParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@EventListen("core")
@Component
public class AfterCashOrderPayListener extends AbstractListener {

    @Resource
    ICoreCashOrderLogService coreCashOrderLogService;

    public void handleEvent(AfterCashOrderPayEvent event) {
        log.info("订单AfterCashOrderPay: {}", event);
        try {
            if (event.getOrderData() == null) {
                return;
            }
            CashOrderLogParam logParam = new CashOrderLogParam();
            logParam.setOrderId(event.getOrderData().getOrderId());
            logParam.setStatus(CashOrderStatusEnum.FINISH.getStatus());
            logParam.setMainType(ObjectUtil.defaultIfNull(event.getMainType(), CashOrderLogEnum.MEMBER.getCode()));
            logParam.setMainId(ObjectUtil.defaultIfNull(event.getMainId(), event.getOrderData().getMemberId()));
            logParam.setType(CashOrderActionEnum.ORDER_PAY_ACTION.getAction());
            logParam.setContent("");
            coreCashOrderLogService.add(logParam);
        } catch (Exception e) {
            log.error("订单AfterCashOrderPay失败", e);
        }
    }
}
