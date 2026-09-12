package com.niu.core.service.core.cash.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.cash.CashOrder;
import com.niu.core.enums.cash.CashOrderLogEnum;
import com.niu.core.enums.cash.CashOrderStatusEnum;
import com.niu.core.mapper.cash.CashOrderMapper;
import com.niu.core.service.core.cash.order.ICoreCashOrderCloseService;
import com.niu.core.service.core.cash.order.ICoreCashOrderEventService;
import com.niu.core.service.core.cash.order.param.AfterCashOrderCloseParam;
import com.niu.core.service.core.pay.ICorePayService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoreCashOrderCloseServiceImpl implements ICoreCashOrderCloseService {

    @Resource
    CashOrderMapper cashOrderMapper;

    @Resource
    ICorePayService corePayService;

    @Resource
    ICoreCashOrderEventService coreCashOrderEventService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void close(Integer orderId) {
        CashOrder order = cashOrderMapper.selectById(orderId);
        if (ObjectUtil.isEmpty(order)) {
            throw new CommonException("ORDER_NOT_EXIST");
        }

        if (CashOrderStatusEnum.WAIT_PAY.getStatus().equals(order.getStatus())) {
            corePayService.closeByTrade("cash", orderId);
        }

        CashOrder updateOrder = new CashOrder();
        updateOrder.setOrderId(orderId);
        updateOrder.setStatus(CashOrderStatusEnum.CLOSE.getStatus());
        cashOrderMapper.updateById(updateOrder);
        order.setStatus(CashOrderStatusEnum.CLOSE.getStatus());

        AfterCashOrderCloseParam afterParam = new AfterCashOrderCloseParam();
        afterParam.setOrderId(orderId);
        afterParam.setOrderData(order);
        afterParam.setMainType(CashOrderLogEnum.SYSTEM.getCode());
        afterParam.setMainId(0);
        coreCashOrderEventService.orderCloseAfter(afterParam);
    }
}
