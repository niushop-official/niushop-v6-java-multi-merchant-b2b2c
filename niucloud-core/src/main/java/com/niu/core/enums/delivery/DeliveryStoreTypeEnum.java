package com.niu.core.enums.delivery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum DeliveryStoreTypeEnum {
    MERCHANT("merchant", "商家配送"),
    DADA("b2b2c_dada", "dada配送");
    private final String key;
    private final String name;

    DeliveryStoreTypeEnum(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    // 根据key获取枚举值
    public static DeliveryStoreTypeEnum fromKey(String key) {
        for (DeliveryStoreTypeEnum type : DeliveryStoreTypeEnum.values()) {
            if (type.key.equals(key)) {
                return type;
            }
        }
        return null;
    }

    // 获取配送类型详情
    public Map<String, Object> getDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("name", this.name);
        details.put("key", this.key);
        details.put("params", new ArrayList<>());
        details.put("component", "");
        details.put("business_list", "");
        details.put("extend_data", new HashMap<>());
        details.put("is_merchant", 1);
        return details;
    }

    // 获取所有配送类型列表
    public static Map<String, Map<String, Object>> getAllTypes() {
        Map<String, Map<String, Object>> list = new HashMap<>();
        for (DeliveryStoreTypeEnum type : DeliveryStoreTypeEnum.values()) {
            list.put(type.key, type.getDetails());
        }

        // 合并扩展配送类型（模拟event('LocalDeliveryType')）
        // Map<String, Map<String, Object>> extend = eventLocalDeliveryType();
        // return mergeMaps(list, extend);

        return list;
    }
}
