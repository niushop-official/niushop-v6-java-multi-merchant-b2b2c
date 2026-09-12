package com.niu.core.enums.fullactive;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum FullReductionRuleTypeEnum {
    LADDER("ladder", "阶梯"),
    CYCLE("cycle", "循环");

    private final String type;
    private final String name;

    public static Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        for (FullReductionRuleTypeEnum item : FullReductionRuleTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static String getNameByType(String type) {
        for (FullReductionRuleTypeEnum item : FullReductionRuleTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    public static FullReductionRuleTypeEnum getEnumByType(String type) {
        for (FullReductionRuleTypeEnum item : FullReductionRuleTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}