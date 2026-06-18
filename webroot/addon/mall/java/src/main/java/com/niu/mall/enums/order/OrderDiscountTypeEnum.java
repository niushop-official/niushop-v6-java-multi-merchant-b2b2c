package com.niu.mall.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum OrderDiscountTypeEnum {

    COUPON("coupon", "优惠券"),
    POINT_DEDUCT("point_deduct", "积分抵扣"),
    FULL_REDUCTION("full_reduction", "满减"),
    FULL_GIFT("full_gift", "满赠"),
    PLATFORM_COUPON("platform_coupon", "平台优惠券");

    private final String type;

    private final String name;

    public static String getNameByType(String type) {
        return Arrays.stream(OrderDiscountTypeEnum.values())
                .filter(e -> e.getType().equals(type))
                .map(OrderDiscountTypeEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static List<String> getPlatFormDiscountType() {
        return Arrays.asList(PLATFORM_COUPON.getType(), POINT_DEDUCT.getType());

    }

    public static List<String> getShopDiscountType() {
        return Arrays.asList(FULL_REDUCTION.getType(), COUPON.getType());
    }
}
