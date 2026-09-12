package com.niu.mall.service.core.order.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.core.service.core.delivery.vo.ElectronicSheetPrintVo;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderCreateService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderService;
import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCreateParam;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.vo.PayInfoVo;
import com.niu.core.service.core.weapp.ICoreWeappDeliveryService;
import com.niu.core.entity.goods.MallGoods;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDelivery;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderGoodsRefundEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.event.order.OrderDeliveryAfterEvent;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.order.MallOrderDeliveryMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.delivery.ICoreElectronicSheetService;
import com.niu.mall.service.core.order.ICoreOrderDeliveryContentService;
import com.niu.mall.service.core.order.ICoreOrderDeliveryService;
import com.niu.mall.service.core.order.ICoreOrderEventService;
import com.niu.mall.service.core.order.ICoreOrderFinishService;
import com.niu.mall.service.core.order.param.OrderDeliveryContentParam;
import com.niu.mall.service.core.order.param.OrderDeliveryDataParam;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import com.niu.mall.service.core.order.vo.MallOrderGoodsDeliveryVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.niu.core.common.utils.CommonUtils.createNo;

@Service
public class CoreOrderDeliveryServiceImpl implements ICoreOrderDeliveryService {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    ICoreOrderFinishService orderFinishService;

    @Resource
    MallOrderDeliveryMapper mallOrderDeliveryMapper;

    ICoreOrderEventService coreOrderEventService;

    @Resource
    ICoreElectronicSheetService coreElectronicSheetService;

    @Resource
    PayMapper payMapper;

    @Resource
    ICoreLocalDeliveryOrderCreateService coreLocalDeliveryOrderCreateService;
    @Resource
    ICoreWeappDeliveryService coreWeappDeliveryService;
    @Resource
    ICoreLocalDeliveryOrderService coreLocalDeliveryOrderService;
    @Resource
    ICoreOrderDeliveryContentService orderDeliveryContentService;
    @Resource
    private ICorePayService corePayService;

    @Resource
    public void setOrderFinishService(@Lazy ICoreOrderFinishService orderFinishService) {
        this.orderFinishService = orderFinishService;
    }

    @Resource
    public void setCoreOrderEventService(@Lazy ICoreOrderEventService coreOrderEventService) {
        this.coreOrderEventService = coreOrderEventService;
    }

    @Override
    @Transactional
    public void delivery(OrderDeliveryParam orderDeliveryParam) {

        Integer orderId = orderDeliveryParam.getOrderId();
        Integer siteId = orderDeliveryParam.getSiteId();
        List<Integer> orderGoodsIds = new ArrayList<>(Arrays.asList(orderDeliveryParam.getOrderGoodsIds()));
        MallOrder mallOrder = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>()
                .eq("order_id", orderId)
                .eq("site_id", siteId));
        if (ObjectUtil.isEmpty(mallOrder)) throw new CommonException("订单信息不能为空");

        if (!mallOrder.getStatus().equals(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString()) && ObjectUtil.isEmpty(orderDeliveryParam.getDeliveryIds()))
            throw new CommonException("订单状态不是待发货状态");

