package com.niu.core.enums.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MemberContentTypeSiteEnum {

    TRADE("order", "消费明细", 100, "/src/addon/mall/views/site/member/components/member-detail-content-order.vue"),
    COUPON("coupon", "优惠券明细", 90, "/src/addon/mall/views/site/member/components/member-detail-content-coupon.vue");

    private final String key;
    private final String name;
    private final Integer sort;
    private final String component;


    public static Map<String, Map<String, Object>> getMap() {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (MemberContentTypeSiteEnum item : MemberContentTypeSiteEnum.values()) {
            map.put(item.getKey(), Map.of("key", item.getKey(), "name", item.getName(), "sort", item.getSort(), "component", item.getComponent()));
        }
        return map;
    }

    /**
     * 通过type获取当前名称
     *
     * @param type
     * @return
     */
    public static String getNameByType(String type) {
        for (MemberContentTypeSiteEnum item : MemberContentTypeSiteEnum.values()) {
            if (item.getKey().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

}
