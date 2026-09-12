package com.niu.core.service.core.cash.order.impl;

import com.niu.core.entity.cash.CashOrder;
import com.niu.core.service.core.cash.order.ICoreCashOrderFinishService;
import org.springframework.stereotype.Service;

@Service
public class CoreCashOrderFinishServiceImpl implements ICoreCashOrderFinishService {

    @Override
    public void finish(CashOrder order) {
        // 资金流水、消息通知、结算等扩展逻辑预留
    }
}
