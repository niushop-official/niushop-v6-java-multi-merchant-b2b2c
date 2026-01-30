package com.niu.core.service.core.recharge.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.service.core.recharge.order.ICoreOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class CoreOrderServiceImpl implements ICoreOrderService {

    @Resource
    private RechargeOrderMapper rechargeOrderMapper;

    @Override
    public RechargeOrder orderInfo(Integer orderId) {
        return rechargeOrderMapper.selectById(orderId);
    }

    @Override
    public RechargeOrder orderInfoByOutTradeNo(String outTradeNo) {
        return rechargeOrderMapper.selectOne(new QueryWrapper<RechargeOrder>().eq("out_trade_no", outTradeNo).last("limit 1"));
    }
}
