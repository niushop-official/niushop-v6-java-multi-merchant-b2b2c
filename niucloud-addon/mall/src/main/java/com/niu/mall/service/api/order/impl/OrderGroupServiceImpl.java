package com.niu.mall.service.api.order.impl;

import com.niu.mall.service.api.order.IOrderGroupService;
import com.niu.mall.service.core.order.ICoreOrderGroupCloseService;
import com.niu.mall.service.core.order.ICoreOrderGroupService;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import com.niu.mall.service.core.order.vo.MallOrderGroupDataVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class OrderGroupServiceImpl implements IOrderGroupService {

    @Resource
    ICoreOrderGroupService coreOrderGroupService;

    @Resource
    ICoreOrderGroupCloseService coreOrderGroupCloseService;
    @Override
    public MallOrderGroupDataVo getGroupByOrderId(Integer orderId) {
        return coreOrderGroupService.getGroupByOrderId(orderId);
    }

    @Override
    public void close(Integer groupId, OrderCloseParam closeParam) {
        coreOrderGroupCloseService.close(groupId, closeParam);
    }
}
