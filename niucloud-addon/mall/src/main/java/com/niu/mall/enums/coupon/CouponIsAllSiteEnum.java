package com.niu.mall.enums.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum CouponIsAllSiteEnum {
    ALL_SITE_JOIN(0,"所有站点参与"),
    NOT_ALL_SITE_JOIN(1,"指定站点参与");

    private final Integer type;

    private final String name;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(CouponTypeEnum item : CouponTypeEnum.values())
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
    public static String getNameByType(Integer type){
        for(CouponTypeEnum item : CouponTypeEnum.values())
        {
            if(item.getType() == type)
            {
                return item.getName();
            }
        }
        return "";
    }
}
