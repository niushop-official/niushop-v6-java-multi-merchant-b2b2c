package com.niu.core.service.core.recharge.order.impl;

import com.niu.core.entity.recharge.RechargeOrderLog;
import com.niu.core.mapper.recharge.RechargeOrderLogMapper;
import com.niu.core.service.core.recharge.order.ICoreOrderLogService;
import com.niu.core.service.core.recharge.order.param.RechargeOrderLogParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CoreOrderLogServiceImpl implements ICoreOrderLogService {

    @Resource
    private RechargeOrderLogMapper rechargeOrderLogMapper;

    @Override
    public void add(RechargeOrderLogParam param) {
        //添加超时时间
        RechargeOrderLog log = new RechargeOrderLog();
        BeanUtils.copyProperties(param, log);
        log.setCreateTime(System.currentTimeMillis()/1000);
        rechargeOrderLogMapper.insert( log);
    }

    @Override
    @Transactional( rollbackFor = Exception.class)
    public void addAll(List<RechargeOrderLogParam> param) {
        for (RechargeOrderLogParam logParam : param){
            add(logParam);
        }
    }
}
