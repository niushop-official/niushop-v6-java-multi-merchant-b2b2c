package com.niu.core.service.core.shop_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.local_delivery.param.CloseShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.FinishShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrder;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryMainTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryOperateEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.shop_delivery.ShopDeliveryStatusEnum;
import com.niu.core.mapper.delivery.DeliveryShopDeliveryOrderMapper;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderNotifyService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderCloseService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderFinishService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderLogService;
import com.niu.core.service.core.shop_delivery.param.AddShopDeliveryOrderLogParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreShopDeliveryOrderFinishServiceImpl implements ICoreShopDeliveryOrderFinishService {


    @Resource
    private DeliveryShopDeliveryOrderMapper deliveryShopDeliveryOrderMapper;
    @Resource
    private ICoreShopDeliveryOrderLogService coreShopDeliveryOrderLogService;
    @Resource
    private ICoreLocalDeliveryOrderNotifyService coreLocalDeliveryOrderNotifyService;

    @Override
    public DeliveryShopDeliveryOrder finishOrder(FinishShopDeliveryOrderParam param) {
        if (ObjectUtil.isEmpty(param.getId()) && ObjectUtil.isEmpty(param.getOutDeliveryNo())){
            throw new CommonException("DELIVERY_ORDER_NOT_EXIT");
        }
        QueryWrapper<DeliveryShopDeliveryOrder> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(param.getId())){
            queryWrapper.eq("id", param.getId());
        }else {
            queryWrapper.eq("out_delivery_no", param.getOutDeliveryNo());
        }
        queryWrapper.last("limit 1");
        DeliveryShopDeliveryOrder deliveryShopDeliveryOrder = deliveryShopDeliveryOrderMapper.selectOne(queryWrapper);
        if(ObjectUtil.isEmpty(deliveryShopDeliveryOrder)){
            throw new CommonException("DELIVERY_ORDER_NOT_EXIT");
        }
        deliveryShopDeliveryOrder.setStatus(LocalDeliveryStatusEnum.COMPLETED.getStatus().toString());
        deliveryShopDeliveryOrder.setFinishTime(System.currentTimeMillis()/1000);
        int update = deliveryShopDeliveryOrderMapper.updateById(deliveryShopDeliveryOrder);
        if(update>0){
            //订单状态回调
            NotifyShopDeliveryOrderParam notifyParam = new NotifyShopDeliveryOrderParam();
            notifyParam.setOutDeliveryNo(deliveryShopDeliveryOrder.getOutDeliveryNo());
            notifyParam.setMainId(deliveryShopDeliveryOrder.getDeliverId());
            notifyParam.setMainName(deliveryShopDeliveryOrder.getDeliverName());
            notifyParam.setStatus(LocalDeliveryStatusEnum.COMPLETED.getStatus().toString());
            coreLocalDeliveryOrderNotifyService.notify(deliveryShopDeliveryOrder.getSiteId(), DeliveryStoreTypeEnum.MERCHANT.getKey(), "order", notifyParam);
            //添加配送订单日志
            LocalDeliveryOperateEnum.OperateResult operateResult = LocalDeliveryOperateEnum.getOperateByStatus(ShopDeliveryStatusEnum.convertStatus(ShopDeliveryStatusEnum.COMPLETED.getStatus()));
            AddShopDeliveryOrderLogParam logParam = new AddShopDeliveryOrderLogParam();
            logParam.setOrderId(deliveryShopDeliveryOrder.getId());
            logParam.setMainId(param.getMainId());
            logParam.setMainName(param.getMainName());
            logParam.setMainType(LocalDeliveryMainTypeEnum.STORE.getType());
            logParam.setOperate(operateResult.getOperate());
            logParam.setOperateDesc(operateResult.getOperateDesc());
            logParam.setStatus(LocalDeliveryStatusEnum.COMPLETED.getStatus().toString());
            logParam.setRemark(operateResult.getOperateDesc());
            coreShopDeliveryOrderLogService.addLog(logParam);
        }
        return null;
    }
}
