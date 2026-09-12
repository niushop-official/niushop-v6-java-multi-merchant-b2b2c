package com.niu.core.service.core.local_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryService;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
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
        if (DeliveryStoreTypeEnum.MERCHANT.getKey().equals(deliveryType)){
            return new HashMap<>();
        }
        QueryWrapper<DeliveryLocalDeliveryService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("`key`", deliveryType);
        queryWrapper.eq("site_id", siteId);
        queryWrapper.last("limit 1");
        DeliveryLocalDeliveryService service = deliveryLocalDeliveryServiceMapper.selectOne(queryWrapper);
        if(ObjectUtil.isEmpty(service) || (ObjectUtil.isNotEmpty(service) && StringUtils.isEmpty(service.getConfig()))){
            String typeName = LocalDeliveryTypeEnum.getName(deliveryType) != null ? LocalDeliveryTypeEnum.getName(deliveryType) : deliveryType;
            throw new RuntimeException(typeName + "未配置");
        }
        Map<String, Object> config = JSONUtil.parseObj(service.getConfig());
        config.put("is_use", service.getIsUse());
        return config;
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
