package com.niu.core.service.core.local_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryService;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
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
    @Resource
    Cached cached;
    @Override
    public Map<String, Object> getConfig(String deliveryType,Integer siteId) {

        QueryWrapper<DeliveryLocalDeliveryService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("`key`", deliveryType);
        queryWrapper.eq("site_id", siteId);
        queryWrapper.last("limit 1");
        DeliveryLocalDeliveryService service = deliveryLocalDeliveryServiceMapper.selectOne(queryWrapper);
        if(ObjectUtil.isEmpty(service)){
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

    @Override
    public String buildNotifyUrl(String siteId, String type,String orderNo, String action) {
        if(StringUtils.isEmpty(action)){
            action = "order";
        }
        String[] param = {siteId,type, orderNo, action};
        String baseUrl = "";
        if (RequestUtils.handler() != null) {
            baseUrl = RequestUtils.getDomain(true);
            cached.put("deliveryBaseurl", baseUrl);
        } else if (cached.get("deliveryBaseurl") != null) {
            baseUrl = (String) cached.get("deliveryBaseurl");
        } else {
            throw new CommonException("build notify url error");
        }
        return baseUrl + "/api/delivery/notify/" + String.join("/", param);
    }
}
