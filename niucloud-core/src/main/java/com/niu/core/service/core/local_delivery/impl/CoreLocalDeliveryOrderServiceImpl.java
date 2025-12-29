package com.niu.core.service.core.local_delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.LocalDeliveryLoader;
import com.niu.core.common.loader.local_delivery.param.CloseShopDeliveryOrderParam;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrder;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryOrderCancelReasonEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderCloseService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryService;
import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCloseParam;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 同城配送事件服务实现
 */
@Service
public class CoreLocalDeliveryOrderServiceImpl implements ICoreLocalDeliveryOrderService {

    @Resource
    private DeliveryLocalDeliveryOrderMapper deliveryLocalDeliveryOrderMapper;
    @Resource
    private ICoreLocalDeliveryService coreLocalDeliveryService;

    @Override
    @Transactional
    public Integer syncOrder(Integer id) {
        DeliveryLocalDeliveryOrder info = deliveryLocalDeliveryOrderMapper.selectById(id);
        if (ObjectUtil.isEmpty( info)){
            throw new CommonException("DELIVERY_ORDER_NOT_EXIT");
        }
        Map<String, Object> localDeliveryConfig = new HashMap<>();
        if(!DeliveryStoreTypeEnum.MERCHANT.getKey().equals(info.getDeliveryService())){
            localDeliveryConfig = coreLocalDeliveryService.getConfig(info.getDeliveryService(),info.getSiteId());
            String deliveryServiceName = LocalDeliveryTypeEnum.getName(info.getDeliveryService());
            if (localDeliveryConfig.isEmpty() || !localDeliveryConfig.containsKey("config"))   throw new RuntimeException(deliveryServiceName + "未配置");
            localDeliveryConfig = JSONUtil.parseObj(localDeliveryConfig.get("config"));
        }

        localDeliveryConfig.put("siteId", info.getSiteId());
        try {
            //第三方配送下单 驱动
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(info.getDeliveryService()).init(JSONUtil.parseObj(localDeliveryConfig));
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("delivery_no", info.getDeliveryNo());
            Map<String, Object> saveData = driver.queryOrderInfo(requestData);
            if (ObjectUtil.isEmpty( saveData)){
                return 0;
            }else {
                DeliveryLocalDeliveryOrder updateData = BeanUtil.mapToBean(saveData, DeliveryLocalDeliveryOrder.class,true,null);
                updateData.setId(info.getId());
                return deliveryLocalDeliveryOrderMapper.updateById(updateData);
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }
}