        MPJQueryWrapper<MallOrderGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("og")
                .innerJoin("?_mall_goods_sku gs ON og.sku_id = gs.sku_id ".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_mall_goods g ON og.goods_id = g.goods_id ".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("og.*");
        queryWrapper.select("gs.weight,g.unit");
        //配送
        String deliveryType = orderDeliveryParam.getDeliveryType();
        if (ObjectUtil.isNotEmpty(orderDeliveryParam.getDeliveryIds())) {
            queryWrapper.eq("og.delivery_status", OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
        } else {
            queryWrapper.eq("og.delivery_status", OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus());
        }
        queryWrapper.eq("og.order_id", orderId);
        queryWrapper.in("og.order_goods_id", orderGoodsIds);
        queryWrapper.eq("og.status", OrderGoodsRefundEnum.NORMAL.getStatus());

        if (OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType().equals(deliveryType)) {
            orderGoodsIds = mallOrderGoodsMapper.selectList(new LambdaQueryWrapper<MallOrderGoods>()
                    .eq(MallOrderGoods::getOrderId, orderId)
                    .in(MallOrderGoods::getOrderGoodsId, orderGoodsIds)
                    .ne(MallOrderGoods::getStatus, OrderGoodsRefundEnum.REFUND_FINISH.getStatus())
            ).stream().map(MallOrderGoods::getOrderGoodsId).toList();
        }

        List<MallOrderGoodsDeliveryVo> orderGoodsData = mallOrderGoodsMapper.selectJoinList(MallOrderGoodsDeliveryVo.class, queryWrapper);

        if (orderGoodsData.size() != orderGoodsIds.size()) throw new CommonException("订单中存在已退款的商品，不能发货");

        List<String> hasGoodsTypeArray = new ArrayList<>();
        for (MallOrderGoodsDeliveryVo orderGoods : orderGoodsData) {
            if (!hasGoodsTypeArray.contains(orderGoods.getGoodsType()))
                hasGoodsTypeArray.add(orderGoods.getGoodsType());
        }

        if (hasGoodsTypeArray.size() > 1) throw new CommonException("一次发货只能发送一种商品类型的订单项");
        String goodsType = hasGoodsTypeArray.get(0);
        if (OrderDeliveryTypeEnum.NONE_EXPRESS.getType().equals(orderDeliveryParam.getDeliveryType())) {
            orderDeliveryParam.setDeliveryWay("");
        }
        OrderDeliveryDataParam orderDeliveryDataParam = new OrderDeliveryDataParam();
        orderDeliveryDataParam.setOrderData(mallOrder);
        orderDeliveryDataParam.setOrderGoodsData(orderGoodsData);
        orderDeliveryDataParam.setParam(orderDeliveryParam);

        if (ObjectUtil.isNotEmpty(goodsType)) {
            if (goodsType.equals(GoodsTypeEnum.VIRTUAL.getType())) {
                if (!orderDeliveryParam.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType()))
                    throw new CommonException("虚拟商品只支持虚拟发货");
                this.virtual(orderDeliveryDataParam);
            } else {
                if (!OrderDeliveryTypeEnum.getMap().containsKey(orderDeliveryParam.getDeliveryType()))
                    throw new CommonException("不支持的配送方式");

                //物流配送
                if (mallOrder.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
                    this.express(orderDeliveryDataParam);
                }

                //同城配送
                if (mallOrder.getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                    this.localDelivery(orderDeliveryDataParam);
                }

                //门店自提
                if (mallOrder.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType())) {
                    this.store(orderDeliveryDataParam);
                }


            }
            this.checkFinish(orderDeliveryParam);
        }


    }

    @Override
    public void virtual(OrderDeliveryDataParam orderDeliveryDataParam) {

        MallOrder orderData = orderDeliveryDataParam.getOrderData();
        List<MallOrderGoodsDeliveryVo> orderGoodsData = orderDeliveryDataParam.getOrderGoodsData();
        OrderDeliveryParam orderDeliveryParam = orderDeliveryDataParam.getParam();
        String deliveryType = orderDeliveryParam.getDeliveryType();

        //添加包裹
        MallOrderDelivery mallOrderDelivery = new MallOrderDelivery();
        mallOrderDelivery.setSiteId(orderData.getSiteId());
        mallOrderDelivery.setOrderId(orderData.getOrderId());
        mallOrderDelivery.setDeliveryType(orderData.getDeliveryType());
        mallOrderDelivery.setSubDeliveryType(deliveryType);
        mallOrderDelivery.setRemark(orderDeliveryParam.getRemark());
        Integer deliveryId = this.packages(orderDeliveryParam.getDeliveryIds(), mallOrderDelivery);
        if (ObjectUtil.isEmpty(orderDeliveryParam.getDeliveryIds())) {
            //虚拟商品自动收货
            for (MallOrderGoodsDeliveryVo orderGoods : orderGoodsData) {
                MallOrderGoods orderGoodsUpdate = new MallOrderGoods();
                orderGoodsUpdate.setOrderGoodsId(orderGoods.getOrderGoodsId());
                orderGoodsUpdate.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
                orderGoodsUpdate.setDeliveryId(deliveryId);
                mallOrderGoodsMapper.updateById(orderGoodsUpdate);
            }

            // 构建商品ID -> 商品配置的映射
            List<Integer> goodsIds = orderGoodsData.stream().map(MallOrderGoodsDeliveryVo::getGoodsId).distinct().collect(Collectors.toList());
            Map<Integer, MallGoods> goodsMap = mallGoodsMapper.selectList(
                    new LambdaQueryWrapper<MallGoods>().in(MallGoods::getGoodsId, goodsIds)
            ).stream().collect(Collectors.toMap(MallGoods::getGoodsId, g -> g));

            //调用虚拟配送
            for (MallOrderGoodsDeliveryVo orderGoods : orderGoodsData) {
                MallGoods mallGoods = goodsMap.get(orderGoods.getGoodsId());
                if (mallGoods == null) continue;

                // 对齐 PHP CoreOrderDeliveryService：收发货配置读商品表
                String virtualDeliveryType = ObjectUtil.defaultIfEmpty(mallGoods.getVirtualDeliveryType(), "auto");
                String virtualReceiveType = ObjectUtil.defaultIfEmpty(mallGoods.getVirtualReceiveType(), "artificial");

                if (Arrays.asList("auto", "manual").contains(virtualDeliveryType)) {
                    String content = StringUtils.hasText(orderDeliveryParam.getVirtualDeliveryContent()) ?
                            orderDeliveryParam.getVirtualDeliveryContent()
                            : ObjectUtil.defaultIfNull(mallGoods.getVirtualDeliveryContent(), "");
                    OrderDeliveryContentParam param = new OrderDeliveryContentParam();
                    param.setSiteId(orderData.getSiteId());
                    param.setTradeId(orderData.getOrderId());
                    param.setTradeType("mall");
                    param.setTradeNo(orderData.getOrderNo());
                    param.setContent(content);
                    orderDeliveryContentService.add(param);
                }

                if ("auto".equals(virtualReceiveType)) {
                    orderFinishService.orderGoodsTake(orderGoods.getOrderId(),
                            new Integer[]{orderGoods.getOrderGoodsId()});
                }
            }
        }
    }

    @Override
    public Integer packages(Integer[] deliverIds, MallOrderDelivery mallOrderDelivery) {
        if (ObjectUtil.isNotEmpty(deliverIds)) {
            if (ObjectUtil.isNotEmpty(mallOrderDelivery) && ObjectUtil.isEmpty(mallOrderDelivery.getExpressCompanyId())) {
                mallOrderDelivery.setExpressCompanyId(0);
            }
            mallOrderDeliveryMapper.update(mallOrderDelivery, new UpdateWrapper<MallOrderDelivery>().in("id", deliverIds));
        } else {
            mallOrderDelivery.setCreateTime(System.currentTimeMillis() / 1000);
            mallOrderDelivery.setCode(createNo());
            mallOrderDeliveryMapper.insert(mallOrderDelivery);
        }
        return mallOrderDelivery.getId();
    }

    @Override
    public void express(OrderDeliveryDataParam orderDeliveryDataParam) {
        MallOrder orderData = orderDeliveryDataParam.getOrderData();
        List<MallOrderGoodsDeliveryVo> orderGoodsData = orderDeliveryDataParam.getOrderGoodsData();
        OrderDeliveryParam orderDeliveryParam = orderDeliveryDataParam.getParam();
        String deliveryType = orderDeliveryParam.getDeliveryType();
        String deliveryWay = ObjectUtil.defaultIfEmpty(orderDeliveryParam.getDeliveryWay(), "manual_write");
        //添加包裹
        MallOrderDelivery mallOrderDelivery = new MallOrderDelivery();
        mallOrderDelivery.setSiteId(orderData.getSiteId());
        mallOrderDelivery.setOrderId(orderData.getOrderId());
        mallOrderDelivery.setDeliveryType(orderData.getDeliveryType());
        mallOrderDelivery.setSubDeliveryType(deliveryType);
        mallOrderDelivery.setRemark(orderDeliveryParam.getRemark());
        mallOrderDelivery.setExpressCompanyId(orderDeliveryParam.getExpressCompanyId());
        mallOrderDelivery.setExpressNumber(orderDeliveryParam.getExpressNumber());

        switch (deliveryWay) {
            case "manual_write" -> {
                if (ObjectUtil.isNotEmpty(orderDeliveryDataParam.getParam().getExpressNumber())) {
                    QueryWrapper<MallOrderDelivery> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("order_id", orderData.getOrderId())
                            .eq("site_id", orderData.getSiteId())
                            .eq("express_number", orderDeliveryParam.getExpressNumber());
                    if (mallOrderDeliveryMapper.selectCount(queryWrapper) > 0) {
                        throw new RuntimeException("物流单号不能重复");
                    }
                }
            }
            case "electronic_sheet" -> {
                ShopDeliveryPrintElectronicSheetParam param = new ShopDeliveryPrintElectronicSheetParam();
                param.setSiteId(orderDeliveryDataParam.getOrderData().getSiteId());
                param.setOrderId(orderDeliveryDataParam.getOrderData().getOrderId());
                param.setElectronicSheetId(orderDeliveryDataParam.getParam().getElectronicSheetId());
                param.setOrderGoodsIds(CollStreamUtil.toList(orderDeliveryDataParam.getOrderGoodsData(), MallOrderGoodsDeliveryVo::getOrderGoodsId));
                param.setAddress(orderDeliveryParam.getAddress());

                List<ElectronicSheetPrintVo> voList = coreElectronicSheetService.printElectronicSheet(param);
                if (ObjectUtil.isEmpty(voList) || !voList.get(0).isSuccess()) {
                    String reason = ObjectUtil.isNotEmpty(voList) ? voList.get(0).getReason() : "电子面单打印失败";
                    throw new CommonException(reason);
                }
                mallOrderDelivery.setExpressNumber(String.valueOf(voList.get(0).getOrderInfo().get("LogisticCode")));
            }
        }

        Integer deliveryId = this.packages(orderDeliveryParam.getDeliveryIds(), mallOrderDelivery);

        for (MallOrderGoodsDeliveryVo orderGoods : orderGoodsData) {
            MallOrderGoods orderGoodsUpdate = new MallOrderGoods();
            orderGoodsUpdate.setOrderGoodsId(orderGoods.getOrderGoodsId());
            orderGoodsUpdate.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
            orderGoodsUpdate.setDeliveryId(deliveryId);
            mallOrderGoodsMapper.updateById(orderGoodsUpdate);
        }
    }


    @Override
    public void localDelivery(OrderDeliveryDataParam orderDeliveryDataParam) {

        //检查当前交易下是否有存在同城配送订单
        Boolean erUnderwayDeliveryOrder = coreLocalDeliveryOrderService.getUnderwayDeliveryOrder("mall", orderDeliveryDataParam.getOrderData().getOrderNo());
        if (!erUnderwayDeliveryOrder) {
            throw new CommonException("该订单已存在正在进行中的配送订单");
        }
        MallOrder orderData = orderDeliveryDataParam.getOrderData();
        List<MallOrderGoodsDeliveryVo> orderGoodsData = orderDeliveryDataParam.getOrderGoodsData();
        OrderDeliveryParam orderDeliveryParam = orderDeliveryDataParam.getParam();
        String deliveryType = orderDeliveryParam.getDeliveryType();

        //添加包裹
        String localDeliveryNo = createNo();
        MallOrderDelivery mallOrderDelivery = new MallOrderDelivery();
        mallOrderDelivery.setSiteId(orderData.getSiteId());
        mallOrderDelivery.setOrderId(orderData.getOrderId());
        mallOrderDelivery.setDeliveryType(orderData.getDeliveryType());
        mallOrderDelivery.setSubDeliveryType(deliveryType);
        mallOrderDelivery.setRemark(orderDeliveryParam.getRemark());
        mallOrderDelivery.setLocalDeliverId(orderDeliveryParam.getLocalDeliverId());
        mallOrderDelivery.setLocalDeliveryOrderId(localDeliveryNo);
        Integer deliveryId = this.packages(orderDeliveryParam.getDeliveryIds(), mallOrderDelivery);
        List<LocalDeliveryOrderCreateParam.OrderGoodsInfo> orderGoodsList = new ArrayList<>();
        for (MallOrderGoodsDeliveryVo orderGoods : orderGoodsData) {
            LocalDeliveryOrderCreateParam.OrderGoodsInfo goodsInfo = new LocalDeliveryOrderCreateParam.OrderGoodsInfo();
            MallOrderGoods orderGoodsUpdate = new MallOrderGoods();
            orderGoodsUpdate.setOrderGoodsId(orderGoods.getOrderGoodsId());
            orderGoodsUpdate.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
            orderGoodsUpdate.setDeliveryId(deliveryId);
            mallOrderGoodsMapper.updateById(orderGoodsUpdate);
            BeanUtils.copyProperties(orderGoods, goodsInfo);
            orderGoodsList.add(goodsInfo);
        }

        //同城配送订单创建
        LocalDeliveryOrderCreateParam param = new LocalDeliveryOrderCreateParam();
        LocalDeliveryOrderCreateParam.OrderDataInfo orderInfo = new LocalDeliveryOrderCreateParam.OrderDataInfo();
        BeanUtils.copyProperties(orderData, orderInfo);
        orderInfo.setTakerCityId(orderData.getTakerCity());
        orderInfo.setTakerDistrictId(orderData.getTakerDistrict());
        orderInfo.setTakerProvinceId(orderData.getTakerProvince());
        param.setDeliveryNo(localDeliveryNo);
        param.setRemark(orderDeliveryParam.getRemark());
        param.setDeliveryId(deliveryId);
        param.setBody(orderData.getBody());
        param.setStoreId(orderDeliveryParam.getStoreId());
        param.setLocalDeliveryId(orderDeliveryParam.getLocalDeliverId());
        param.setLocalDeliveryNo(localDeliveryNo);
        param.setLocalDeliveryType(orderDeliveryParam.getLocalDeliveryType());
        param.setOrderData(orderInfo);
        param.setGoodsWeight(orderDeliveryParam.getGoodsWeight());
        param.setOrderGoodsData(orderGoodsList);
        param.setTradeNo(orderData.getOrderNo());
        param.setTradeType("mall");
        coreLocalDeliveryOrderCreateService.create(param);
    }

    @Override
    public void store(OrderDeliveryDataParam orderDeliveryDataParam) {
        MallOrder orderData = orderDeliveryDataParam.getOrderData();
        List<MallOrderGoodsDeliveryVo> orderGoodsData = orderDeliveryDataParam.getOrderGoodsData();
        OrderDeliveryParam orderDeliveryParam = orderDeliveryDataParam.getParam();
        String deliveryType = orderDeliveryParam.getDeliveryType();

        //添加包裹
        MallOrderDelivery mallOrderDelivery = new MallOrderDelivery();
        mallOrderDelivery.setSiteId(orderData.getSiteId());
        mallOrderDelivery.setOrderId(orderData.getOrderId());
        mallOrderDelivery.setDeliveryType(orderData.getDeliveryType());
        mallOrderDelivery.setSubDeliveryType(deliveryType);
        mallOrderDelivery.setRemark(orderDeliveryParam.getRemark());
        Integer deliveryId = this.packages(orderDeliveryParam.getDeliveryIds(), mallOrderDelivery);
        if (ObjectUtil.isEmpty(orderDeliveryParam.getDeliveryIds())) {
            for (MallOrderGoodsDeliveryVo orderGoods : orderGoodsData) {
                Integer num = orderGoods.getNum() - orderGoods.getVerifyCount();
                if (num >= orderGoods.getNum()) {
                    MallOrderGoods orderGoodsUpdate = new MallOrderGoods();
                    orderGoodsUpdate.setOrderGoodsId(orderGoods.getOrderGoodsId());
                    orderGoodsUpdate.setVerifyCount(orderGoods.getVerifyCount() + orderGoods.getNum());
                    orderGoodsUpdate.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
                    orderGoodsUpdate.setDeliveryId(deliveryId);
                    mallOrderGoodsMapper.updateById(orderGoodsUpdate);
                }
            }
        }
    }

    @Override
    public boolean checkFinish(OrderDeliveryParam orderDeliveryParam) {

        Long orderGoodsCount = mallOrderGoodsMapper.selectCount(new QueryWrapper<MallOrderGoods>()
                .eq("order_id", orderDeliveryParam.getOrderId())
                .eq("delivery_status", OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus())
                .ne("status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus())
        );

        Long orderGoodsDeliveryCount = mallOrderGoodsMapper.selectCount(new QueryWrapper<MallOrderGoods>()
                .eq("order_id", orderDeliveryParam.getOrderId())
                .in("delivery_status", OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus(), OrderDeliveryStatusEnum.TAKED.getStatus())
                .ne("status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus())
        );

        //查询订单
        List<MallOrderGoods> ordersGoodsList = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>()
                .eq("order_id", orderDeliveryParam.getOrderId())
                .ne("status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus())
        );
        Boolean checkFinish = true;
        if (DeliveryTypeEnum.STORE.getType().equals(orderDeliveryParam.getDeliveryType())) {
            for (MallOrderGoods orderGoods : ordersGoodsList) {
                if (orderGoods.getNum() > orderGoods.getVerifyCount()) {
                    checkFinish = false;
                }
            }
        }

        if (orderGoodsCount.intValue() == 0L && orderGoodsDeliveryCount.intValue() > 0 && checkFinish) {
            this.finish(orderDeliveryParam);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean finish(OrderDeliveryParam orderDeliveryParam) {
        //查询订单
        QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderDeliveryParam.getOrderId())
                .eq("site_id", orderDeliveryParam.getSiteId());
        if (orderDeliveryParam.getDeliveryIds() != null && orderDeliveryParam.getDeliveryIds().length > 0) {
            queryWrapper.eq("status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus());
        } else {
            queryWrapper.eq("status", OrderStatusEnum.WAIT_DELIVERY.getStatus());
        }
        MallOrder order = mallOrderMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(order)) throw new CommonException("订单不存在或已发货");
        order.setDeliveryTime(System.currentTimeMillis() / 1000);
        order.setStatus(OrderStatusEnum.WAIT_TAKE.getStatus().toString());
        order.setTimeout(0L);
        mallOrderMapper.updateById(order);

        coreOrderEventService.orderDeliveryAfter(order, orderDeliveryParam);

        // 订单发货后事件
        OrderDeliveryAfterEvent event = new OrderDeliveryAfterEvent();
        event.setSiteId(order.getSiteId());
        event.addAppSign("supply");
        event.setOrder(order);
        EventAndSubscribeOfPublisher.publishAll(event);

        Boolean canFinish = false;
        // 全部有效商品行均为虚拟商品且已收货，则订单完成
        List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>()
                .eq("order_id", orderDeliveryParam.getOrderId())
                .ne("status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus()));
        if (!orderGoodsList.isEmpty() && orderGoodsList.stream().allMatch(orderGoods ->
                GoodsTypeEnum.VIRTUAL.getType().equals(orderGoods.getGoodsType())
                        && OrderDeliveryStatusEnum.TAKED.getStatus().equals(orderGoods.getDeliveryStatus()))) {
            canFinish = true;
        }
        //自提订单发货完成后，订单直接完成
        if (order.getDeliveryType().equals(DeliveryTypeEnum.STORE.getType())) {
            canFinish = true;
        }
        //如果是小程序订单，不能直接完成
        if (order.getOrderFrom().equals(ChannelEnum.WEAPP.getCode())) {
            PayInfoVo payInfo = corePayService.getPayInfoByOutTradeNo(order.getOutTradeNo());
            if (payInfo == null) {
                throw new CommonException("支付信息不存在");
            }
            if (ChannelEnum.WEAPP.getCode().equals(payInfo.getChannel()) &&
                    TransferTypeEnum.WECHATPAY.getKey().equals(payInfo.getType()) &&
                    !OrderDeliveryTypeEnum.VIRTUAL.getType().equals(order.getDeliveryType())) {
                canFinish = false;
            }
        }
        if (canFinish) {
            OrderFinishParam finishParam = new OrderFinishParam();
            finishParam.setOrderId(orderDeliveryParam.getOrderId());
            finishParam.setSiteId(orderDeliveryParam.getSiteId());
            orderFinishService.finish(finishParam);
        }

        return true;

    }
}
