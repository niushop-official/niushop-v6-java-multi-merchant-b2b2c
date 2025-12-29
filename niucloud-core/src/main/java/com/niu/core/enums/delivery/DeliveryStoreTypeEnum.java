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

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (DeliveryStoreTypeEnum item : DeliveryStoreTypeEnum.values()) {
            map.put(item.getKey(), item.getName());
        }
        return map;
    }

    public static String getNameByKey(String key) {
        for (DeliveryStoreTypeEnum item : DeliveryStoreTypeEnum.values()) {
            if (item.getKey().equals(key)) {
                return item.getName();
            }
        }
        return "";
    }
}
