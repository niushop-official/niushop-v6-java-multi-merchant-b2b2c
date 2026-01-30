package com.niu.core.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum BrandPopularEnum {
    HOT(1, "是"),
    NOT_HOT(2, "否");

    private final Integer type;
    private final String name;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(BrandPopularEnum item : BrandPopularEnum.values())
        {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     * @param status
     * @return
     */
    public static String getNameByType(Integer status){
        for(BrandPopularEnum item : BrandPopularEnum.values())
        {
            if(item.getType().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
