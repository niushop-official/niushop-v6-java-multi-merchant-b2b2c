package com.niu.mall.service.core.order.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.trade.SettlementEnum;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderEventService;
import com.niu.mall.service.core.order.ICoreOrderSettlementService;
import com.niu.mall.service.core.shop.ICoreShopPerkService;
import com.niu.mall.service.core.shop.ICoreShopSettlementService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 订单结算服务实现类
 */
@Service
public class CoreOrderSettlementServiceImpl implements ICoreOrderSettlementService {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    ICoreShopSettlementService coreShopSettlementService;

    @Resource
    ICoreShopPerkService coreShopPerkService;

    @Resource
    ICoreOrderEventService coreOrderEventService;

    @Override
    public void settlement(MallOrder order) {
        // 设置结算状态为处理中
        this.dealing(order);
        
        // 店铺结算
        coreShopSettlementService.settlement(order);
        
        // 订单结算补贴
        coreShopPerkService.perk(order);
        
        // 发布订单结算事件
        coreOrderEventService.orderSettlement(order);
    }

    @Override
    public void dealing(MallOrder order) {
        mallOrderMapper.update(null, new UpdateWrapper<MallOrder>()
            .eq("order_id", order.getOrderId())
            .set("settlement_status", SettlementEnum.DEALING.getStatus()));
    }

    @Override
    public void success(MallOrder order) {
        mallOrderMapper.update(null, new UpdateWrapper<MallOrder>()
            .eq("order_id", order.getOrderId())
            .set("settlement_status", SettlementEnum.SETTLED.getStatus())
            .set("settlement_time", System.currentTimeMillis() / 1000));
        
        // 发布订单结算完成事件
        coreOrderEventService.orderSettlementFinish(order);
    }
}

