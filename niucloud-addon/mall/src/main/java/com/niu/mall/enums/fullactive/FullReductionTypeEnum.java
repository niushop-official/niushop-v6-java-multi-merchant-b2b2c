package com.niu.mall.enums.fullactive;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum FullReductionTypeEnum {
    NUM("num", "满N件"),
    AMOUNT("amount", "满N元");

    private final String type;
    private final String name;

    public static Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        for (FullReductionTypeEnum item : FullReductionTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static String getNameByType(String type) {
        for (FullReductionTypeEnum item : FullReductionTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    public static FullReductionTypeEnum getEnumByType(String type) {
        for (FullReductionTypeEnum item : FullReductionTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}