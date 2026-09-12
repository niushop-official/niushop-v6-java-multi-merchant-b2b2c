package com.niu.core.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 订单配送相关枚举类
 */
@Getter
@AllArgsConstructor
public enum OrderDeliveryEnum {

    // 配送方式
    EXPRESS("express", "物流配送"),
    LOCAL_DELIVERY("local_delivery", "同城配送"),
    STORE("store", "门店自提"),
    VIRTUAL("virtual", "虚拟发货"),
    NONE_EXPRESS("none_express", "无需快递");

    private final String value;
    private final String name;

    /**
     * 获取配送方式(用于订单)，对齐 PHP OrderDeliveryDict::getType
     * （express / local_delivery / store / virtual，不含 none_express）
     */
    public static Map<String, String> getType() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(OrderDeliveryEnum.EXPRESS.value, OrderDeliveryEnum.EXPRESS.getName());
        map.put(OrderDeliveryEnum.LOCAL_DELIVERY.value, OrderDeliveryEnum.LOCAL_DELIVERY.getName());
        map.put(OrderDeliveryEnum.STORE.value, OrderDeliveryEnum.STORE.getName());
        map.put(OrderDeliveryEnum.VIRTUAL.value, OrderDeliveryEnum.VIRTUAL.getName());
        return map;
    }
}
