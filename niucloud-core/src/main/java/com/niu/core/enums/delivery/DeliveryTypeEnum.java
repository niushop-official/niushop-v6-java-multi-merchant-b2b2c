package com.niu.core.enums.delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 配送方式枚举类
 */
@Getter
@AllArgsConstructor
public enum DeliveryTypeEnum {
    
    EXPRESS("express", "物流配送"),
    LOCAL_DELIVERY("local_delivery", "同城配送"),
    STORE("store", "门店自提");

    private final String type;
    private final String name;

    /**
     * 获取配送方式Map集合
     * @return Map<String, String>
     */
    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (DeliveryTypeEnum item : DeliveryTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 根据type获取配送方式名称
     * @param type 配送方式类型
     * @return 配送方式名称
     */
    public static String getNameByType(String type) {
        for (DeliveryTypeEnum item : DeliveryTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    /**
     * 根据type获取枚举对象
     * @param type 配送方式类型
     * @return DeliveryTypeEnum
     */
    public static DeliveryTypeEnum getByType(String type) {
        for (DeliveryTypeEnum item : DeliveryTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}