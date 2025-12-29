package com.niu.mall.service.site.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.order.OrderLogMainTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.site.order.IMallOrderFinishService;
import com.niu.mall.service.core.order.ICoreOrderFinishService;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.util.Assert;

@Service
public class IMallOrderFinishServiceImpl implements IMallOrderFinishService {

    @Resource
    ICoreOrderFinishService coreOrderFinishService;
    @Resource
    MallOrderMapper mallOrderMapper;

    @Override
    public void finish(Integer orderId) {
        //查询订单
        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().eq("order_id", orderId));
        Assert.notNull(order, "订单不存在");

        if (order.getOrderFrom().equals(ChannelEnum.WEAPP.getCode())){
            throw new CommonException("微信小程序订单需在小程序端收货后才能操作完成");
        }
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_TAKE.getStatus().toString())){
            throw new CommonException("只有待收货的订单才可以收货");
        }

        OrderFinishParam finishParam = new OrderFinishParam();
        finishParam.setOrderId(orderId);
        finishParam.setSiteId(RequestUtils.siteId());
        finishParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        finishParam.setMainId(RequestUtils.uid());
        coreOrderFinishService.finish(finishParam);

    }
}
