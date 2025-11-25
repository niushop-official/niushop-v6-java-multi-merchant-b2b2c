package com.niu.core.enums.delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单配送状态枚举类
 */
@Getter
@AllArgsConstructor
public enum OrderDeliveryStatusEnum {
    
    WAIT_DELIVERY("wait_delivery", "待发货"),
    DELIVERYING("delivery", "配送中"),
    DELIVERY_FINISH("delivery_finish", "配送完成"),
    TAKED("taked", "已收货"),
    EXPIRE("expire", "失效");

    private final String status;
    private final String name;

    /**
     * 获取所有配送状态Map集合
     * @return Map<String, String>
     */
    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (OrderDeliveryStatusEnum item : OrderDeliveryStatusEnum.values()) {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    /**
     * 根据status获取配送状态名称
     * @param status 配送状态
     * @return 配送状态名称
     */
    public static String getNameByStatus(String status) {
        for (OrderDeliveryStatusEnum item : OrderDeliveryStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item.getName();
            }
        }
        return "";
    }

    /**
     * 根据status获取枚举对象
     * @param status 配送状态
     * @return OrderDeliveryStatusEnum
     */
    public static OrderDeliveryStatusEnum getByStatus(String status) {
        for (OrderDeliveryStatusEnum item : OrderDeliveryStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 检查状态是否有效
     * @param status 配送状态
     * @return boolean
     */
    public static boolean isValidStatus(String status) {
        return getByStatus(status) != null;
    }

    /**
     * 获取有效配送状态Map集合（排除失效状态）
     * @return Map<String, String>
     */
    public static Map<String, String> getValidStatusMap() {
        Map<String, String> map = new HashMap<>();
        for (OrderDeliveryStatusEnum item : OrderDeliveryStatusEnum.values()) {
            if (!EXPIRE.equals(item)) {
                map.put(item.getStatus(), item.getName());
            }
        }
        return map;
    }
}