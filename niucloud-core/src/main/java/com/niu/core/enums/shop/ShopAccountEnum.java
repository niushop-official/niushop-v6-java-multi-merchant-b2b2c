package com.niu.core.enums.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ShopAccountEnum {

    ORDER("order", "订单", 1, 0),
    REFUND("refund", "退款", 0, 1),
    COUPON("coupon", "优惠券补贴", 1, 0),
    COUPON_REFUND("coupon_refund", "返还优惠券补贴", 0, 1),
    POINT_PERK("point_perk", "积分补贴", 1, 0),
    POINT_PERK_REFUND("point_perk_refund", "退还积分补贴", 0, 1),
    CASH_OUT("cash_out", "提现", 0, 1),
    SITE_SEPARATE("site_separate", "商户分账", 1, 0),
    SUPPLY_SEPARATE("supply_separate", "商户分账", 1, 0);

    private final String type;

    private final String name;

    //是否增长
    private final Integer inc;

    //是否减少
    private final Integer dec;

    /**
     * 获取枚举map
     *
     * @return
     */
    public static Map<String, Map<String, String>> getMap() {
        Map<String, Map<String, String>> map = new HashMap<>();
        for (ShopAccountEnum item : ShopAccountEnum.values()) {
            Map<String, String> itemMap = new HashMap<>();
            itemMap.put("name", item.getName());
            itemMap.put("type", item.getType());

            map.put(item.getType(), itemMap);
        }
        return map;
    }

    /**
     * 通过type获取当前信息
     *
     * @param type
     * @return
     */
    public static ShopAccountEnum getNameByType(String type) {
        for (ShopAccountEnum item : ShopAccountEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}
