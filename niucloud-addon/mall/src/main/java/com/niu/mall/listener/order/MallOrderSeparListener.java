package com.niu.mall.listener.order;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.event.order.OrderPageEventDefiner;
import com.niu.core.event.separ.AfterSeparEvent;
import com.niu.core.service.core.shop.param.ShopStatParam;
import com.niu.core.entity.goods.MallGoods;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.core.enums.goods.GoodsStatusEnum;
import com.niu.mall.enums.order.OrderRefundStatusEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.enums.trade.SettlementEnum;
import com.niu.mall.event.goods.AfterGoodsEditEvent;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.mall.service.core.order.ICoreOrderSettlementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@EventListen("mall")
@Component
@Slf4j
public class MallOrderSeparListener extends AbstractListener {

    @Resource
    MallOrderMapper mallOrderMapper;
    @Resource
    ICoreOrderSettlementService coreOrderSettlementService;

    public void handleEvent(AfterSeparEvent event) {
        log.info("AfterMallOrderSepar: {}", event);
        try {
            if(StringUtils.isEmpty(event.getAccount().getTradeType()) ||
                    (StringUtils.isNotEmpty(event.getAccount().getTradeType()) && !event.getAccount().getTradeType().equals("order_trade"))){
                return;
            }
            MallOrder mallOrder = mallOrderMapper.selectById(event.getAccount().getTradeId());
            if(ObjectUtil.isNotEmpty(mallOrder)){
//                mallOrder.setSettlementStatus(SettlementEnum.SETTLED.getStatus());
//                mallOrder.setSettlementTime(System.currentTimeMillis()/1000);
//                mallOrderMapper.updateById(mallOrder);
                coreOrderSettlementService.success(mallOrder);
            }
        }catch (Exception e){
            log.error("AfterMallOrderSepar error: {}", e.getMessage());
        }
    }
}
