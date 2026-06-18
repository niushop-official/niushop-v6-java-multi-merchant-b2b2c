package com.niu.mall.enums.fullactive;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum FullGiftMemberTypeEnum {
    ALL("all", "全部会员"),
    LABEL("label", "指定标签会员"),
    LEVEL("level", "指定等级会员");

    private final String type;
    private final String name;

    public static Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        for (FullGiftMemberTypeEnum item : FullGiftMemberTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static String getNameByType(String type) {
        for (FullGiftMemberTypeEnum item : FullGiftMemberTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    public static FullGiftMemberTypeEnum getEnumByType(String type) {
        for (FullGiftMemberTypeEnum item : FullGiftMemberTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}