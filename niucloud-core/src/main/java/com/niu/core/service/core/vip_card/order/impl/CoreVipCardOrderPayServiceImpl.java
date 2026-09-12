package com.niu.core.service.core.vip_card.order.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.entity.vip_card.MemberVipCardOrderItem;
import com.niu.core.enums.vip_card.VipCardOrderLogEnum;
import com.niu.core.enums.vip_card.VipCardOrderStatusEnum;
import com.niu.core.event.vip_card.order.AfterVipCardOrderPayEvent;
import com.niu.core.mapper.vip_card.VipCardOrderItemMapper;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderEventService;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderFinishService;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderPayService;
import com.niu.core.service.core.vip_card.order.param.VipCardOrderFinishParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoreVipCardOrderPayServiceImpl implements ICoreVipCardOrderPayService {

    private static final Logger log = LoggerFactory.getLogger(CoreVipCardOrderPayServiceImpl.class);

    @Autowired
    private VipCardOrderMapper orderMapper;

    @Autowired
    private VipCardOrderItemMapper orderItemMapper;

    @Autowired
    private ICoreVipCardOrderEventService orderEventService;

    @Autowired
    private ICoreVipCardOrderFinishService orderFinishService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Pay payInfo) {
        Integer tradeId = payInfo.getTradeId();
        String outTradeNo = payInfo.getOutTradeNo();

        // 1. 查询订单及订单项
        MemberVipCardOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardOrder>()
                        .eq(MemberVipCardOrder::getOrderId, tradeId)
        );
        if (order == null) {
            throw new CommonException("ORDER_NOT_EXIST");
        }

        // 查询订单项
        MemberVipCardOrderItem orderItem = orderItemMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardOrderItem>()
                        .eq(MemberVipCardOrderItem::getOrderId, tradeId)
                        .last("limit 1")
        );
        VipCardOrderFinishParam param = new VipCardOrderFinishParam();
        BeanUtil.copyProperties(order, param);
        param.setOrderItem(orderItem); // 设置关联项

        // 2. 更新订单状态
        long now = System.currentTimeMillis() / 1000;
        LambdaUpdateWrapper<MemberVipCardOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MemberVipCardOrder::getOrderId, tradeId)
                .set(MemberVipCardOrder::getPayTime, now)
                .set(MemberVipCardOrder::getStatus, VipCardOrderStatusEnum.FINISH.getCode())
                .set(MemberVipCardOrder::getIsEnableRefund, 0)
                .set(MemberVipCardOrder::getOutTradeNo, outTradeNo);
        orderMapper.update(null, updateWrapper);

        // 3. 触发支付后事件
        AfterVipCardOrderPayEvent event = new AfterVipCardOrderPayEvent(
                this, tradeId, order, VipCardOrderLogEnum.MEMBER.getCode(), order.getMemberId()
        );
        orderEventService.orderPayAfter(event);

        // 4. 订单完成
        orderFinishService.finish(param);
    }
}