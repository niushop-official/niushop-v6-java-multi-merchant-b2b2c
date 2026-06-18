package com.niu.core.enums.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MemberContentTypeAdminEnum {

    BALANCE("balance", "余额明细", 90, "/src/app/views/member/components/member-detail-content-balance.vue"),
    POINT("point", "积分明细", 80, "/src/app/views/member/components/member-detail-content-point.vue"),
    GROWTH("growth", "成长值明细", 70, "/src/app/views/member/components/member-detail-content-growth.vue"),
    COMMISSION("commission", "佣金明细", 60, "/src/app/views/member/components/member-detail-content-commission.vue"),
    SIGN("sign", "签到记录", 50, "/src/app/views/member/components/member-detail-content-sign.vue"),
    TRADE("trade", "交易明细", 40, "/src/app/views/member/components/member-detail-content-trade.vue"),
    COUPON("coupon", "优惠券明细", 30, "/src/addon/mall/views/admin/member/components/member-detail-content-coupon.vue");

    private final String key;
    private final String name;
    private final Integer sort;
    private final String component;


    public static Map<String, Map<String, Object>> getMap() {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (MemberContentTypeAdminEnum item : MemberContentTypeAdminEnum.values()) {
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
        for (MemberContentTypeAdminEnum item : MemberContentTypeAdminEnum.values()) {
            if (item.getKey().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

}
