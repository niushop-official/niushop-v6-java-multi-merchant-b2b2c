package com.niu.mall.enums.points_mall;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 积分商城商品分类枚举（实物/虚拟）
 */
@Getter
@AllArgsConstructor
public enum PointsMallGoodsCategoryEnum {
    
    REAL("实物商品", "real"),
    VIRTUAL("虚拟商品", "virtual");

    private final String name;
    private final String type;

    /**
     * 通过type获取当前名称
     * @param type 类型
     * @return 名称
     */
    public static String getNameByType(String type) {
        for (PointsMallGoodsCategoryEnum item : PointsMallGoodsCategoryEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }
    public static String getType(String type) {
        for (PointsMallGoodsCategoryEnum item : PointsMallGoodsCategoryEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getType();
            }
        }
        return "";
    }

    /**
     * 根据值获取名称（兼容方法名）
     * @param value 值
     * @return 名称
     */
    public static String getNameByValue(String value) {
        return getNameByType(value);
    }

    /**
     * 根据type获取Map对象
     * @param type 类型
     * @return Map对象
     */
    public static Map<String, Object> getMapByType(String type) {
        Map<String, Object> map = new HashMap<>();
        for (PointsMallGoodsCategoryEnum item : PointsMallGoodsCategoryEnum.values()) {
            if (item.getType().equals(type)) {
                map.put("name", item.getName());
                map.put("type", item.getType());
            }
        }
        return map;
    }

    /**
     * 获取所有枚举的Map结构
     * @return Map
     */
    public static Map<String, Map<String, Object>> getMap() {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (PointsMallGoodsCategoryEnum item : PointsMallGoodsCategoryEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("label", item.getName());
            mapItem.put("value", item.getType());
            map.put(item.getType(), mapItem);
        }
        return map;
    }

}
