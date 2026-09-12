package com.niu.core.service.core.vip_card.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.enums.vip_card.VipCardOrderLogEnum;
import com.niu.core.enums.vip_card.VipCardOrderStatusEnum;
import com.niu.core.event.vip_card.order.AfterVipCardOrderCloseEvent;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderCloseService;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CoreVipCardOrderCloseServiceImpl implements ICoreVipCardOrderCloseService {

    @Autowired
    private VipCardOrderMapper orderMapper;

    @Autowired
    private ICorePayService corePayService;

    @Autowired
    private ICoreVipCardOrderEventService orderEventService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void close(Integer orderId) {
        // 1. 查询订单
        MemberVipCardOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new CommonException("ORDER_NOT_EXIST");
        }

        // 2. 如果是待支付状态，关闭支付单据
        if (VipCardOrderStatusEnum.WAIT_PAY.getCode().equals(order.getStatus())) {
            corePayService.closeByTrade("vip_card", orderId);
        }

        // 3. 更新订单状态为关闭
        LambdaUpdateWrapper<MemberVipCardOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberVipCardOrder::getOrderId, orderId)
                .set(MemberVipCardOrder::getStatus, VipCardOrderStatusEnum.CLOSE.getCode());
        orderMapper.update(null, wrapper);

        // 4. 触发订单关闭后事件
        AfterVipCardOrderCloseEvent event = new AfterVipCardOrderCloseEvent(
                this, orderId, order, VipCardOrderLogEnum.SYSTEM.getCode(), 0
        );
        orderEventService.orderCloseAfter(event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchClose(List<Integer> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return;
        }

        // 1. 查询所有待支付订单，逐个关闭支付单据
        List<MemberVipCardOrder> orders = orderMapper.selectList(
                new LambdaQueryWrapper<MemberVipCardOrder>()
                        .in(MemberVipCardOrder::getOrderId, orderIds)
                        .eq(MemberVipCardOrder::getStatus, VipCardOrderStatusEnum.WAIT_PAY.getCode())
        );
        for (MemberVipCardOrder order : orders) {
            corePayService.closeByTrade("vip_card", Math.toIntExact(order.getOrderId()));
        }

        // 2. 批量更新订单状态为关闭
        LambdaUpdateWrapper<MemberVipCardOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(MemberVipCardOrder::getOrderId, orderIds)
                .set(MemberVipCardOrder::getStatus, VipCardOrderStatusEnum.CLOSE.getCode());
        orderMapper.update(null, wrapper);
    }
}