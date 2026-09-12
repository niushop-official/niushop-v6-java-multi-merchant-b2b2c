package com.niu.core.service.core.cash.order.impl;

import com.niu.core.entity.cash.CashOrderLog;
import com.niu.core.mapper.cash.CashOrderLogMapper;
import com.niu.core.service.core.cash.order.ICoreCashOrderLogService;
import com.niu.core.service.core.cash.order.param.CashOrderLogParam;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CoreCashOrderLogServiceImpl implements ICoreCashOrderLogService {

    @Resource
    CashOrderLogMapper cashOrderLogMapper;

    @Override
    public void add(CashOrderLogParam param) {
        CashOrderLog log = new CashOrderLog();
        BeanUtils.copyProperties(param, log);
        log.setCreateTime(System.currentTimeMillis() / 1000);
        cashOrderLogMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAll(List<CashOrderLogParam> params) {
        for (CashOrderLogParam param : params) {
            add(param);
        }
    }
}
