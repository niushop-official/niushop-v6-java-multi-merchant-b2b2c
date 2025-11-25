package com.niu.mall.service.core.order.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.object.ObjectUtils;
import com.niu.core.event.common.CommonEvent;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.enums.order.*;
import com.niu.mall.event.order.OrderCloseAfterEvent;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.mall.service.core.order.ICoreOrderCloseService;
import com.niu.mall.service.core.order.ICoreOrderEventService;
import com.niu.mall.service.core.order.ICoreOrderFinishService;
import com.niu.mall.service.core.order.ICoreOrderGroupService;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CoreOrderCloseServiceImpl implements ICoreOrderCloseService {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    ICorePayService corePayService;

    @Resource
    MallOrderRefundMapper mallOrderRefundMapper;

    @Resource
    ICoreOrderFinishService coreOrderFinishService;

    @Resource
    ICoreOrderEventService coreOrderEventService;

    @Resource
    ICoreOrderGroupService coreOrderGroupService;

    /**
     * 订单关闭
     *
     * @param param
     */
    @Override
    @Transactional
    public void close(OrderCloseParam param) {
        QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<MallOrder>().eq("order_id", param.getOrderId());
        MallOrder order = mallOrderMapper.selectOne(queryWrapper);
        if (order == null) throw new CommonException("订单不存在");
        if (order.getStatus().equals(OrderStatusEnum.CLOSE.getStatus().toString())) throw new CommonException("订单已关闭");
        //关闭订单组
        if(!param.getCloseType().equals(OrderCloseTypeEnum.REFUND_CLOSE.getType())) {

            coreOrderGroupService.cancel(order.getGroupId());
        }

        // 关闭订单
        order.setStatus(OrderStatusEnum.CLOSE.getStatus().toString());
        order.setCloseType(param.getCloseType());
        order.setCloseRemark(param.getCloseRemark());
        order.setIsEnableRefund(0);
        order.setTimeout(0L);
        order.setCloseTime(System.currentTimeMillis() / 1000);
        mallOrderMapper.updateById(order);

        if (!param.getCloseType().equals(OrderCloseTypeEnum.REFUND_CLOSE.getType())) {
            corePayService.closeByTrade(order.getOrderType(), order.getOrderId());
        }

        order = mallOrderMapper.selectById(order.getOrderId());

        // 订单关闭之后事件
        if(ObjectUtil.isNotEmpty(order.getSupplyOrderId()) && order.getSupplyOrderId() > 0){
            OrderCloseAfterEvent event = new OrderCloseAfterEvent();
            event.setSiteId(order.getSiteId());
            event.addAppSign("supply");
            event.setName("OrderCloseAfterEvent");
            event.setOrder(order);
            EventAndSubscribeOfPublisher.publishAll(event);
        }
        coreOrderEventService.orderCloseAfter(order, param);
    }

    /**
     * 校验是否全部退款
     *
     * @param orderId
     */
    @Override
    public void checkAllClose(Integer orderId) {
        Long count = mallOrderGoodsMapper.selectCount(new QueryWrapper<MallOrderGoods>()
                .eq("order_id", orderId)
                .ne("status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus())
        );
        if (count == 0) {
            MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().select("order_id,site_id,order_money").eq("order_id", orderId));
            MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                    .select("SUM(money) AS money")
                    .eq("order_id", orderId)
                    .eq("status", OrderRefundStatusEnum.FINISH.getStatus()));

            if (refund.getMoney().compareTo(order.getOrderMoney()) != -1) {
                OrderCloseParam closeParam = new OrderCloseParam();
                closeParam.setOrderId(order.getOrderId());
                closeParam.setSiteId(order.getSiteId());
                closeParam.setCloseType(OrderCloseTypeEnum.REFUND_CLOSE.getType());
                closeParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                closeParam.setMainId(0);
                this.close(closeParam);
            } else {
                OrderFinishParam finishParam = new OrderFinishParam();
                finishParam.setOrderId(order.getOrderId());
                finishParam.setSiteId(order.getSiteId());
                finishParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                finishParam.setMainId(0);
                coreOrderFinishService.finish(finishParam);
            }
        }
    }
}
