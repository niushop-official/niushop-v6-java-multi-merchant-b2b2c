package com.niu.core.enums.local_delivery;

import com.niu.core.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 同城配送店铺状态枚举类
 */
@Getter
@AllArgsConstructor
public enum LocalDeliveryStoreTypeEnum {

    LOCAL_DELIVERY("local_delivery", "同城配送"),
    STORE("store", "门店自提");

    private final String type;
    private final String name;

    /**
     * 获取所有配送状态Map集合
     * @return Map<Integer, String>
     */
    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (LocalDeliveryStoreTypeEnum item : LocalDeliveryStoreTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 获取提货类型
     * @return Map<Integer, String>
     */
    public static String getPickUpType(String type) {
        Map<String, String> map = getMap();
        return StringUtils.isNotEmpty(map.get(type)) ? map.get(type) : "";
    }
}