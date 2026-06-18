package com.niu.mall.enums.fullactive;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum FullReductionMemberTypeEnum {
    ALL("all", "全部会员"),
    LABEL("label", "指定标签会员"),
    LEVEL("level", "指定等级会员");

    private final String type;
    private final String name;

    public static Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        for (FullReductionMemberTypeEnum item : FullReductionMemberTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static String getNameByType(String type) {
        for (FullReductionMemberTypeEnum item : FullReductionMemberTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    public static FullReductionMemberTypeEnum getEnumByType(String type) {
        for (FullReductionMemberTypeEnum item : FullReductionMemberTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}