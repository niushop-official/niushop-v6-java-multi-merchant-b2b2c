package com.niu.core.enums.delivery_package;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 配送包裹状态枚举类
 */
@Getter
@AllArgsConstructor
public enum PackageStatusEnum {
    
    WAIT_DELIVERY("wait_delivery", "待配送"),
    DELIVERYING("deliverying", "配送中"),
    FINISH("finish", "配送完成"),
    CANCEL("cancel", "已作废");

    private final String status;
    private final String name;

    /**
     * 获取所有状态Map集合
     * @return Map<String, String>
     */
    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (PackageStatusEnum item : PackageStatusEnum.values()) {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    /**
     * 根据status获取状态名称
     * @param status 状态值
     * @return 状态名称
     */
    public static String getNameByStatus(String status) {
        for (PackageStatusEnum item : PackageStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item.getName();
            }
        }
        return "";
    }

    /**
     * 根据status获取枚举对象
     * @param status 状态值
     * @return PackageStatusEnum
     */
    public static PackageStatusEnum getByStatus(String status) {
        for (PackageStatusEnum item : PackageStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }
}