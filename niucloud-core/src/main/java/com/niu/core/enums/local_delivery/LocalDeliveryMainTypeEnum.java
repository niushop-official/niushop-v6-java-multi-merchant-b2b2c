package com.niu.core.enums.local_delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 同城配送操作人类型枚举类
 */
@Getter
@AllArgsConstructor
public enum LocalDeliveryMainTypeEnum {
    
    STORE("store", "商家"),
    RIDER("rider", "骑手"),
    MEMBER("member", "会员"),
    SYSTEM("system", "系统");

    private final String type;
    private final String name;

    /**
     * 获取所有操作人类型Map集合
     * @return Map<String, String>
     */
    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (LocalDeliveryMainTypeEnum item : LocalDeliveryMainTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 根据type获取操作人类型名称
     * @param type 操作人类型
     * @return 操作人类型名称
     */
    public static String getNameByType(String type) {
        for (LocalDeliveryMainTypeEnum item : LocalDeliveryMainTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    /**
     * 根据type获取枚举对象
     * @param type 操作人类型
     * @return LocalDeliveryMainTypeEnum
     */
    public static LocalDeliveryMainTypeEnum getByType(String type) {
        for (LocalDeliveryMainTypeEnum item : LocalDeliveryMainTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 检查操作人类型是否有效
     * @param type 操作人类型
     * @return boolean
     */
    public static boolean isValidType(String type) {
        return getByType(type) != null;
    }

    /**
     * 判断是否为系统操作
     * @param type 操作人类型
     * @return boolean
     */
    public static boolean isSystemType(String type) {
        return SYSTEM.getType().equals(type);
    }

    /**
     * 判断是否为人工操作
     * @param type 操作人类型
     * @return boolean
     */
    public static boolean isManualType(String type) {
        return STORE.getType().equals(type) || 
               RIDER.getType().equals(type) || 
               MEMBER.getType().equals(type);
    }
}