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
public enum DeliveryStoreStatusEnum {

    OPEN_PASS(1, "通过"),
    OPEN_REFUND(2, "未通过");
    private final Integer status;
    private final String name;

    /**
     * 获取所有配送状态Map集合
     * @return Map<String, String>
     */
    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for (DeliveryStoreStatusEnum item : DeliveryStoreStatusEnum.values()) {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    /**
     * 根据status获取配送状态名称
     * @param status 配送状态
     * @return 配送状态名称
     */
    public static String getNameByStatus(Integer status) {
        for (DeliveryStoreStatusEnum item : DeliveryStoreStatusEnum.values()) {
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
    public static DeliveryStoreStatusEnum getByStatus(Integer status) {
        for (DeliveryStoreStatusEnum item : DeliveryStoreStatusEnum.values()) {
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
    public static boolean isValidStatus(Integer status) {
        return getByStatus(status) != null;
    }

}