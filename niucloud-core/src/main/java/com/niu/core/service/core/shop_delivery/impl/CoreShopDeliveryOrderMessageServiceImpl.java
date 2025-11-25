package com.niu.core.service.core.shop_delivery.impl;

import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.shop_delivery.ShopDeliveryStatusEnum;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderEventService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderMessageService;
import com.niu.core.service.core.shop_delivery.param.EventShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class CoreShopDeliveryOrderMessageServiceImpl implements ICoreShopDeliveryOrderMessageService {

    @Resource
    private ICoreShopDeliveryOrderEventService coreShopDeliveryOrderEventService;

    @Override
    public void orderNotify(NotifyShopDeliveryOrderParam param) {
        // 同城配送订单号
        String deliveryNo = param.getOutDeliveryNo();
        Integer status = ShopDeliveryStatusEnum.convertStatus(Integer.parseInt(param.getStatus()));
        EventShopDeliveryOrderParam paramObj = new EventShopDeliveryOrderParam();
        BeanUtils.copyProperties(param, paramObj);
        // 同城配送订单状态转换
        if(LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().equals(status)){
            coreShopDeliveryOrderEventService.riderPickedUp(paramObj,deliveryNo);
        }else if(LocalDeliveryStatusEnum.CANCELED.getStatus().equals(status)){
            coreShopDeliveryOrderEventService.cancel(paramObj,deliveryNo);
        }else if(LocalDeliveryStatusEnum.COMPLETED.getStatus().equals(status)){
            coreShopDeliveryOrderEventService.finish(paramObj,deliveryNo);
        }
    }
}
