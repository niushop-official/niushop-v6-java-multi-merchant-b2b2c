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
public enum SeparStatusEnum {
    WAIT("待分账", "wait"),
    SUCCESS("分账成功", "success"),
    FAIL("分账失败", "fail"),
    DEALING("分账进行中", "DEALING"),
    CLOSE("分账取消", "close")
    ;
    private final String name;
    private final String status;

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for(SeparStatusEnum item : SeparStatusEnum.values()){
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    public static String getName(String status){
        return getMap().get(status);
    }
}
