package com.niu.core.service.core.recharge.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.enums.recharge.RechargeOrderLogEnum;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.service.core.recharge.order.ICoreOrderEventService;
import com.niu.core.service.core.recharge.order.ICoreOrderFinishService;
import com.niu.core.service.core.recharge.order.ICoreOrderPayService;
import com.niu.core.service.core.recharge.order.param.AfterOrderPayParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CoreOrderPayServiceImpl implements ICoreOrderPayService {

    @Resource
    private RechargeOrderMapper rechargeOrderMapper;
    @Resource
    private ICoreOrderEventService coreOrderEventService;
    @Resource
    private ICoreOrderFinishService coreOrderFinishService;

    @Override
    public void pay(Integer tradeId, String outTradeNo) {
        try {
            RechargeOrder order = rechargeOrderMapper.selectById(tradeId);
            if (ObjectUtil.isEmpty(order)){
                throw new CommonException("订单不存在");
            }
            order.setPayTime(System.currentTimeMillis()/1000);
            order.setOrderStatus(RechargeOrderStatusEnum.FINISH.getStatus());
            order.setIsEnableRefund(1);
            order.setOutTradeNo(outTradeNo);
            rechargeOrderMapper.updateById( order);

            //订单支付后的操作
            AfterOrderPayParam param = new AfterOrderPayParam();
            param.setOrderData(order);
            param.setOrderId(tradeId);
            param.setMainType(RechargeOrderLogEnum.MEMBER.getType());
            param.setMainId(order.getMemberId());
            coreOrderEventService.AfterRechargeOrderPay(param);

            //订单完成
            coreOrderFinishService.finish(tradeId);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }
}
