package com.niu.mall.service.core.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.task.ThreadPoolManager;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderGroup;
import com.niu.mall.enums.order.*;
import com.niu.mall.event.order.OrderPayAfterEvent;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderGroupMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.order.ICoreOrderEventService;
import com.niu.mall.service.core.order.ICoreOrderPayService;
import com.niu.mall.service.core.order.param.OrderPayParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Service
public class CoreOrderPayServiceImpl implements ICoreOrderPayService {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    MallOrderGroupMapper mallOrderGroupMapper;

    @Resource
    SiteShopMapper shopMapper;

    @Resource
    ICoreOrderEventService coreOrderEventService;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    /**
     * 订单支付成功
     *
     * @param param
     */
    @Transactional
    public void pay(OrderPayParam param) {
        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().select("order_id,status").eq("order_id", param.getTradeId()));
        if (order == null) throw new CommonException("订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString())) throw new CommonException("订单状态异常");

        // 变更订单状态
        order.setStatus(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString());
        order.setTimeout(0L);
        order.setIsEnableRefund(1);
        order.setOutTradeNo(param.getOutTradeNo());
        order.setPayTime(System.currentTimeMillis() / 1000);
        mallOrderMapper.updateById(order);

        // 变更订单项发货状态
        MallOrderGoods orderGoods = new MallOrderGoods();
        orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus());
        orderGoods.setIsEnableRefund(1);
        mallOrderGoodsMapper.update(orderGoods, new QueryWrapper<MallOrderGoods>().eq("order_id", order.getOrderId()));

        order = mallOrderMapper.selectById(order.getOrderId());


        MallOrder finalOrder = order;

        //计算佣金
        SiteShop shop = shopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", order.getSiteId()));
        BigDecimal commissionRate = shop.getCommissionRate();
        order.setMallRate(commissionRate);
        if(commissionRate.compareTo(BigDecimal.ZERO) > 0 )
        {
            BigDecimal orderMoney = (order.getOrderMoney().subtract(order.getDeliveryMoney())).setScale(2, RoundingMode.HALF_UP);
            BigDecimal mallMoney = (orderMoney.multiply(commissionRate.divide(new BigDecimal(100)))).setScale(2, RoundingMode.HALF_UP);
            BigDecimal shopMoney = (order.getOrderMoney().subtract(mallMoney)).setScale(2, RoundingMode.HALF_UP);
            if(order.getSupplyId() > 0){
                order.setSupplyMoney(order.getCostGoodsMoney());
            }
            order.setMallMoney(mallMoney);
            order.setShopMoney(shopMoney);
        }
        mallOrderMapper.updateById(order);

        //同步订单成功，打印
        coreOrderEventService.orderPayAfter(finalOrder, param);

        // 订单支付之后事件
        OrderPayAfterEvent event = new OrderPayAfterEvent();
        event.setSiteId(order.getSiteId());
        event.addAppSign("mall");
        event.setName("OrderPayAfterEvent");
        event.setOrder(order);
        EventAndSubscribeOfPublisher.publishAll(event);

    }

    @Override
    public void payOfGroup(OrderPayParam param) {

        MallOrderGroup orderGroup = mallOrderGroupMapper.selectOne(new QueryWrapper<MallOrderGroup>().eq("group_id", param.getTradeId()));
        if(ObjectUtil.isEmpty(orderGroup)) throw new CommonException("订单信息不存在");
        if(!orderGroup.getStatus().equals(OrderGroupEnum.WAIT_PAY.getStatus())) throw new CommonException("订单状态异常,请勿重复支付");
        List<MallOrder> orderList = mallOrderMapper.selectList(new QueryWrapper<MallOrder>().eq("group_id", param.getTradeId()));
        if(ObjectUtil.isEmpty(orderList)) throw new CommonException("订单信息不存在");
        for (MallOrder order : orderList) {
            OrderPayParam orderPayParam = new OrderPayParam();
            BeanUtil.copyProperties(param, orderPayParam);
            orderPayParam.setTradeId(order.getOrderId());
            pay(orderPayParam);
        }
        orderGroup.setStatus(OrderGroupEnum.PAID.getStatus());
        orderGroup.setPayTime(System.currentTimeMillis() / 1000);
        mallOrderGroupMapper.updateById(orderGroup);

    }
}
