package com.niu.core.service.core.local_delivery.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryService;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryServiceMapper;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 同城配送核心服务实现
 */
@Service
public class CoreLocalDeliveryServiceImpl implements ICoreLocalDeliveryService {

    @Resource
    private DeliveryLocalDeliveryServiceMapper deliveryLocalDeliveryServiceMapper;

    @Override
    public Map<String, Object> getConfig(String deliveryType) {
        QueryWrapper<DeliveryLocalDeliveryService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("`key`", deliveryType);
        
        DeliveryLocalDeliveryService service = deliveryLocalDeliveryServiceMapper.selectOne(queryWrapper);
        
        if (service == null) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", service.getId());
        result.put("site_id", service.getSiteId());
        result.put("key", service.getKey());
        result.put("name", service.getName());
        result.put("config", service.getConfig());
        result.put("is_merchant", service.getIsMerchant());
        result.put("is_use", service.getIsUse());
        result.put("is_open", service.getIsOpen());
        
        return result;
    }
}
