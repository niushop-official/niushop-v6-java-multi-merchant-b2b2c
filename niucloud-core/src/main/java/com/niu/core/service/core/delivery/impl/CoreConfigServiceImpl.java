package com.niu.core.service.core.delivery.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.delivery.ICoreConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品分类实现
 */
@Service
public class CoreConfigServiceImpl implements ICoreConfigService {
    /**
     * 获取配送方式
     */
    @Resource
    private com.niu.core.service.core.sys.ICoreConfigService coreConfigService;
    @Override
    public Map<String, Object> getDeliveryList() {

        JSONObject value = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "DELIVERY_CONFIG");

        Map<String, Object> list = new HashMap<>();

        Map<String, Object> express = new HashMap<>();
        express.put("name", "物流配送");
        express.put("status", 1);  // 默认启用，与PHP一致
        express.put("key", "express");
        express.put("isKey", "isExpress");
        list.put("express", express);

        Map<String, Object> localDelivery = new HashMap<>();
        localDelivery.put("name", "同城配送");
        localDelivery.put("status", 0); 
        localDelivery.put("key", "local_delivery");
        localDelivery.put("isKey", "isLocalDelivery");
        list.put("local_delivery", localDelivery);

        Map<String, Object> storeDelivery = new HashMap<>();
        storeDelivery.put("name", "门店自提");
        storeDelivery.put("status", 0);
        storeDelivery.put("key", "store");
        storeDelivery.put("isKey", "isStore");
        list.put("store", storeDelivery);

        if (value != null && !value.isEmpty()) {
            for (Map.Entry<String, Object> entry : list.entrySet()) {
                String key = entry.getKey();
                Map<String, Object> val = (Map<String, Object>) entry.getValue();
                String isKey = val.get("isKey").toString();
                Map<String, Object> updatedVal = new HashMap<>();
                updatedVal.put("name", val.get("name"));
                if(value.get(isKey) != null){
                    updatedVal.put("status",value.get(val.get("isKey").toString()));
                }else{
                    updatedVal.put("status", val.get("status"));
                }
                updatedVal.put("key", val.get("key"));

                list.put(key, updatedVal);
            }
        }
        return list;
    }
}
