package com.niu.core.enums.local_delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 同城配送状态枚举类
 */
@Getter
@AllArgsConstructor
public enum LocalDeliveryStatusEnum {
    
    WAIT_RELEASE(-100, "预约待发布"),
    ORDER_ACCEPTED(0,"商家已接单"),
    PENDING_ACCEPTANCE(1, "待接单"),
    PENDING_PICKUP(2, "待取货"),
    RIDER_ARRIVED(3, "骑士到店"),
    IN_DELIVERY(4, "配送中"),
    COMPLETED(5, "已完成"),
    DISPATCH_ORDER(6, "指派单"),
    RETURN_IN_PROGRESS(7, "妥投异常之物品返回中"),
    RETURN_COMPLETED(8, "妥投异常之物品返回完成"),
    RETURN_RIDER_ARRIVED(9, "售后取件单送达门店"),
    CREATE_ORDER_FAILED(-1, "创建运单失败"),
    CANCELED(-2, "已取消");

    private final Integer status;
    private final String name;

    /**
     * 获取所有配送状态Map集合
     * @return Map<Integer, String>
     */
    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for (LocalDeliveryStatusEnum item : LocalDeliveryStatusEnum.values()) {
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
        for (LocalDeliveryStatusEnum item : LocalDeliveryStatusEnum.values()) {
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
    public static LocalDeliveryStatusEnum getByStatus(Integer status) {
        for (LocalDeliveryStatusEnum item : LocalDeliveryStatusEnum.values()) {
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
        for (LocalDeliveryStatusEnum item : LocalDeliveryStatusEnum.values()) {
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
        for (LocalDeliveryStatusEnum item : LocalDeliveryStatusEnum.values()) {
            if (item.getStatus() < 0) {
                map.put(item.getStatus(), item.getName());
            }
        }
        return map;
    }

    /**
     * 判断是否为完成状态
     * @param status 配送状态值
     * @return boolean
     */
    public static boolean isCompletedStatus(Integer status) {
        return COMPLETED.getStatus().equals(status) || 
               RETURN_COMPLETED.getStatus().equals(status);
    }

    /**
     * 判断是否为进行中状态
     * @param status 配送状态值
     * @return boolean
     */
    public static boolean isInProgressStatus(Integer status) {
        return PENDING_ACCEPTANCE.getStatus().equals(status) ||
               PENDING_PICKUP.getStatus().equals(status) ||
               RIDER_ARRIVED.getStatus().equals(status) ||
               IN_DELIVERY.getStatus().equals(status) ||
               DISPATCH_ORDER.getStatus().equals(status) ||
               RETURN_IN_PROGRESS.getStatus().equals(status);
    }

    /**
     * 判断是否为失败状态
     * @param status 配送状态值
     * @return boolean
     */
    public static boolean isFailedStatus(Integer status) {
        return CREATE_ORDER_FAILED.getStatus().equals(status) ||
               CANCELED.getStatus().equals(status);
    }
}