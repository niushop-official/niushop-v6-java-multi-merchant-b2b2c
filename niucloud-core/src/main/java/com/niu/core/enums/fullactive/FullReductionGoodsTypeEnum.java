package com.niu.core.enums.fullactive;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum FullReductionGoodsTypeEnum {
    ALL("all", "全部商品"),
    SELECTED("selected", "指定商品参与"),
    SELECTED_NOT("selected_not", "指定商品不参与");

    private final String type;
    private final String name;

    public static Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        for (FullReductionGoodsTypeEnum item : FullReductionGoodsTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static String getNameByType(String type) {
        for (FullReductionGoodsTypeEnum item : FullReductionGoodsTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    public static FullReductionGoodsTypeEnum getEnumByType(String type) {
        for (FullReductionGoodsTypeEnum item : FullReductionGoodsTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}