package com.niu.core.service.core.vip_card.order.impl;

import com.niu.core.entity.vip_card.MemberVipCardOrderLog;
import com.niu.core.mapper.vip_card.VipCardOrderLogMapper;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CoreVipCardOrderLogServiceImpl implements ICoreVipCardOrderLogService {

    @Autowired
    private VipCardOrderLogMapper logMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(MemberVipCardOrderLog log) {
        if (log.getCreateTime() == null) {
            log.setCreateTime(System.currentTimeMillis() / 1000);
        }
        logMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAll(List<MemberVipCardOrderLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return;
        }
        for (MemberVipCardOrderLog log : logs) {
            if (log.getCreateTime() == null) {
                log.setCreateTime(System.currentTimeMillis() / 1000);
            }
        }
        logMapper.insert(logs);
    }
}