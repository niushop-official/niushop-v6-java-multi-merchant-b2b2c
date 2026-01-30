package com.niu.core.service.core.recharge.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.enums.recharge.RechargeOrderLogEnum;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.recharge.order.ICoreOrderCloseService;
import com.niu.core.service.core.recharge.order.ICoreOrderEventService;
import com.niu.core.service.core.recharge.order.param.AfterOrderCloseParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CoreOrderCloseServiceImpl implements ICoreOrderCloseService {

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    ICorePayService corePayService;

    @Resource
    ICoreOrderEventService coreOrderEventService;

    /**
     * 订单关闭
     * @param orderId 创建参数
     * @return 订单关闭结果
     */
    @Override
    @Transactional
    public Boolean close(Integer orderId) {
        RechargeOrder order = rechargeOrderMapper.selectById(orderId);
        if (ObjectUtil.isEmpty(order)){
            throw new CommonException("订单不存在");
        }
        // 关闭支付单据
        if (RechargeOrderStatusEnum.WAIT_PAY.getStatus().equals(order.getOrderStatus())) {
            corePayService.closeByTrade("recharge", orderId);
        }

        //修改订单状态
        order.setOrderStatus(RechargeOrderStatusEnum.CLOSE.getStatus());
        order.setCloseTime(System.currentTimeMillis()/1000);
        rechargeOrderMapper.updateById(order);

        //订单关闭后操作
        AfterOrderCloseParam paramAfter = new AfterOrderCloseParam();
        paramAfter.setOrderId(orderId);
        paramAfter.setMainId(0);
        paramAfter.setMainType(RechargeOrderLogEnum.SYSTEM.getType());
        paramAfter.setOrderData(order);
        coreOrderEventService.AfterRechargeOrderClose(paramAfter);
        return true;
    }


}
