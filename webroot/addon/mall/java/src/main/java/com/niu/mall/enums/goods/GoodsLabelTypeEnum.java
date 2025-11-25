package com.niu.mall.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum GoodsLabelTypeEnum {
    ENABLE("启用", 1),
    CLOSE("关闭", 0);

    private final String name;

    private final Integer status;

    public static String getNameByStatus(Integer status){
        for (GoodsLabelTypeEnum item: GoodsLabelTypeEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByStatus(Integer status){
        Map<String, Object> map = new HashMap<>();
        for (GoodsLabelTypeEnum item: GoodsLabelTypeEnum.values()) {
            if(item.getStatus().equals(status))
            {
                map.put("name", item.getName());
                map.put("status", item.getStatus());
            }
        }
        return map;
    }

    public static Map<Integer, Map<String, Object>> getMap() {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        for (GoodsLabelTypeEnum item : GoodsLabelTypeEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("label", item.getName());
            mapItem.put("value", item.getStatus());
            map.put(item.getStatus(), mapItem);
        }
        return map;
    }
}
