package com.niu.core.service.core.recharge.finance.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.service.core.recharge.finance.param.OrderPlatParam;
import com.niu.core.service.core.recharge.finance.ICorePlatService;
import com.niu.core.service.core.recharge.finance.param.OrderSeparateParam;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.recharge.finance.ICoreFinanceService;
import com.niu.core.service.core.sys.ICoreSysStatService;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoreFinanceServiceImpl implements ICoreFinanceService {

    @Resource
    ICorePlatService corePlatService;

    @Resource
    ICoreSysStatService coreSysStatService;

    @Override
    @Async
    public Boolean orderPay(RechargeOrder order) {
        //平台
        OrderPlatParam orderPlatParam = new OrderPlatParam();
        orderPlatParam.setOrderNo(order.getOrderNo());
        orderPlatParam.setOrderId(order.getOrderId());
        orderPlatParam.setOutTradeNo(order.getOutTradeNo());
        orderPlatParam.setOrderMoney(order.getOrderMoney());
        orderPlatParam.setMemberId(order.getMemberId());
        orderPlatParam.setSiteId(RequestUtils.defaultSiteId());
        orderPlatParam.setOrderGiveMoney(order.getOrderGiveMoney());
        corePlatService.platIncomeOrderPay(orderPlatParam);

        //平台统计
        SysDataAddParam sysDataAddParam = new SysDataAddParam();
        sysDataAddParam.setSiteId(RequestUtils.defaultSiteId());
        sysDataAddParam.setRechargeMoney(order.getOrderFaceMoney());
        sysDataAddParam.setRechargeOrderMoney(order.getOrderMoney());
        sysDataAddParam.setRechargeGiveMoney(order.getOrderGiveMoney());
        coreSysStatService.addStat(sysDataAddParam);
        return null;
    }

    @Override
    @Async
    public Boolean orderFinish(RechargeOrder order) {
        //平台
        OrderSeparateParam orderSeparateParam = new OrderSeparateParam();
        orderSeparateParam.setOrderNo(order.getOrderNo());
        orderSeparateParam.setOrderId(order.getOrderId());
        orderSeparateParam.setOrderMoney(order.getOrderMoney());
        orderSeparateParam.setMemberId(order.getMemberId());
        orderSeparateParam.setSiteId(RequestUtils.defaultSiteId());
        corePlatService.platIncomeOrderSeparate(orderSeparateParam);
        return true;
    }
}
