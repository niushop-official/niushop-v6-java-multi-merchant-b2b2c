package com.niu.core.service.core.cash.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.cash.CashOrder;
import com.niu.core.enums.cash.CashOrderLogEnum;
import com.niu.core.enums.cash.CashOrderStatusEnum;
import com.niu.core.mapper.cash.CashOrderMapper;
import com.niu.core.service.core.cash.order.ICoreCashOrderEventService;
import com.niu.core.service.core.cash.order.ICoreCashOrderFinishService;
import com.niu.core.service.core.cash.order.ICoreCashOrderPayService;
import com.niu.core.service.core.cash.order.param.AfterCashOrderPayParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoreCashOrderPayServiceImpl implements ICoreCashOrderPayService {

    @Resource
    CashOrderMapper cashOrderMapper;

    @Resource
    ICoreCashOrderEventService coreCashOrderEventService;

    @Resource
    ICoreCashOrderFinishService coreCashOrderFinishService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Integer tradeId, String outTradeNo) {
        CashOrder order = cashOrderMapper.selectById(tradeId);
        if (ObjectUtil.isEmpty(order)) {
            throw new CommonException("ORDER_NOT_EXIST");
        }

        CashOrder updateOrder = new CashOrder();
        updateOrder.setOrderId(tradeId);
        updateOrder.setPayTime(System.currentTimeMillis() / 1000);
        updateOrder.setStatus(CashOrderStatusEnum.FINISH.getStatus());
        updateOrder.setOutTradeNo(outTradeNo);
        cashOrderMapper.updateById(updateOrder);

        order.setPayTime(updateOrder.getPayTime());
        order.setStatus(updateOrder.getStatus());
        order.setOutTradeNo(outTradeNo);

        AfterCashOrderPayParam afterParam = new AfterCashOrderPayParam();
        afterParam.setOrderId(tradeId);
        afterParam.setOrderData(order);
        afterParam.setMainType(CashOrderLogEnum.MEMBER.getCode());
        afterParam.setMainId(order.getMemberId());
        coreCashOrderEventService.orderPayAfter(afterParam);

        coreCashOrderFinishService.finish(order);
    }
}
