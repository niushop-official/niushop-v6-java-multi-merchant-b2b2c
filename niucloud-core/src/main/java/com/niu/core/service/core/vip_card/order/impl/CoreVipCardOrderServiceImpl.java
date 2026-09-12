package com.niu.core.service.core.vip_card.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreVipCardOrderServiceImpl implements ICoreVipCardOrderService {

    @Autowired
    private VipCardOrderMapper orderMapper;

    @Override
    public MemberVipCardOrder orderInfo(Integer orderId) {
        return orderMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardOrder>()
                        .eq(MemberVipCardOrder::getOrderId, orderId)
        );
    }

    @Override
    public MemberVipCardOrder orderInfoByOutTradeNo(String outTradeNo) {
        return orderMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardOrder>()
                        .eq(MemberVipCardOrder::getOutTradeNo, outTradeNo)
        );
    }
}