package com.niu.core.enums.shop_delivery;

import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家配送状态枚举类
 */
@Getter
@AllArgsConstructor
public enum ShopDeliveryStatusEnum {

    IN_DELIVERY(4, "配送中"),
    COMPLETED(5, "已完成"),
    CANCELED(-2, "已取消");

    private final Integer status;
    private final String name;

    /**
     * 获取所有配送状态Map集合
     * @return Map<Integer, String>
     */
    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for (ShopDeliveryStatusEnum item : ShopDeliveryStatusEnum.values()) {
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
        for (ShopDeliveryStatusEnum item : ShopDeliveryStatusEnum.values()) {
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
    public static ShopDeliveryStatusEnum getByStatus(Integer status) {
        for (ShopDeliveryStatusEnum item : ShopDeliveryStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 检查状态是否有效
     * @param status 配送状态值
     * @return boolean
     */
    public static boolean isValidStatus(Integer status) {
        return getByStatus(status) != null;
    }

    /**
     * 获取正常状态Map集合（排除失败和取消状态）
     * @return Map<Integer, String>
     */
    public static Map<Integer, String> getNormalStatusMap() {
        Map<Integer, String> map = new HashMap<>();
        for (ShopDeliveryStatusEnum item : ShopDeliveryStatusEnum.values()) {
            if (item.getStatus() >= 0) {
                map.put(item.getStatus(), item.getName());
            }
        }
        return map;
    }

    /**
     * 获取异常状态Map集合（失败和取消状态）
     * @return Map<Integer, String>
     */
    public static Map<Integer, String> getAbnormalStatusMap() {
        Map<Integer, String> map = new HashMap<>();
        for (ShopDeliveryStatusEnum item : ShopDeliveryStatusEnum.values()) {
            if (item.getStatus() < 0) {
                map.put(item.getStatus(), item.getName());
            }
        }
        return map;
    }


    /**
     * 转换配送状态
     * @return Map<Integer, String>
     */
    public static Integer convertStatus(Integer status) {
        if(ShopDeliveryStatusEnum.IN_DELIVERY.status.equals( status)){
            return LocalDeliveryStatusEnum.IN_DELIVERY.getStatus();
        }else if(ShopDeliveryStatusEnum.COMPLETED.status.equals( status)){
            return LocalDeliveryStatusEnum.COMPLETED.getStatus();
        }else if(ShopDeliveryStatusEnum.CANCELED.status.equals( status)){
            return LocalDeliveryStatusEnum.CANCELED.getStatus();
        }
        return 0;
    }
}