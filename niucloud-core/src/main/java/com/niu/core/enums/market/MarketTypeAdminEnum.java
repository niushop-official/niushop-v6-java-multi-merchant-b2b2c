package com.niu.core.enums.market;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MarketTypeAdminEnum {

    DISCOUNT("discount", "discount", 30, "/src/addon/mall/views/admin/marketing/components/marketing-type-discount.vue"),
    COUPON("coupon", "优惠券", 30, "/src/addon/mall/views/admin/marketing/components/marketing-type-coupon.vue"),
    FULL_REDUCTION("full_reduction", "满减", 30, "/src/addon/mall/views/admin/marketing/components/marketing-type-full-reduction.vue"),
    FULL_GIFT("full_gift", "满赠", 30, "/src/addon/mall/views/admin/marketing/components/marketing-type-full-gift.vue"),
    SECKILL("seckill", "秒杀", 30, "/src/addon/seckill/views/admin/active/components/marketing-type-seckill.vue"),
    PINTUAN("pintuan", "拼团", 30, "/src/addon/pintuan/views/admin/active/components/marketing-type-pintuan.vue"),
    ;
    private final String key;
    private final String name;
    private final Integer sort;
    private final String component;

    public static Map<String, String> getkeyNameMap() {
        Map<String, String> map = new HashMap<>();
        for (MarketTypeAdminEnum item : MarketTypeAdminEnum.values()) {
            map.put(item.getKey(), item.getName());
        }
        return map;
    }

    public static Map<String, Map<String, Object>> getMap() {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (MarketTypeAdminEnum e : MarketTypeAdminEnum.values()) {
            map.put(e.getKey(),
                    Map.of("key", e.getKey(),
                            "name", e.getName(),
                            "sort", e.getSort(),
                            "component", e.getComponent()));
        }
        return map;
    }
}
