package com.niu.core.service.core.cash.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.entity.cash.CashOrder;
import com.niu.core.mapper.cash.CashOrderMapper;
import com.niu.core.service.core.cash.order.ICoreCashOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreCashOrderServiceImpl implements ICoreCashOrderService {

    @Resource
    CashOrderMapper cashOrderMapper;

    @Override
    public CashOrder orderInfo(Integer orderId) {
        return cashOrderMapper.selectById(orderId);
    }

    @Override
    public CashOrder orderInfoByOutTradeNo(String outTradeNo) {
        return cashOrderMapper.selectOne(new LambdaQueryWrapper<CashOrder>()
                .eq(CashOrder::getOutTradeNo, outTradeNo));
    }
}
