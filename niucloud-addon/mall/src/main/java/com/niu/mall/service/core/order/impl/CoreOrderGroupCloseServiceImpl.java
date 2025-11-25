package com.niu.mall.service.core.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGroup;
import com.niu.mall.enums.order.OrderGroupEnum;
import com.niu.mall.event.order.OrderGroupCloseAfterEvent;
import com.niu.mall.mapper.order.MallOrderGroupMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderCloseService;
import com.niu.mall.service.core.order.ICoreOrderGroupCloseService;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CoreOrderGroupCloseServiceImpl implements ICoreOrderGroupCloseService {

    @Resource
    MallOrderGroupMapper mallOrderGroupMapper;

    @Resource
    MallOrderMapper orderMapper;

    @Resource
    ICorePayService corePayService;

    @Resource
    ICoreOrderCloseService coreOrderCloseService;


    @Override
    @Transactional
    public void close(Integer groupId, OrderCloseParam closeParam) {
        MallOrderGroup group = mallOrderGroupMapper.selectById(groupId);
        if (group != null && group.getStatus().equals(OrderGroupEnum.WAIT_PAY.getStatus())) {
            try {
                corePayService.closeByTrade("mall", groupId);
            } catch (Exception e) {
                // 忽略异常
            }
            List<MallOrder> orderList = orderMapper.selectList(new QueryWrapper<MallOrder>().eq("group_id", groupId));
            mallOrderGroupMapper.update(null, new UpdateWrapper<MallOrderGroup>().eq("group_id", groupId).set("status", OrderGroupEnum.CLOSE.getStatus()));
            for (MallOrder order : orderList) {
                closeParam.setOrderId(order.getOrderId());
                coreOrderCloseService.close(closeParam);
            }

            // 订单组关闭之后事件
            OrderGroupCloseAfterEvent event = new OrderGroupCloseAfterEvent();
            event.addAppSign("mall");
            event.setName("OrderCloseAfterEvent");
            event.setOrderGroup(group);
            EventAndSubscribeOfPublisher.publishAll(event);

        }
    }
}
