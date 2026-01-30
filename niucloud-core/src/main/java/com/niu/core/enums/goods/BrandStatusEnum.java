package com.niu.core.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum BrandStatusEnum {
    ON(1, "显示"),
    OFF(2, "隐藏");

    private final Integer type;
    private final String name;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(BrandStatusEnum item : BrandStatusEnum.values())
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
        for(BrandStatusEnum item : BrandStatusEnum.values())
        {
            if(item.getType().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
