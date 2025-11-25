package com.niu.core.enums.sys;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 核销员类型枚举
 */
public enum VerifierTypeEnum {
    
    MEMBER("member", "会员"),
    SITE("site", "管理员");

    private String type;
    private String name;

    VerifierTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取所有类型Map
     */
    public static Map<String, String> getMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (VerifierTypeEnum item : VerifierTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 根据类型获取名称
     */
    public static String getNameByType(String type) {
        for (VerifierTypeEnum item : VerifierTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }
}

