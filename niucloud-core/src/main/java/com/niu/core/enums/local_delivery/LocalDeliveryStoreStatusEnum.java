package com.niu.core.enums.local_delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 同城配送店铺状态枚举类
 */
@Getter
@AllArgsConstructor
public enum LocalDeliveryStoreStatusEnum {

    OPEN_PASS(1, "通过"),
    OPEN_REFUND(2, "未通过"),
    EDIT_PASS(1, "编辑通过"),
    EDIT_REFUND(2, "编辑未通过");

    private final Integer status;
    private final String name;

    /**
     * 获取所有配送状态Map集合
     * @return Map<Integer, String>
     */
    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for (LocalDeliveryStoreStatusEnum item : LocalDeliveryStoreStatusEnum.values()) {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    /**
     * 根据status获取配送状态名称
     * @param status 配送状态值
     * @return 配送状态名称
     */
    public static String getNameByStatus(Integer status) {
        for (LocalDeliveryStoreStatusEnum item : LocalDeliveryStoreStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item.getName();
            }
        }
        return "";
    }

    /**
     * 根据status获取枚举对象
     * @param status 配送状态值
     * @return LocalDeliveryStatusEnum
     */
    public static LocalDeliveryStoreStatusEnum getByStatus(Integer status) {
        for (LocalDeliveryStoreStatusEnum item : LocalDeliveryStoreStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }


}