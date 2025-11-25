package com.niu.mall.service.admin.order.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.service.admin.order.IOrderConfigService;
import com.niu.mall.service.core.order.ICoreOrderConfigService;
import com.niu.mall.service.core.order.param.OrderConfigParam;
import com.niu.mall.service.core.order.vo.OrderConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class OrderConfigServiceImpl implements IOrderConfigService {

    @Resource
    private ICoreOrderConfigService coreOrderConfigService;

    @Override
    public void setConfig(OrderConfigParam orderConfigParam) {
       coreOrderConfigService.setConfig(orderConfigParam);
    }

    @Override
    public OrderConfigVo getConfig() {
       return coreOrderConfigService.getConfig();
    }
}
