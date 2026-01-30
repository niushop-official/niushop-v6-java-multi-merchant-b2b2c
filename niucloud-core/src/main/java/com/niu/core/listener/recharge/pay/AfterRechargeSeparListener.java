package com.niu.core.listener.recharge.pay;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.event.separ.AfterSeparEvent;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.service.core.recharge.order.ICoreOrderSettlementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@EventListen("core")
@Component
@Slf4j
public class AfterRechargeSeparListener extends AbstractListener {

    @Resource
    RechargeOrderMapper rechargeOrderMapper;
    @Resource
    ICoreOrderSettlementService coreOrderSettlementService;

    public void handleEvent(AfterSeparEvent event) {
        log.info("AfterRechargeOrderSepar: {}", event);
        try {
            if(StringUtils.isEmpty(event.getAccount().getTradeType()) ||
                    (StringUtils.isNotEmpty(event.getAccount().getTradeType()) && !event.getAccount().getTradeType().equals("recharge_order_trade"))){
                return;
            }
            RechargeOrder order = rechargeOrderMapper.selectById(event.getAccount().getTradeId());
            if(ObjectUtil.isNotEmpty(order)){
                coreOrderSettlementService.success(order);
            }
        }catch (Exception e){
            log.error("AfterMallOrderSepar error: {}", e.getMessage());
        }
    }
}
