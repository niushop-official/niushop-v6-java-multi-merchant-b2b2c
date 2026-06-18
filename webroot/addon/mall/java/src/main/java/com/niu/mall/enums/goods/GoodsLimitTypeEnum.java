package com.niu.mall.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum GoodsLimitTypeEnum {
    SINGLE_TIME("启用", 1),
    SINGLE_PERSON("关闭", 0);

    private final String name;

    private final Integer code;

    public static String getNameByCode(Integer status) {
        for (GoodsLimitTypeEnum item : GoodsLimitTypeEnum.values()) {
            if (item.getCode().equals(status)) {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByStatus(Integer status) {
        Map<String, Object> map = new HashMap<>();
        for (GoodsLimitTypeEnum item : GoodsLimitTypeEnum.values()) {
            if (item.getCode().equals(status)) {
                map.put("name", item.getName());
                map.put("code", item.getCode());
            }
        }
        return map;
    }

    public static Map<Integer, Map<String, Object>> getMap() {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        for (GoodsLimitTypeEnum item : GoodsLimitTypeEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("label", item.getName());
            mapItem.put("value", item.getCode());
            map.put(item.getCode(), mapItem);
        }
        return map;
    }

}
