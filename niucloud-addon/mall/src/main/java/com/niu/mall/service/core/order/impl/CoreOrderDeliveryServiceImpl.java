package com.niu.mall.service.core.order.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.express.WxMaExpressDelivery;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.*;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderCreateService;
import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCreateParam;
import com.niu.core.service.core.weapp.ICoreWeappDeliveryService;
import com.niu.core.service.core.weapp.vo.IsTradeManagedVo;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDelivery;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderGoodsRefundEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.event.order.OrderDeliveryAfterEvent;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.order.MallOrderDeliveryMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.delivery.ICoreElectronicSheetService;
import com.niu.core.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.core.service.core.delivery.vo.ElectronicSheetPrintVo;
import com.niu.mall.service.core.order.ICoreOrderDeliveryService;
import com.niu.mall.service.core.order.ICoreOrderEventService;
import com.niu.mall.service.core.order.ICoreOrderFinishService;
import com.niu.mall.service.core.order.param.OrderDeliveryDataParam;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import com.niu.mall.service.core.order.vo.MallOrderGoodsDeliveryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
        if (ObjectUtil.isNotEmpty(orderDeliveryParam.getDeliveryIds())){
            if(DeliveryTypeEnum.LOCAL_DELIVERY.getType().equals(deliveryType)) {
                queryWrapper.in("og.delivery_status", OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus(), OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus(), OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
            }else{
                queryWrapper.eq("og.delivery_status", OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
            }
        }else{
            queryWrapper.eq("og.delivery_status", OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus());
        }
        queryWrapper.eq("og.order_id", orderId);
        queryWrapper.in("og.order_goods_id", orderGoodsIds);
        queryWrapper.eq("og.status", OrderGoodsRefundEnum.NORMAL.getStatus());
        List<MallOrderGoodsDeliveryVo> orderGoodsData = mallOrderGoodsMapper.selectJoinList(MallOrderGoodsDeliveryVo.class,queryWrapper);

        if (orderGoodsData.size() != orderGoodsIds.size()) throw new CommonException("订单中存在已退款的商品，不能发货");

        List<String> hasGoodsTypeArray = new ArrayList<>();
        for (MallOrderGoodsDeliveryVo orderGoods : orderGoodsData) {
            if (!hasGoodsTypeArray.contains(orderGoods.getGoodsType()))
                hasGoodsTypeArray.add(orderGoods.getGoodsType());
        }

        if (hasGoodsTypeArray.size() > 1) throw new CommonException("一次发货只能发送一种商品类型的订单项");
        String goodsType = hasGoodsTypeArray.get(0);

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
        Integer deliveryId = this.packages(orderDeliveryParam.getDeliveryIds(),mallOrderDelivery);

        //虚拟商品自动收货
        for (MallOrderGoodsDeliveryVo orderGoods : orderGoodsData) {
            MallOrderGoods orderGoodsUpdate = new MallOrderGoods();
            orderGoodsUpdate.setOrderGoodsId(orderGoods.getOrderGoodsId());
            orderGoodsUpdate.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
            orderGoodsUpdate.setDeliveryId(deliveryId);
            mallOrderGoodsMapper.updateById(orderGoodsUpdate);
        }

        //调用虚拟配送(生成核销码)
        List<Integer> goodsIds = orderGoodsData.stream().map(MallOrderGoodsDeliveryVo::getGoodsId).collect(Collectors.toList());
        List<MallGoods> tempGoodsList = mallGoodsMapper.selectList(new QueryWrapper<MallGoods>().in("goods_id", goodsIds));
        for (MallOrderGoodsDeliveryVo orderGoods : orderGoodsData) {
            for (MallGoods mallGoods : tempGoodsList) {
                if (orderGoods.getGoodsId().equals(mallGoods.getGoodsId())) {
                    if (mallGoods.getVirtualReceiveType().equals("verify")) {
                        //生成核销码
                        Long expireTime;
                        if (mallGoods.getVirtualVerifyType() == 0) {
                            expireTime = 0L;
                        } else if (mallGoods.getVirtualVerifyType() == 1) {

                            expireTime = System.currentTimeMillis() / 1000 + mallGoods.getVirtualIndate() * 86400;
                        } else {
                            expireTime = (long) mallGoods.getVirtualIndate();
                        }
                        MallOrderGoods orderGoodsUpdate = new MallOrderGoods();
                        orderGoodsUpdate.setOrderGoodsId(orderGoods.getOrderGoodsId());
                        orderGoodsUpdate.setVerifyExpireTime(expireTime);
                        orderGoodsUpdate.setIsVerify(1);
                        mallOrderGoodsMapper.updateById(orderGoodsUpdate);
                    } else if (mallGoods.getVirtualReceiveType().equals("auto")) {
                        orderFinishService.orderGoodsTake(orderGoods.getOrderId(), new Integer[]{orderGoods.getOrderGoodsId()});
                    }

                }
            }
        }


    }

    @Override
    public Integer packages(Integer[] deliverIds,MallOrderDelivery mallOrderDelivery) {
        if(ObjectUtil.isNotEmpty(deliverIds)){
            if (ObjectUtil.isNotEmpty(mallOrderDelivery) && ObjectUtil.isEmpty(mallOrderDelivery.getExpressCompanyId())){
                mallOrderDelivery.setExpressCompanyId(0);
            }
            mallOrderDeliveryMapper.update(mallOrderDelivery,new UpdateWrapper<MallOrderDelivery>().in("id",deliverIds));
        }else{
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

        //添加包裹
        MallOrderDelivery mallOrderDelivery = new MallOrderDelivery();
        mallOrderDelivery.setSiteId(orderData.getSiteId());
        mallOrderDelivery.setOrderId(orderData.getOrderId());
        mallOrderDelivery.setDeliveryType(orderData.getDeliveryType());
        mallOrderDelivery.setSubDeliveryType(deliveryType);
        mallOrderDelivery.setRemark(orderDeliveryParam.getRemark());
        mallOrderDelivery.setExpressCompanyId(orderDeliveryParam.getExpressCompanyId());
        mallOrderDelivery.setExpressNumber(orderDeliveryParam.getExpressNumber());

        switch (orderDeliveryDataParam.getParam().getDeliveryWay()) {
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
                param.setOrderGoodsIds(CollStreamUtil.toList(orderDeliveryDataParam.getOrderGoodsData(), MallOrderGoodsDeliveryVo::getGoodsId));

                List<ElectronicSheetPrintVo> voList = coreElectronicSheetService.printElectronicSheet(param);
                if (ObjectUtil.isNotEmpty(voList)) {
                    mallOrderDelivery.setExpressNumber(String.valueOf(voList.get(0).getOrderInfo().get("LogisticCode")));
                }
            }
        }

        Integer deliveryId = this.packages(orderDeliveryParam.getDeliveryIds(),mallOrderDelivery);

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

        for (MallOrderGoodsDeliveryVo orderGoods : orderGoodsData) {
            MallOrderGoods orderGoodsUpdate = new MallOrderGoods();
            orderGoodsUpdate.setOrderGoodsId(orderGoods.getOrderGoodsId());
            orderGoodsUpdate.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
            orderGoodsUpdate.setDeliveryId(deliveryId);
            mallOrderGoodsMapper.updateById(orderGoodsUpdate);
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
        param.setLocalDeliveryId(orderDeliveryParam.getLocalDeliverId());
        param.setLocalDeliveryNo(localDeliveryNo);
        param.setLocalDeliveryType(orderDeliveryParam.getLocalDeliveryType());
        param.setOrderData(orderInfo);
        coreLocalDeliveryOrderCreateService.create(param);
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

        if (orderGoodsCount.intValue() == 0L && orderGoodsDeliveryCount.intValue() > 0) {
            this.finish(orderDeliveryParam);
        }
        return true;
    }

    @Override
    public boolean finish(OrderDeliveryParam orderDeliveryParam) {

        //查询订单
        QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderDeliveryParam.getOrderId())
                .eq("site_id", orderDeliveryParam.getSiteId());
        if (orderDeliveryParam.getDeliveryIds() != null && orderDeliveryParam.getDeliveryIds().length > 0){
            queryWrapper.eq("status",OrderGoodsRefundEnum.REFUND_FINISH.getStatus());
        }else{
            queryWrapper.eq("status",OrderStatusEnum.WAIT_DELIVERY.getStatus());
        }
        MallOrder order =    mallOrderMapper.selectOne(queryWrapper);
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

        //如果有且只有一个虚拟商品并且已经收货，则订单完成
        List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("order_id", orderDeliveryParam.getOrderId()));
        if (orderGoodsList.size() == 1) {
            MallOrderGoods orderGoods = orderGoodsList.get(0);
            if (orderGoods.getGoodsType().equals(GoodsTypeEnum.VIRTUAL.getType()) && orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.TAKED.getStatus())) {
                OrderFinishParam finishParam = new OrderFinishParam();
                finishParam.setOrderId(orderDeliveryParam.getOrderId());
                finishParam.setSiteId(orderDeliveryParam.getSiteId());
                orderFinishService.finish(finishParam);
            }
        }
        return true;

    }

    /**
     * 微信小程序发货入口
     * @param order
     */
    @Override
    public void orderShippingUploadShippingInfo(MallOrder order) {
        try {
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", order.getOutTradeNo()));
            if (pay == null || !pay.getType().equals("wechatpay")) return;

            WxMaService miniapp = WechatUtils.miniapp(order.getSiteId());

            WxMaOrderShippingInfoUploadRequest request = new WxMaOrderShippingInfoUploadRequest();

            // 需要上传物流信息的订单
            OrderKeyBean orderKey = new OrderKeyBean();
            orderKey.setOrderNumberType(1);
            orderKey.setOutTradeNo(order.getOutTradeNo());
            request.setOrderKey(orderKey);

            request.setUploadTime(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)).toString());

            // 物流模式，发货方式枚举值：1、实体物流配送采用快递公司进行实体物流配送形式 2、同城配送 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式 4、用户自提
            if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
                request.setLogisticsType(1);
            } else if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType())) {
                request.setLogisticsType(3);
            }

            // 发货模式，发货模式枚举值：1、UNIFIED_DELIVERY（统一发货）2、SPLIT_DELIVERY（分拆发货）
            request.setDeliveryMode(1);
            request.setIsAllDelivered(true);

            // 物流信息列表
            List<MallOrderDelivery> packageList = mallOrderDeliveryMapper.selectJoinList(MallOrderDelivery.class, new MPJQueryWrapper<MallOrderDelivery>()
                    .setAlias("d").innerJoin("?_mall_delivery_company c ON d.express_company_id = c.company_id".replace("?_", GlobalConfig.tablePrefix))
                    .select("express_number,id,express_company_id,c.company_name AS name")
                   .eq("order_id", order.getOrderId())
                   .eq("d.site_id", order.getSiteId())
            );

            List<WxMaExpressDelivery> expressDelivery = miniapp.getExpressService().getAllDelivery();

            if (packageList.size() > 0) {
                List<ShippingListBean> shippingList = new LinkedList<>();

                for (MallOrderDelivery mallOrderDelivery : packageList) {
                    ShippingListBean shippingListBean = new ShippingListBean();
                    if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
                        shippingListBean.setTrackingNo(mallOrderDelivery.getExpressNumber());
                        shippingListBean.setExpressCompany("");
                        expressDelivery.forEach(i -> {
                            if (i.getDeliveryName().equals(mallOrderDelivery.getName())) {
                                shippingListBean.setExpressCompany(i.getDeliveryId());
                            }
                        });
                    }
                    ContactBean contact = new ContactBean();
                    contact.setReceiverContact(order.getTakerMobile().substring(0, 3) + "****" + order.getTakerMobile().substring(7));
                    MallOrderGoods orderGoods = mallOrderGoodsMapper.selectOne(new QueryWrapper<MallOrderGoods>().select("goods_name GROUP_CONCAT(SEPARATOR ';') AS goods_name").eq("delivery_id", mallOrderDelivery.getId()).groupBy("delivery_id"));
                    shippingListBean.setItemDesc(orderGoods.getGoodsName());
                    shippingListBean.setContact(contact);

                    shippingList.add(shippingListBean);
                }

                request.setDeliveryMode(2);
                request.setShippingList(shippingList);
            }

            JSONObject json = JSONUtil.parseObj(pay.getJson());

            PayerBean payer = new PayerBean();
            payer.setOpenid(json.getStr("openid", ""));
            request.setPayer(payer);

            WxMaOrderShippingInfoBaseResponse response = miniapp.getWxMaOrderShippingService().upload(request);
            System.out.println("uploadShippingInfo response " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("uploadShippingInfo error");
        }
    }

    @Override
    public void orderShippingNotifyConfirmReceive(MallOrder order) {
        try {
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", order.getOutTradeNo()));
            if (pay == null || !pay.getType().equals("wechatpay")) return;
            if (!order.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) return;
            WxMaService miniapp = WechatUtils.miniapp(order.getSiteId());

            // 检测微信小程序是否已开通发货信息管理服务
            IsTradeManagedVo isTradeManaged = coreWeappDeliveryService.getIsTradeManaged(order.getSiteId());
            if(ObjectUtil.isNotEmpty(isTradeManaged.getIsTradeManaged())){
                throw new CommonException("发货信息录入接口，报错：");
            }

            WxMaOrderShippingInfoBaseResponse resultJumpPath = coreWeappDeliveryService.setMsgJumpPath(order.getSiteId(), "mall_order");
            if(resultJumpPath.getErrCode() != 0){
                throw new CommonException("设置消息跳转路径设置接口，报错："+resultJumpPath.getErrMsg());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("uploadShippingInfo error");
        }
    }

}
