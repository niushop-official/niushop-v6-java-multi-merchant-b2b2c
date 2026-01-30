package com.niu.core.enums.recharge;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum RechargeOrderLogEnum {
    ADMIN("admin", "平台"),
    MEMBER("member", "会员"),
    SYSTEM("system", "系统");

    private final String type;
    private final String name;

    public static  Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(RechargeOrderLogEnum item : RechargeOrderLogEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     * @param type
     * @return
     */
    public static String getNameByStatus(String type){
        for(RechargeOrderLogEnum item : RechargeOrderLogEnum.values())  {
            if(item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }
}
