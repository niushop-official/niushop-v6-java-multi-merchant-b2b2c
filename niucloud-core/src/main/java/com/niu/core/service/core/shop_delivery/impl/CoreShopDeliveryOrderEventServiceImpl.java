package com.niu.core.service.core.shop_delivery.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.enums.local_delivery.LocalDeliveryMainTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryOperateEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.event.local_delivery.LocalDeliveryEvent;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderLogService;
import com.niu.core.service.core.local_delivery.param.AddLocalDeliveryOrderLogParam;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderEventService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderLogService;
import com.niu.core.service.core.shop_delivery.param.AddShopDeliveryOrderLogParam;
import com.niu.core.service.core.shop_delivery.param.EventShopDeliveryOrderParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoreShopDeliveryOrderEventServiceImpl implements ICoreShopDeliveryOrderEventService {

    @Resource
    private DeliveryLocalDeliveryOrderMapper deliveryLocalDeliveryOrderMapper;
    @Resource
    private ICoreLocalDeliveryOrderLogService coreDeliveryOrderLogService;

    @Override
    @Transactional
    public Boolean riderPickedUp(EventShopDeliveryOrderParam param, String deliveryNo) {
        DeliveryLocalDeliveryOrder localDeliveryOrder = deliveryLocalDeliveryOrderMapper.selectOne(new QueryWrapper<DeliveryLocalDeliveryOrder>()
                .eq("delivery_no", deliveryNo).last("limit 1"));
        if (localDeliveryOrder == null) throw new CommonException("DELIVERY_ORDER_NOT_EXIT");
        if(LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().toString().equals(localDeliveryOrder.getStatus())){
            return true;
        }

        DeliveryLocalDeliveryOrder update = new DeliveryLocalDeliveryOrder();
        update.setId(localDeliveryOrder.getId());
        update.setStatus(LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().toString());
        update.setOutStatus(param.getStatus());
        if (StringUtils.isNotEmpty(param.getMainName())) {
            update.setRiderName(param.getMainName());
        }
        deliveryLocalDeliveryOrderMapper.updateById(update);

        LocalDeliveryEvent event = new LocalDeliveryEvent();
        event.setEvent("rider_pickedup");
        event.setTradeType(localDeliveryOrder.getTradeType());
        event.setTradeNo(localDeliveryOrder.getTradeNo());
        EventAndSubscribeOfPublisher.publishAll(event);

        //添加配送订单日志
        LocalDeliveryOperateEnum.OperateResult operateResult = LocalDeliveryOperateEnum.getOperateByStatus(LocalDeliveryStatusEnum.IN_DELIVERY.getStatus());
        AddLocalDeliveryOrderLogParam logParam = new AddLocalDeliveryOrderLogParam();
        logParam.setOrderId(localDeliveryOrder.getId());
        logParam.setMainId(param.getMainId());
        logParam.setMainName(param.getMainName());
        logParam.setMainType(LocalDeliveryMainTypeEnum.RIDER.getType());
        logParam.setOperate(operateResult.getOperate());
        logParam.setOperateDesc(operateResult.getOperateDesc());
        logParam.setStatus(LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().toString());
        logParam.setRemark(operateResult.getOperateDesc()+"骑手"+ (StringUtils.isNotEmpty(param.getMainName()) ? param.getMainName() : ""));
        coreDeliveryOrderLogService.addLog(logParam);
        return true;
    }

    @Override
    @Transactional
    public Boolean cancel(EventShopDeliveryOrderParam param, String deliveryNo) {
        DeliveryLocalDeliveryOrder localDeliveryOrder = deliveryLocalDeliveryOrderMapper.selectOne(new QueryWrapper<DeliveryLocalDeliveryOrder>()
                .eq("delivery_no", deliveryNo).last("limit 1"));
        if (localDeliveryOrder == null) throw new CommonException("DELIVERY_ORDER_NOT_EXIT");
        if(LocalDeliveryStatusEnum.CANCELED.getStatus().toString().equals(localDeliveryOrder.getStatus())){
            return true;
        }

        DeliveryLocalDeliveryOrder update = new DeliveryLocalDeliveryOrder();
        update.setId(localDeliveryOrder.getId());
        update.setStatus(LocalDeliveryStatusEnum.CANCELED.getStatus().toString());
        update.setOutStatus(param.getStatus());
        update.setRemark(param.getCancelReason());
        update.setCancelTime(System.currentTimeMillis() / 1000);
        deliveryLocalDeliveryOrderMapper.updateById(update);

        LocalDeliveryEvent event = new LocalDeliveryEvent();
        event.setEvent("cancel");
        event.setTradeType(localDeliveryOrder.getTradeType());
        event.setTradeNo(localDeliveryOrder.getTradeNo());
        EventAndSubscribeOfPublisher.publishAll(event);

        //添加配送订单日志
        LocalDeliveryOperateEnum.OperateResult operateResult = LocalDeliveryOperateEnum.getOperateByStatus(LocalDeliveryStatusEnum.CANCELED.getStatus());
        AddLocalDeliveryOrderLogParam logParam = new AddLocalDeliveryOrderLogParam();
        logParam.setOrderId(localDeliveryOrder.getId());
        logParam.setMainId(param.getMainId());
        logParam.setMainName(param.getMainName());
        logParam.setMainType(LocalDeliveryMainTypeEnum.SYSTEM.getType());
        logParam.setOperate(operateResult.getOperate());
        logParam.setOperateDesc(operateResult.getOperateDesc());
        logParam.setStatus(LocalDeliveryStatusEnum.CANCELED.getStatus().toString());
        logParam.setRemark(operateResult.getOperateDesc());
        coreDeliveryOrderLogService.addLog(logParam);
        return true;
    }

    @Override
    @Transactional
    public Boolean finish(EventShopDeliveryOrderParam param, String deliveryNo) {
        DeliveryLocalDeliveryOrder localDeliveryOrder = deliveryLocalDeliveryOrderMapper.selectOne(new QueryWrapper<DeliveryLocalDeliveryOrder>()
                .eq("delivery_no", deliveryNo).last("limit 1"));
        if (localDeliveryOrder == null) throw new CommonException("DELIVERY_ORDER_NOT_EXIT");
        if(LocalDeliveryStatusEnum.COMPLETED.getStatus().toString().equals(localDeliveryOrder.getStatus())){
            return true;
        }

        DeliveryLocalDeliveryOrder update = new DeliveryLocalDeliveryOrder();
        update.setId(localDeliveryOrder.getId());
        update.setStatus(LocalDeliveryStatusEnum.COMPLETED.getStatus().toString());
        update.setOutStatus(param.getStatus());
        update.setFinishTime(System.currentTimeMillis() / 1000);
        deliveryLocalDeliveryOrderMapper.updateById(update);

        LocalDeliveryEvent event = new LocalDeliveryEvent();
        event.setEvent("finish");
        event.setTradeType(localDeliveryOrder.getTradeType());
        event.setTradeNo(localDeliveryOrder.getTradeNo());
        EventAndSubscribeOfPublisher.publishAll(event);

        //添加配送订单日志
        LocalDeliveryOperateEnum.OperateResult operateResult = LocalDeliveryOperateEnum.getOperateByStatus(LocalDeliveryStatusEnum.COMPLETED.getStatus());
        AddLocalDeliveryOrderLogParam logParam = new AddLocalDeliveryOrderLogParam();
        logParam.setOrderId(localDeliveryOrder.getId());
        logParam.setMainId(param.getMainId());
        logParam.setMainName(param.getMainName());
        logParam.setMainType(LocalDeliveryMainTypeEnum.SYSTEM.getType());
        logParam.setOperate(operateResult.getOperate());
        logParam.setOperateDesc(operateResult.getOperateDesc());
        logParam.setStatus(LocalDeliveryStatusEnum.COMPLETED.getStatus().toString());
        logParam.setRemark(operateResult.getOperateDesc());
        coreDeliveryOrderLogService.addLog(logParam);
        return true;
    }
}
