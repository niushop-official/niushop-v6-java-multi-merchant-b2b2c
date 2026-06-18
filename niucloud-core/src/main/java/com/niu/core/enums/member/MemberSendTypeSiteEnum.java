package com.niu.core.enums.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MemberSendTypeSiteEnum {

    SEND_COUPON("send_coupon", "赠送优惠券", "/src/addon/mall/views/site/marketing/coupon/components/send-coupon.vue");

    private final String key;
    private final String name;
    private final String component;


    public static Map<String, Map<String, Object>> getMap() {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (MemberSendTypeSiteEnum item : MemberSendTypeSiteEnum.values()) {
            map.put(item.getKey(), Map.of("key", item.getKey(), "name", item.getName(), "component", item.getComponent()));
        }
        return map;
    }

    public static Map<String, String> getEnumMapByKey(String key) {
        return Arrays.stream(MemberSendTypeSiteEnum.values())
                .filter(e -> e.getKey().equals(key))
                .map(e -> Map.of("key", e.getKey(), "name", e.getName(), "component", e.getComponent()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 通过type获取当前名称
     *
     * @param type
     * @return
     */
    public static String getNameByType(String type) {
        for (MemberSendTypeSiteEnum item : MemberSendTypeSiteEnum.values()) {
            if (item.getKey().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

}
