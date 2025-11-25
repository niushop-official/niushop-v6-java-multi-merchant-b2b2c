package com.niu.core.service.core.delivery_store.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.LocalDeliveryLoader;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryService;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.delivery.DeliveryStoreStatusEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryServiceMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.core.delivery_store.ICoreDeliveryStoreService;
import com.niu.core.service.core.delivery_store.param.DeliveryShopOpenParam;
import com.niu.core.service.core.delivery_store.param.DeliveryStoreShopEditParam;
import com.niu.core.service.core.delivery_store.vo.DeliveryStoreListVo;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 物流查询服务层
 */
@Service
public class CoreDeliveryStoreServiceImpl implements ICoreDeliveryStoreService {

    @Resource
    private SiteAddressMapper siteAddressMapper;
    @Resource
    private DeliveryLocalDeliveryServiceMapper deliveryLocalDeliveryServiceMapper;
    @Resource
    private ICoreLocalDeliveryService coreLocalDeliveryService;

    @Override
    public List<DeliveryStoreListVo> deliveryServiceList(Integer id) {
        SiteAddress info = siteAddressMapper.selectById(id);
        if (info == null) {
            throw new RuntimeException("地址库不存在");
        }
        Map<String, Map<String, Object>> list = jsonToNestedMap(LocalDeliveryTypeEnum.getType());
        QueryWrapper<DeliveryLocalDeliveryService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", info.getSiteId())
                .in("`key`", list.keySet())
                .eq("is_use", 1);
        List<DeliveryLocalDeliveryService> enabledServices = deliveryLocalDeliveryServiceMapper.selectList(queryWrapper);
        Set<String> keys = enabledServices.stream()
                .map(DeliveryLocalDeliveryService::getKey)
                .collect(Collectors.toSet());
        Map<String, Object> supportDeliveryArray = new HashMap<>();
        if (info.getSupportDelivery() != null && !info.getSupportDelivery().trim().isEmpty()) {
            try {
                supportDeliveryArray = JSONUtil.toBean(info.getSupportDelivery(), Map.class);
            } catch (Exception e) {
                supportDeliveryArray = new HashMap<>();
            }
        }
        List<DeliveryStoreListVo> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : list.entrySet()) {
            String k = entry.getKey();
            Map<String, Object> v = entry.getValue();
            DeliveryStoreListVo vo = new DeliveryStoreListVo();
            vo.setKey(k);
            vo.setName((String) v.get("name"));
            vo.setIsMerchant((Integer) v.get("is_merchant") == 1);
            if ((Integer) v.get("is_merchant") == 1) {
                vo.setOpenStatus(DeliveryStoreStatusEnum.OPEN_PASS.getStatus());
                vo.setBusiness("");
                vo.setEditStatus("");
                vo.setReason("");
            } else {
                if (keys.contains(k)) {
                    Object supportInfoObj = supportDeliveryArray.get(k);
                    Map<String, Object> supportInfo = new HashMap<>();
                    if (supportInfoObj instanceof Map) {
                        supportInfo = (Map<String, Object>) supportInfoObj;
                    }
                    vo.setOpenStatus(supportInfo.get("open_status") != null ? (Integer) supportInfo.get("open_status") : null);
                    vo.setBusiness(supportInfo.get("business") != null ? (String) supportInfo.get("business") : "");
                    vo.setEditStatus(supportInfo.get("edit_status") != null ? (String) supportInfo.get("edit_status") : "");
                    vo.setReason(supportInfo.get("reason") != null ? (String) supportInfo.get("reason") : "");
                } else {
                    continue;
                }
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public void deliveryShopOpen(Integer id,DeliveryShopOpenParam param) {
        SiteAddress info = siteAddressMapper.selectById(id);
        if (info == null) {
            throw new RuntimeException("地址库不存在");
        }
        String deliveryType = param.getDeliveryType();
        Map<String, Object> localDeliveryConfig = coreLocalDeliveryService.getConfig(deliveryType);
        if (localDeliveryConfig.isEmpty()) {
            String typeName = LocalDeliveryTypeEnum.getName(deliveryType) != null ? LocalDeliveryTypeEnum.getName(deliveryType) : deliveryType;
            throw new RuntimeException(typeName + "未配置");
        }

        String shopNo = CommonUtils.createNo();
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("delivery_type", deliveryType);
        requestData.put("business", param.getBusiness());
        requestData.put("site_address", info);
        requestData.put("shop_no", shopNo);
        Map<String, Object> config = new HashMap<>();
        config.put("siteId", info.getSiteId());
        //第三方配送下单 驱动
        BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(deliveryType).init(JSONUtil.parseObj(config));
        Map<String, Object> responseData = driver.addShop(requestData);

        if (!responseData.isEmpty()) {
            Map<String, Object> supportDelivery = (Map<String, Object>) responseData.get("support_delivery");
            if (supportDelivery != null) {
                Map<String, Object> currentSupportDelivery = new HashMap<>();
                if (info.getSupportDelivery() != null && !info.getSupportDelivery().isEmpty()) {
                    try {
                        currentSupportDelivery = JSON.parseObject(info.getSupportDelivery(), Map.class);
                    } catch (Exception e) {

                        currentSupportDelivery = new HashMap<>();
                    }
                }
                currentSupportDelivery.putAll(supportDelivery);

                info.setSupportDelivery(JSON.toJSONString(currentSupportDelivery));
            }

            if (param.getExtendData() != null) {
                Map<String, Object> currentExtendData = new HashMap<>();
                if (info.getExtendData() != null && !info.getExtendData().isEmpty()) {
                    try {
                        currentExtendData = JSON.parseObject(info.getExtendData(), Map.class);
                    } catch (Exception e) {
                        // 如果解析失败，使用空Map
                        currentExtendData = new HashMap<>();
                    }
                }
                currentExtendData.putAll(param.getExtendData());
                info.setExtendData(JSON.toJSONString(currentExtendData));
            }

            info.setShopNo(shopNo);

            siteAddressMapper.updateById(info);
        }
    }

    @Override
    public void deliveryShopEdit(Integer id, DeliveryStoreShopEditParam param) {
        SiteAddress info = siteAddressMapper.selectById(id);
        if (info == null) {
            throw new RuntimeException("地址库不存在");
        }

        String deliveryType = param.getDeliveryType();

        Map<String, Object> localDeliveryConfig = coreLocalDeliveryService.getConfig(deliveryType);
        if (localDeliveryConfig.isEmpty()) {
            String typeName = LocalDeliveryTypeEnum.getName(deliveryType) != null ? LocalDeliveryTypeEnum.getName(deliveryType) : deliveryType;
            throw new RuntimeException(typeName + "未配置");
        }

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("delivery_type", deliveryType);
        requestData.put("business", param.getBusiness());
        requestData.put("site_address", info);
        Map<String, Object> config = new HashMap<>();
        config.put("siteId", info.getSiteId());
        //第三方配送下单 驱动
        BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(deliveryType).init(JSONUtil.parseObj(config));
        Map<String, Object> responseData = driver.addShop(requestData);

        if (!responseData.isEmpty()) {
            Map<String, Object> supportDelivery = (Map<String, Object>) responseData.get("support_delivery");
            if (supportDelivery != null) {
                info.setSupportDelivery(JSON.toJSONString(supportDelivery));
            }

            siteAddressMapper.updateById(info);
        }
    }

    public static Map<String, Map<String, Object>> jsonToNestedMap(JSONObject json) {
        Map<String, Map<String, Object>> resultMap = new HashMap<>();

        for (String key : json.keySet()) {
            Object value = json.get(key);

            if (value instanceof JSONObject nestedJson) {
                // 递归处理嵌套的JSONObject
                Map<String, Object> nestedMap = new HashMap<>();

                for (String nestedKey : nestedJson.keySet()) {
                    nestedMap.put(nestedKey, nestedJson.get(nestedKey));
                }
                resultMap.put(key, nestedMap);
            }
        }
        return resultMap;
    }
}
