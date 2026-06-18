package com.niu.core.enums.mall_huifu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件状态枚举类
 */
@Getter
@AllArgsConstructor
public enum ParamTypeEnum {
    HUIFU("汇付", "huifu"),
    OF_PUBLIC("对公", "0"),
    PRIVATE_LEGAL_ENTITIES("对私法人", "1"),
    PRIVATE_NON_LEGAL_ENTITIES("对私非法人", "2"),
    ;
    private final String name;
    private final String status;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (ParamTypeEnum item : ParamTypeEnum.values()) {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    public static String getName(String status) {
        return getMap().get(status);
    }
}
