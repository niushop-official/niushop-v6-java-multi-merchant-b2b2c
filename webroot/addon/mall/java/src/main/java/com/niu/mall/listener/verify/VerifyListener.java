package com.niu.mall.listener.verify;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.event.DefaultEventResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.event.sys.VerifyEventDefiner;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderGoodsRefundEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.event.verify.AfterMallOrderVerifyEvent;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderDeliveryService;
import com.niu.mall.service.core.order.ICoreOrderFinishService;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EventCallback("mall")
@Component
public class VerifyListener extends VerifyEventDefiner {

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    ICoreOrderFinishService coreOrderFinishService;
    @Resource
    ICoreOrderDeliveryService coreOrderDeliveryService;
    @Resource
    ICoreAddonService coreAddonService;


    @Override
    public DefaultEventResult handleCallback(VerifyEvent event) {
        if ("shopVirtualGoods".equals(event.getType())) {
            Integer siteId = event.getSiteId();
            Map<String, Object> data = event.getData();
            Integer orderGoodsId = Integer.valueOf(String.valueOf(data.get("order_goods_id")));

            MallOrderGoods orderGoods = mallOrderGoodsMapper.selectOne(
                    new QueryWrapper<MallOrderGoods>()
                            .eq("order_goods_id", orderGoodsId)
            );
            if (orderGoods == null) {
                throw new CommonException("订单不存在");
            }

            if (OrderDeliveryStatusEnum.TAKED.getStatus().equals(orderGoods.getDeliveryStatus()) 
                || OrderDeliveryStatusEnum.EXPIRE.getStatus().equals(orderGoods.getDeliveryStatus())) {
                throw new CommonException("商品已核销或已过期");
            }

            if (OrderGoodsRefundEnum.REFUNDING.getStatus().equals(orderGoods.getStatus()) 
                || OrderGoodsRefundEnum.REFUND_FINISH.getStatus().equals(orderGoods.getStatus())) {
                throw new CommonException("商品正在退款或已退款");
            }

            if (orderGoods.getVerifyExpireTime() != null && orderGoods.getVerifyExpireTime() > 0 
                && orderGoods.getVerifyExpireTime() < System.currentTimeMillis() / 1000) {
                throw new CommonException("商品已过期");
            }

            if (orderGoods.getVerifyCount() >= orderGoods.getNum()) {
                throw new CommonException("订单已达到最大核销次数");
            }

            MallOrder order = mallOrderMapper.selectById(orderGoods.getOrderId());
            if (order == null) {
                throw new CommonException("订单不存在");
            }

            if (OrderStatusEnum.FINISH.getStatus().equals(order.getStatus()) 
                || OrderStatusEnum.CLOSE.getStatus().equals(order.getStatus())) {
                throw new CommonException("订单已关闭或已完成");
            }

            // 核销次数累加1
            MallOrderGoods orderGoodsUpdate = new MallOrderGoods();
            orderGoodsUpdate.setOrderGoodsId(orderGoodsId);
            orderGoodsUpdate.setVerifyCount(orderGoods.getVerifyCount() + 1);
            mallOrderGoodsMapper.updateById(orderGoodsUpdate);

            // 核销完成，订单完成
            if (orderGoodsUpdate.getVerifyCount() >= orderGoods.getNum()) {
                // 非小程序订单直接完成  没有后台控制 小程序订单 不能主动确认收货
                //if (!"weapp".equals(order.getOrderFrom())) {
                    OrderFinishParam finishParam = new OrderFinishParam();
                    finishParam.setOrderId(order.getOrderId());
                    finishParam.setSiteId(order.getSiteId());
                    coreOrderFinishService.finish(finishParam);
                //}
            }

            // 监听商城订单核销，同步供货商订单核销
            if (checkAddon("supply")) {
                if (orderGoods.getSupplyOrderGoodsId() != null && orderGoods.getSupplyOrderGoodsId() > 0) {
                    AfterMallOrderVerifyEvent eventSupply = new AfterMallOrderVerifyEvent();
                    eventSupply.setOrderGoodsId(orderGoods.getSupplyOrderGoodsId());
                    eventSupply.addAppSign("supply");
                    eventSupply.setSiteId(siteId);
                    EventAndSubscribeOfPublisher.publishAll(eventSupply);
                }
            }
        } else if ("shopPickUpOrder".equals(event.getType())) {
            // 自提订单核销
            Integer siteId = event.getSiteId();
            Map<String, Object> data = event.getData();
            Integer orderId = Integer.valueOf(String.valueOf(data.get("order_id")));
            List<Integer> orderGoodsIds = (List<Integer>) data.get("order_goods_ids");
            String remark = (String) data.get("remark");

            MallOrder order = mallOrderMapper.selectOne(
                    new QueryWrapper<MallOrder>()
                            .eq("site_id", siteId)
                            .eq("order_id", orderId)
            );
            if (order == null) {
                throw new CommonException("订单不存在");
            }

            if (OrderStatusEnum.FINISH.getStatus().equals(order.getStatus()) 
                || OrderStatusEnum.CLOSE.getStatus().equals(order.getStatus())) {
                throw new CommonException("订单已关闭或已完成");
            }

            // 查询订单商品
            QueryWrapper<MallOrderGoods> orderGoodsWhere = new QueryWrapper<>();
            orderGoodsWhere.eq("order_id", orderId);
            orderGoodsWhere.eq("site_id", siteId);
            if (orderGoodsIds != null && !orderGoodsIds.isEmpty()) {
                orderGoodsWhere.in("order_goods_id", orderGoodsIds);
            }
            List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper.selectList(orderGoodsWhere);
            
            if (orderGoodsList == null || orderGoodsList.isEmpty()) {
                throw new CommonException("订单不存在");
            }

            // 检查订单商品状态
            for (MallOrderGoods orderGoods : orderGoodsList) {
                if (OrderDeliveryStatusEnum.TAKED.getStatus().equals(orderGoods.getDeliveryStatus()) 
                    || OrderDeliveryStatusEnum.EXPIRE.getStatus().equals(orderGoods.getDeliveryStatus())) {
                    throw new CommonException("商品已核销或已过期");
                }
                if (OrderGoodsRefundEnum.REFUNDING.getStatus().equals(orderGoods.getStatus()) 
                    || OrderGoodsRefundEnum.REFUND_FINISH.getStatus().equals(orderGoods.getStatus())) {
                    throw new CommonException("商品正在退款或已退款");
                }
            }

            // 提取订单商品ID
            List<Integer> finalOrderGoodsIds = new ArrayList<>();
            for (MallOrderGoods orderGoods : orderGoodsList) {
                finalOrderGoodsIds.add(orderGoods.getOrderGoodsId());
            }

            // 订单发货
            OrderDeliveryParam deliveryParam = new OrderDeliveryParam();
            deliveryParam.setSiteId(siteId);
            deliveryParam.setOrderId(orderId);
            deliveryParam.setOrderGoodsIds(finalOrderGoodsIds.toArray(new Integer[0]));
            deliveryParam.setDeliveryType(order.getDeliveryType());
            deliveryParam.setRemark(remark);
            coreOrderDeliveryService.delivery(deliveryParam);
        }
        return new DefaultEventResult();
    }

    /**
     * 检查插件是否安装
     * @param key
     */
    private Boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap= coreAddonService.getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }

}
