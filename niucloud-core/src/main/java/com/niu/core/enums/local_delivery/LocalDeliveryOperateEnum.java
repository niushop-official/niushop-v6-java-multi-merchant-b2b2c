package com.niu.core.enums.local_delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 同城配送操作类型枚举类
 */
@Getter
@AllArgsConstructor
public enum LocalDeliveryOperateEnum {
    
    ORDER_ACCEPTED("order_accepted", "商家已接单"),
    ORDER_CALL("order_call", "商家呼叫配送"),
    DISPATCH_ORDER("dispatch_order", "商家指派骑手"),
    RIDER_ACCEPTED("rider_accepted", "骑手已接单"),
    RIDER_ARRIVED("rider_arrived", "骑手已到店"),
    RIDER_PICKED_UP("rider_picked_up", "骑手已取货"),
    ORDER_DELIVERED("order_delivered", "订单已送达"),
    ORDER_CANCELED("order_canceled", "订单已取消"),
    RETURN_APPLIED("return_applied", "物品返回申请"),
    RETURN_COMPLETED("return_completed", "物品返回完成");

    private final String operate;
    private final String operateDesc;

    /**
     * 操作信息结果类
     */
    @Getter
    @AllArgsConstructor
    public static class OperateResult {
        private String operate;
        private String operateDesc;
    }

    /**
     * 获取所有操作类型Map集合
     * @return Map<String, String>
     */
    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (LocalDeliveryOperateEnum item : LocalDeliveryOperateEnum.values()) {
            map.put(item.getOperate(), item.getOperateDesc());
        }
        return map;
    }

    /**
     * 根据operate获取操作描述
     * @param operate 操作类型
     * @return 操作描述
     */
    public static String getDescByOperate(String operate) {
        for (LocalDeliveryOperateEnum item : LocalDeliveryOperateEnum.values()) {
            if (item.getOperate().equals(operate)) {
                return item.getOperateDesc();
            }
        }
        return "";
    }

    /**
     * 根据配送状态获取对应的操作信息
     * @param status 配送状态值
     * @return OperateResult
     */
    public static OperateResult getOperateByStatus(Integer status) {
        if (status == null) {
            return new OperateResult("", "");
        }

        switch (status) {
            case 0: // LocalDeliveryStatusEnum.PENDING_ACCEPTANCE
                return new OperateResult(ORDER_ACCEPTED.getOperate(), ORDER_ACCEPTED.getOperateDesc());
            case 1: // LocalDeliveryStatusEnum.PENDING_ACCEPTANCE
                return new OperateResult(ORDER_CALL.getOperate(), ORDER_CALL.getOperateDesc());
            case 6: // LocalDeliveryStatusEnum.DISPATCH_ORDER
                return new OperateResult(DISPATCH_ORDER.getOperate(), DISPATCH_ORDER.getOperateDesc());
            case 2: // LocalDeliveryStatusEnum.PENDING_PICKUP
                return new OperateResult(RIDER_ACCEPTED.getOperate(), RIDER_ACCEPTED.getOperateDesc());
            case 3: // LocalDeliveryStatusEnum.RIDER_ARRIVED
                return new OperateResult(RIDER_ARRIVED.getOperate(), RIDER_ARRIVED.getOperateDesc());
            case 4: // LocalDeliveryStatusEnum.IN_DELIVERY
                return new OperateResult(RIDER_PICKED_UP.getOperate(), RIDER_PICKED_UP.getOperateDesc());
            case 5: // LocalDeliveryStatusEnum.COMPLETED
                return new OperateResult(ORDER_DELIVERED.getOperate(), ORDER_DELIVERED.getOperateDesc());
            case -2: // LocalDeliveryStatusEnum.CANCELED
                return new OperateResult(ORDER_CANCELED.getOperate(), ORDER_CANCELED.getOperateDesc());
            case 7: // LocalDeliveryStatusEnum.RETURN_IN_PROGRESS
                return new OperateResult(RETURN_APPLIED.getOperate(), RETURN_APPLIED.getOperateDesc());
            case 8: // LocalDeliveryStatusEnum.RETURN_COMPLETED
                return new OperateResult(RETURN_COMPLETED.getOperate(), RETURN_COMPLETED.getOperateDesc());
            default:
                return new OperateResult("", "");
        }
    }

    /**
     * 根据operate获取枚举对象
     * @param operate 操作类型
     * @return LocalDeliveryOperateEnum
     */
    public static LocalDeliveryOperateEnum getByOperate(String operate) {
        for (LocalDeliveryOperateEnum item : LocalDeliveryOperateEnum.values()) {
            if (item.getOperate().equals(operate)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 检查操作类型是否有效
     * @param operate 操作类型
     * @return boolean
     */
    public static boolean isValidOperate(String operate) {
        return getByOperate(operate) != null;
    }

    /**
     * 判断是否为商家操作
     * @param operate 操作类型
     * @return boolean
     */
    public static boolean isStoreOperate(String operate) {
        return ORDER_ACCEPTED.getOperate().equals(operate) ||
               ORDER_CALL.getOperate().equals(operate) ||
               DISPATCH_ORDER.getOperate().equals(operate);
    }

    /**
     * 判断是否为骑手操作
     * @param operate 操作类型
     * @return boolean
     */
    public static boolean isRiderOperate(String operate) {
        return RIDER_ACCEPTED.getOperate().equals(operate) ||
               RIDER_ARRIVED.getOperate().equals(operate) ||
               RIDER_PICKED_UP.getOperate().equals(operate) ||
               ORDER_DELIVERED.getOperate().equals(operate);
    }

    /**
     * 判断是否为取消或退货操作
     * @param operate 操作类型
     * @return boolean
     */
    public static boolean isCancelOrReturnOperate(String operate) {
        return ORDER_CANCELED.getOperate().equals(operate) ||
               RETURN_APPLIED.getOperate().equals(operate) ||
               RETURN_COMPLETED.getOperate().equals(operate);
    }

    /**
     * 获取商家操作Map集合
     * @return Map<String, String>
     */
    public static Map<String, String> getStoreOperateMap() {
        Map<String, String> map = new HashMap<>();
        map.put(ORDER_ACCEPTED.getOperate(), ORDER_ACCEPTED.getOperateDesc());
        map.put(ORDER_CALL.getOperate(), ORDER_CALL.getOperateDesc());
        map.put(DISPATCH_ORDER.getOperate(), DISPATCH_ORDER.getOperateDesc());
        return map;
    }

    /**
     * 获取骑手操作Map集合
     * @return Map<String, String>
     */
    public static Map<String, String> getRiderOperateMap() {
        Map<String, String> map = new HashMap<>();
        map.put(RIDER_ACCEPTED.getOperate(), RIDER_ACCEPTED.getOperateDesc());
        map.put(RIDER_ARRIVED.getOperate(), RIDER_ARRIVED.getOperateDesc());
        map.put(RIDER_PICKED_UP.getOperate(), RIDER_PICKED_UP.getOperateDesc());
        map.put(ORDER_DELIVERED.getOperate(), ORDER_DELIVERED.getOperateDesc());
        return map;
    }
}