package com.niu.mall.service.core.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.event.fenxiao.*;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderGroup;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderGroupEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.enums.order.OrderTypePayEnum;
import com.niu.mall.event.order.MallOrderPayCustomEvent;
import com.niu.mall.event.order.MallOrderPayEvent;
import com.niu.mall.event.order.OrderPayAfterEvent;
import com.niu.core.enums.fenxiao.FenxiaoTradeTypeEnum;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;


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
        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().select("order_id,status,order_type").eq("order_id", param.getTradeId()));
        if (order == null) throw new CommonException("订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString()) ||
                OrderStatusEnum.CLOSE.getStatus().toString().equals(order.getStatus()))
            throw new CommonException("订单状态异常");

        OrderTypePayEnum type = OrderTypePayEnum.pintuan.getEnumByType(order.getOrderType());
        boolean isCustom = type != null && type.isCustom();

        if (isCustom) {
            // 对照 PHP CoreOrderPayService::pay custom 路径，仅更新支付相关字段
            MallOrder payUpdate = new MallOrder();
            payUpdate.setOrderId(order.getOrderId());
            payUpdate.setTimeout(0L);
            payUpdate.setIsEnableRefund(1);
            payUpdate.setOutTradeNo(param.getOutTradeNo());
            payUpdate.setPayTime(System.currentTimeMillis() / 1000);
            mallOrderMapper.updateById(payUpdate);
        } else {
            order.setStatus(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString());
            order.setTimeout(0L);
            order.setIsEnableRefund(1);
            order.setOutTradeNo(param.getOutTradeNo());
            order.setPayTime(System.currentTimeMillis() / 1000);
            mallOrderMapper.updateById(order);

            MallOrderGoods orderGoods = new MallOrderGoods();
            orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus());
            orderGoods.setIsEnableRefund(1);
            mallOrderGoodsMapper.update(orderGoods, new QueryWrapper<MallOrderGoods>().eq("order_id", order.getOrderId()));
        }

        order = mallOrderMapper.selectById(order.getOrderId());

        if (isCustom) {
            orderPayCustomEvent(order, param);
        } else {
            orderPayEvent(order, OrderStatusEnum.WAIT_DELIVERY.getStatus().toString(), param);
        }

    }

    @Override
    public void payOfGroup(OrderPayParam param) {

        MallOrderGroup orderGroup = mallOrderGroupMapper.selectOne(new QueryWrapper<MallOrderGroup>().eq("group_id", param.getTradeId()));
        if (ObjectUtil.isEmpty(orderGroup)) throw new CommonException("订单信息不存在");
        if (!orderGroup.getStatus().equals(OrderGroupEnum.WAIT_PAY.getStatus()))
            throw new CommonException("订单状态异常,请勿重复支付");
        List<MallOrder> orderList = mallOrderMapper.selectList(new QueryWrapper<MallOrder>().eq("group_id", param.getTradeId()));
        if (ObjectUtil.isEmpty(orderList)) throw new CommonException("订单信息不存在");
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

    @Override
    @Transactional
    public void orderPayEvent(MallOrder order, String status) {
        // 变更订单状态
        MallOrder orderObj = new MallOrder();
        orderObj.setOrderId(order.getOrderId());
        orderObj.setStatus(status);
        mallOrderMapper.updateById(orderObj);

        //订单到达发货状态
        this.orderGoodsPay(order.getOrderId(), status);

        if (OrderStatusEnum.WAIT_DELIVERY.getStatus().toString().equals(status)) {
            MallOrder orderEvent = mallOrderMapper.selectById(order.getOrderId());
            // 订单支付之后事件
            MallOrderPayEvent event = new MallOrderPayEvent();
            event.setSiteId(order.getSiteId());
            event.addAppSign("mall");
            event.setName("MallOrderPayEvent");
            event.setOrder(orderEvent);
            EventAndSubscribeOfPublisher.publishAll(event);

            publishFenxiaoOrderCreate(orderEvent);
        }
    }

    @Override
    @Transactional
    public void orderPayEvent(MallOrder order, String status, OrderPayParam param) {
        // 变更订单状态
        MallOrder orderObj = new MallOrder();
        orderObj.setOrderId(order.getOrderId());
        orderObj.setStatus(status);
        mallOrderMapper.updateById(orderObj);

        //订单到达发货状态
        this.orderGoodsPay(order.getOrderId(), status);

        if (OrderStatusEnum.WAIT_DELIVERY.getStatus().toString().equals(status)) {
            MallOrder orderEvent = mallOrderMapper.selectById(order.getOrderId());
            // 订单支付之后事件
            MallOrderPayEvent payEvent = new MallOrderPayEvent();
            payEvent.setSiteId(order.getSiteId());
            payEvent.addAppSign("mall");
            payEvent.setName("MallOrderPayEvent");
            payEvent.setOrder(orderEvent);
            EventAndSubscribeOfPublisher.publishAll(payEvent);

            publishFenxiaoOrderCreate(orderEvent);
        }

        order = mallOrderMapper.selectById(order.getOrderId());
        MallOrder finalOrder = order;

        //计算佣金
        shopOrderCalc(order);

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

    private void orderPayCustomEvent(MallOrder order, OrderPayParam param) {
        // 发布自定义订单支付事件，供插件监听
        MallOrderPayCustomEvent event = new MallOrderPayCustomEvent(order, param);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    private void orderGoodsPay(Integer orderId, String status) {
        MallOrderGoods orderGoods = new MallOrderGoods();
        orderGoods.setIsEnableRefund(1);
        if (OrderStatusEnum.WAIT_DELIVERY.getStatus().toString().equals(status)) {
            orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus());
        }
        mallOrderGoodsMapper.update(orderGoods, new QueryWrapper<MallOrderGoods>().eq("order_id", orderId));
        // 2. 检查不可退款的商品（虚拟商品核销且退款设置为0）
        this.checkOrderGoodsNotRefund(orderId);
    }

    private void checkOrderGoodsNotRefund(Integer orderId) {
        // 查询订单项中符合条件（虚拟商品、核销、退款=0）的 goods_id
        List<MallOrderGoods> goodsList = mallOrderGoodsMapper.selectList(
                new QueryWrapper<MallOrderGoods>()
                        .eq("order_id", orderId)
                        .eq("goods_type", GoodsTypeEnum.VIRTUAL.getType())
                        .apply("JSON_EXTRACT(goods_config, '$.virtual_delivery_type') = 'verify'")
                        .apply("JSON_EXTRACT(goods_config, '$.virtual_refund') = 0")
        );
        if (!goodsList.isEmpty()) {
            List<Integer> orderGoodsIds = goodsList.stream().map(MallOrderGoods::getOrderGoodsId).collect(Collectors.toList());
            MallOrderGoods update = new MallOrderGoods();
            update.setIsEnableRefund(0);
            mallOrderGoodsMapper.update(update, new QueryWrapper<MallOrderGoods>().in("order_goods_id", orderGoodsIds));
        }
    }

    /**
     * 计算佣金
     *
     * @param order
     */
    private void shopOrderCalc(MallOrder order) {
        SiteShop shop = shopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", order.getSiteId()));
        BigDecimal commissionRate = shop.getServiceRate() != null ? shop.getServiceRate() : BigDecimal.ZERO;
        BigDecimal orderMoney = order.getOrderMoney().subtract(order.getDeliveryMoney()).setScale(2, RoundingMode.DOWN);
        BigDecimal mallMoney = orderMoney.multiply(commissionRate).divide(new BigDecimal("100"), 2, RoundingMode.DOWN);
        BigDecimal shopMoney = order.getOrderMoney().subtract(mallMoney).setScale(2, RoundingMode.DOWN);
        order.setMallRate(commissionRate);
        order.setMallMoney(mallMoney);
        order.setShopMoney(shopMoney);
        if (order.getSupplyId() > 0) {
            order.setSupplyMoney(order.getCostGoodsMoney());
        }
        mallOrderMapper.updateById(order);
    }

    /**
     * 支付成功后发布通用分销订单创建事件
     */
    private void publishFenxiaoOrderCreate(MallOrder order) {
        List<MallOrderGoods> goodsList = mallOrderGoodsMapper.selectList(
                new QueryWrapper<MallOrderGoods>().eq("order_id", order.getOrderId()));
        List<FenxiaoOrderGoodsItem> items = new java.util.ArrayList<>();
        for (MallOrderGoods goods : goodsList) {
            items.add(FenxiaoOrderEventUtil.buildGoodsItem(
                    goods.getOrderGoodsId(), goods.getGoodsId(), goods.getSkuId(), goods.getNum(),
                    goods.getOrderGoodsMoney(), goods.getSupplyOrderId(), goods.getCostGoodsMoney()));
        }
        FenxiaoOrderData orderData = FenxiaoOrderEventUtil.buildOrderData(
                order.getSiteId(), order.getOrderId(), order.getOrderNo(), order.getMemberId(), order.getOrderMoney());
        FenxiaoOrderCreateEvent fenxiaoEvent =
                FenxiaoOrderEventUtil.buildCreateEvent(FenxiaoTradeTypeEnum.MALL.getType(), orderData, items);
        EventAndSubscribeOfPublisher.publishAll(fenxiaoEvent);
    }
}
