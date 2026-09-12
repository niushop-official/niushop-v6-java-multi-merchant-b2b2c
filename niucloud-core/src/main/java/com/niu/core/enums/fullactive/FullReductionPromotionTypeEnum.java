package com.niu.core.enums.fullactive;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum FullReductionPromotionTypeEnum {
    DISCOUNT("discount", "折扣"),
    REDUCTION("reduction", "减价");

    private final String type;
    private final String name;

    public static Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        for (FullReductionPromotionTypeEnum item : FullReductionPromotionTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static String getNameByType(String type) {
        for (FullReductionPromotionTypeEnum item : FullReductionPromotionTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    public static FullReductionPromotionTypeEnum getEnumByType(String type) {
        for (FullReductionPromotionTypeEnum item : FullReductionPromotionTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}