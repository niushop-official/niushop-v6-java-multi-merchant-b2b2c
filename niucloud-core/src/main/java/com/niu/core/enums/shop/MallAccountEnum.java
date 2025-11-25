package com.niu.core.enums.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MallAccountEnum {

    SERVICE("service", "服务费", 1, 0),
    REFUND("refund", "退款", 0, 1),
    COUPON("coupon", "优惠券补贴", 1, 0),
    COUPON_REFUND("coupon_refund", "返还优惠券补贴", 0, 1);

    private final String type;

    private final String name;

    //是否增长
    private final Integer inc;

    //是否减少
    private final Integer dec;

    /**
     * 获取枚举map
     * @return
     */
    public static Map<String, MallAccountEnum> getMap() {
        Map<String, MallAccountEnum> map = new HashMap<>();
        for(MallAccountEnum item : MallAccountEnum.values())
        {
            map.put(item.getType(), item);
        }
        return map;
    }

    /**
     * 通过type获取当前信息
     * @param type
     * @return
     */
    public static MallAccountEnum getNameByType(String type){
        for(MallAccountEnum item : MallAccountEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item;
            }
        }
        return null;
    }
}
