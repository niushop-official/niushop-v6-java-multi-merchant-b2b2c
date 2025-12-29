package com.niu.core.enums.delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单配送方式枚举类
 */
@Getter
@AllArgsConstructor
public enum OrderDeliveryTypeEnum {
    
    EXPRESS("express", "快递"),
    LOCAL_DELIVERY("local_delivery", "同城配送"),
    STORE("store", "门店自提"),
    NONE_EXPRESS("none_express", "无需快递"),
    VIRTUAL("virtual", "虚拟发货");

    private final String type;
    private final String name;

    /**
     * 获取所有配送方式Map集合
     * @return Map<String, String>
     */
    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (OrderDeliveryTypeEnum item : OrderDeliveryTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 根据type获取配送方式名称
     * @param type 配送方式类型
     * @return 配送方式名称
     */
    public static String getNameByType(String type) {
        for (OrderDeliveryTypeEnum item : OrderDeliveryTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    /**
     * 获取配送子级配送方式
     * @param parentType 父级配送方式类型
     * @return Map<String, String>
     */
    public static Map<String, String> getChildTypeMap(String parentType) {
        Map<String, String> childMap = new HashMap<>();
        
        switch (parentType) {
            case "express":
                childMap.put(EXPRESS.getType(), EXPRESS.getName());
                childMap.put(NONE_EXPRESS.getType(), NONE_EXPRESS.getName());
                break;
            case "local_delivery":
                childMap.put(LOCAL_DELIVERY.getType(), LOCAL_DELIVERY.getName());
                break;
            case "store":
                childMap.put(STORE.getType(), STORE.getName());
                break;
            case "virtual":
                childMap.put(VIRTUAL.getType(), VIRTUAL.getName());
                break;
            default:
                break;
        }
        
        return childMap;
    }

    /**
     * 获取所有配送子级配送方式
     * @return Map<String, Map<String, String>>
     */
    public static Map<String, Map<String, String>> getAllChildTypeMap() {
        Map<String, Map<String, String>> allChildMap = new HashMap<>();
        
        // 快递相关
        Map<String, String> expressChild = new HashMap<>();
        expressChild.put(EXPRESS.getType(), EXPRESS.getName());
        expressChild.put(NONE_EXPRESS.getType(), NONE_EXPRESS.getName());
        allChildMap.put("express", expressChild);
        
        // 同城配送
        Map<String, String> localDeliveryChild = new HashMap<>();
        localDeliveryChild.put(LOCAL_DELIVERY.getType(), LOCAL_DELIVERY.getName());
        allChildMap.put("local_delivery", localDeliveryChild);
        
        // 自提
        Map<String, String> storeChild = new HashMap<>();
        storeChild.put(STORE.getType(), STORE.getName());
        allChildMap.put("store", storeChild);

        // 虚拟发货
        Map<String, String> virtualChild = new HashMap<>();
        virtualChild.put(VIRTUAL.getType(), VIRTUAL.getName());
        allChildMap.put("virtual", virtualChild);
        
        return allChildMap;
    }

    /**
     * 根据type获取枚举对象
     * @param type 配送方式类型
     * @return OrderDeliveryTypeEnum
     */
    public static OrderDeliveryTypeEnum getByType(String type) {
        for (OrderDeliveryTypeEnum item : OrderDeliveryTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}