package com.niu.mall.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum CommonActiveEnum {
    DISCOUNT("discount", "限时折扣"),
    FULL_REDUCTION("full_reduction", "满减"),
    FULL_GIFT("full_gift", "满赠"),
    POINT_DEDUCT("point_deduct", "积分抵扣");

    private final String type;
    private final String name;

    public static Object getActiveShort(String active) {
        Map<String, Object> data = new HashMap<>();

        Map<String, Object> discountData = new HashMap<>();
        discountData.put("name", getLang("common_active_short.discount_short"));
        discountData.put("active_name", getLang("common_active_short.discount_name"));
        discountData.put("bg_color", "#FFA322");

        data.put(DISCOUNT.getType(), discountData);

        if (active != null && !active.isEmpty()) {
            return data.get(active);
        } else {
            return data;
        }
    }

    /**
     * 获取语言包文本
     *
     * @param key 语言包key
     * @return 对应的文本
     */
    private static String getLang(String key) {
        switch (key) {
            case "common_active_short.discount_short":
                return "折";
            case "common_active_short.discount_name":
                return "限时折扣";
            default:
                return key;
        }
    }
}
