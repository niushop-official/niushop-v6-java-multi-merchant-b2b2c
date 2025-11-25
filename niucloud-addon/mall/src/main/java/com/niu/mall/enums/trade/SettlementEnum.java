
package com.niu.mall.enums.trade;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SettlementEnum {
    WAIT("待结算", "wait"),
    DEALING("处理中", "dealing"),
    SETTLED("已结算", "settled"),
    FAIL("已结算", "fail");

    private final String name;

    private final String status;

    public static String getNameByStatus(String type){
        for (SettlementEnum item: SettlementEnum.values()) {
            if(item.getStatus().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for (SettlementEnum item: SettlementEnum.values()) {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }
}
