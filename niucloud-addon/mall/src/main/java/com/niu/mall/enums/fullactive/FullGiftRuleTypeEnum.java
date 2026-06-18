package com.niu.mall.enums.fullactive;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum FullGiftRuleTypeEnum {
    LADDER("ladder", "实付满N元赠"),
    CYCLE("cycle", "实付每满N元赠");

    private final String type;
    private final String name;

    public static Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        for (FullGiftRuleTypeEnum item : FullGiftRuleTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static String getNameByType(String type) {
        for (FullGiftRuleTypeEnum item : FullGiftRuleTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    public static FullGiftRuleTypeEnum getEnumByType(String type) {
        for (FullGiftRuleTypeEnum item : FullGiftRuleTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}