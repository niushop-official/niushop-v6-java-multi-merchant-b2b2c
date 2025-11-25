package com.niu.core.enums.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件状态枚举类
 */
@Getter
@AllArgsConstructor
public enum SeparMainTypeEnum {
    PLAT("平台", "plat"),
    SITE("商户", "site")
    ;
    private final String name;
    private final String type;

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for(SeparMainTypeEnum item : SeparMainTypeEnum.values()){
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static String getName(String type){
        return getMap().get(type);
    }
}
