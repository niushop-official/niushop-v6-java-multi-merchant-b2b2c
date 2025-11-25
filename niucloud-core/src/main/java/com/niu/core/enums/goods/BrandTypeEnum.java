package com.niu.core.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum BrandTypeEnum {
    SITE("店铺", "site"),
    ADMIN("平台", "admin");

    private final String name;

    private final String type;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(BrandTypeEnum item : BrandTypeEnum.values())
        {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过type获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(String type){
        for(BrandTypeEnum item : BrandTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }
}
